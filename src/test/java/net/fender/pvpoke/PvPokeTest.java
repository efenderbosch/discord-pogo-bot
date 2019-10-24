package net.fender.pvpoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class PvPokeTest {

    @Test
    public void test() throws IOException {
        InputStream rankingsIs = PvPokeTest.class.getClassLoader().getResourceAsStream("rankings-1500.json");
        InputStream gameMasterIs = PvPokeTest.class.getClassLoader().getResourceAsStream("gamemaster.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);

        GameMaster gameMaster = mapper.readValue(gameMasterIs, GameMaster.class);
        List<Pokemon> allPokemon = gameMaster.getPokemon();
        Map<String, Pokemon> pokemonBySpeciesId = allPokemon.stream().collect(toMap(Pokemon::getSpeciesId, identity()));
        List<Move> allMoves = gameMaster.getMoves();
        Map<String, Move> movesById = allMoves.stream().collect(toMap(Move::getMoveId, identity()));

        Set<String> search = new HashSet<>();
        search.add("dark");
        search.add("ghost");

        Set<String> types = new HashSet<>();
//        types.add("ghost");
        types.add("psychic");
        types.add("steel");
        types.add("fighting");

        ArrayNode root = (ArrayNode) mapper.readTree(rankingsIs);
        for (int i = 0; i < root.size(); i++) {
            boolean print = false;
            StringBuilder temp = new StringBuilder();
            ObjectNode node = (ObjectNode) root.get(i);

            String speciesName = node.get("speciesName").textValue();
            temp.append(speciesName).append(' ');

            String speciesId = node.get("speciesId").textValue();
            Pokemon pokemon = pokemonBySpeciesId.get(speciesId);
            if (Sets.intersection(types, pokemon.getTypes()).isEmpty()) continue;

            // TODO print types
            temp.append(pokemon.getTypes().stream().collect(Collectors.joining(", ", "(", ")")));
            temp.append(' ');

            JsonNode moves = node.get("moves");
            Set<String> legacyMoves = pokemon.getLegacyMoves();
            ArrayNode fastMoves = (ArrayNode) moves.get("fastMoves");
            temp.append("F: [");
            for (int f = 0; f < fastMoves.size(); f++) {
                String fastMoveId = fastMoves.get(f).get("moveId").textValue();
                Move fastMove = movesById.get(fastMoveId);
                if (search.contains(fastMove.getType())) {
                    print = true;
                }
                temp.append(fastMove.getName());
                if (legacyMoves.contains(fastMoveId)) {
                    temp.append("(L)");
                }
                temp.append(", ");
            }
            temp.append("] C: [");
            ArrayNode chargedMoves = (ArrayNode) moves.get("chargedMoves");
            for (int c = 0; c < chargedMoves.size(); c++) {
                String chargeMoveId = chargedMoves.get(c).get("moveId").textValue();
                Move chargeMove = movesById.get(chargeMoveId);
                if (search.contains(chargeMove.getType())) {
                    print = true;
                }
                temp.append(chargeMove.getName());
                if (legacyMoves.contains(chargeMoveId)) {
                    temp.append("(L)");
                }
                temp.append(", ");
            }
            temp.append("]");
            if (print) System.out.println(temp);
        }
    }
}
