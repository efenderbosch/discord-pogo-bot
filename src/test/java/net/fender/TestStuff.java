package net.fender;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import net.fender.pogo.BaseStats;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.StatProduct;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class TestStuff {

    @Disabled
    public void test_base_stats() {
        PokeApi pokeApi = new PokeApiClient();
        for (int i = 1; i < 10; i++) {
            Pokemon pokemon = pokeApi.getPokemon(i);
            BaseStats baseStats = new BaseStats(pokemon);
            System.out.println(pokemon.getName() + " " + baseStats.getAttack() + " " + baseStats.getDefense() + " " + baseStats.getStamina());

            Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, League.GREAT);
            SortedSet<StatProduct> sortedStatProducts = new TreeSet<>();
            sortedStatProducts.addAll(statProducts.values());
            sortedStatProducts.stream().findFirst().ifPresent(System.out::println);
//            top -> {}
//                    System.out.println(top.getLevel() + " " + top.getIvs().getAttack() + "/" +
//                            top.getIvs().getDefense() + "/" + top.getIvs().getStamina() + " " + top.getCp() + " " +
//                            StatProduct.round(top.getLevelAttack()) + " " + StatProduct.round(top.getLevelDefense()) +
//                            " " + top.getHp() + " " + top.getStatProduct()));
            System.out.println("---------------------");
        }
    }

    @Test
    public void test_api() {
        PokeApi pokeApi = new PokeApiClient();
        Pokemon pokemon = pokeApi.getPokemon("lapras");
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);

        IndividualValues ivs = new IndividualValues(6, 7, 12);
        StatProduct statProduct = stats.get(ivs);
        System.out.println(statProduct);

        SortedSet<StatProduct> betterStats = stats.values().stream().
                filter(s -> s.getStatProduct() >= statProduct.getStatProduct()).
                collect(Collectors.toCollection(TreeSet::new));
        int rank = betterStats.size();
        System.out.println("rank: " + rank);
        StatProduct bestStats = betterStats.first();
        System.out.println(bestStats);

        List<StatProduct> bestFriends = betterStats.stream().filter(StatProduct::isBestFriend).collect(toList());
        double odds = Math.round(1000.0 * bestFriends.size() / 1331) / 10.0;

    }
}
