package net.fender.pogo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pvpoke.BaseStats;
import net.fender.pvpoke.GameMaster;
import net.fender.pvpoke.Pokemon;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.SimilarityScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@Component
public class PokemonRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonRegistry.class);

    private static final SimilarityScore<Double> SIMILARITY_SCORE = new JaroWinklerSimilarity();
    private final Map<String, Pokemon> pokemonByName = new HashMap<>();
    //private final Map<Integer, Pokemon> pokemonByDex = new HashMap<>();

    @Autowired
    public PokemonRegistry(ObjectMapper mapper, ResourceLoader resourceLoader) throws IOException {
        mapper.setDefaultMergeable(true);
        Resource gameMasterResource = resourceLoader.getResource("classpath:gamemaster.json");
        LOG.info("loading gamemaster.json");
        GameMaster gameMaster = mapper.readValue(gameMasterResource.getInputStream(), GameMaster.class);
        LOG.info("filtering forms with duplicate base stats");
        Map<Integer, List<Pokemon>> dexById = gameMaster.getPokemon().stream().collect(groupingBy(Pokemon::getDex));

        // filter multiple forms w/ the same base stats down to one
        for (Map.Entry<Integer, List<Pokemon>> entry : dexById.entrySet()) {
            List<Pokemon> forms = entry.getValue();
            if (forms.size() != 1) {
                Set<BaseStats> allStats = forms.stream().map(Pokemon::getBaseStats).collect(toSet());
                if (allStats.size() == 1) {
                    // collapse all forms to one form
                    Pokemon mainForm = forms.get(0);
                    //LOG.info("filtering dupes for {}", mainForm.getSpeciesId());
                    String speciesIdWithForm = mainForm.getSpeciesId();
                    String speciesIdWithoutForm = StringUtils.substringBefore(speciesIdWithForm, "_");
                    mainForm.setSpeciesId(speciesIdWithoutForm);
                    forms.stream().map(Pokemon::getSpeciesId).
                            filter(Predicate.not(Predicate.isEqual(speciesIdWithoutForm))).
                            forEach(speciesId -> LOG.info("replacing {} with {}", speciesId, speciesIdWithoutForm));
                    pokemonByName.put(speciesIdWithoutForm, mainForm);
                } else {
                    forms.forEach(form -> pokemonByName.put(form.getSpeciesId(), form));
                }
            } else {
                Pokemon pokemon = forms.get(0);
                pokemon.setSpeciesId(pokemon.getSpeciesId().replace('_', '-'));
                pokemonByName.put(pokemon.getSpeciesId(), pokemon);
            }
        }

        LOG.info("merging custom.json");
        dexById = pokemonByName.values().stream().collect(groupingBy(Pokemon::getDex));
        IgnoreNullBeanUtilsBean ignoreNullBeanUtilsBean = new IgnoreNullBeanUtilsBean();
        Resource customResource = resourceLoader.getResource("classpath:custom.json");
        GameMaster custom = mapper.readValue(customResource.getInputStream(), GameMaster.class);
        for (Pokemon pokemon : custom.getPokemon()) {
            List<Pokemon> forms = dexById.get(pokemon.getDex());
            if (forms == null) {
                pokemonByName.put(pokemon.getSpeciesId(), pokemon);
                continue;
            }
            for (Pokemon form : forms) {
                try {
                    ignoreNullBeanUtilsBean.copyProperties(form, pokemon);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }

        LOG.info("loaded {} pokemon", pokemonByName.size());
    }

    public Pokemon getPokeman(String name) {
        return pokemonByName.get(name);
    }

    public Collection<Pokemon> getAll() {
        return pokemonByName.values();
    }

    public Map<String, Pokemon> getAllByPokemonName() {
        return pokemonByName;
    }

    public List<String> find(String search) {
        NavigableMap<Double, List<String>> scores = new TreeMap<>(Comparator.reverseOrder());
        for (Pokemon pokemon : pokemonByName.values()) {
            String pokemonName = pokemon.getSpeciesId();
            Double score = SIMILARITY_SCORE.apply(pokemonName, search);
            List<String> withScore = scores.get(score);
            if (withScore == null) {
                withScore = new ArrayList<>();
                scores.put(score, withScore);
            }
            withScore.add(pokemonName);
        }
        return scores.headMap(0.85).values().stream().flatMap(Collection::stream).limit(9).collect(toList());
    }
}
