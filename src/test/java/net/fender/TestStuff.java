package net.fender;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import net.fender.pogo.BaseStats;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.RestPokeAPI;
import net.fender.pogo.StatProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TestStuff {

    @Test
    public void test_api() {
        RestPokeAPI restPokeAPI = new RestPokeAPI();
        Pokemon raticateAlola = restPokeAPI.getPokemon("raticate-alola");
        System.out.println(raticateAlola);

        PokeApi pokeApi = new PokeApiClient();
        Pokemon bulbasaur = pokeApi.getPokemon(1);
        BaseStats baseStats = new BaseStats(bulbasaur);
//        List<PokemonStat> stats = bulbasaur.getStats();
//        int physicalAttack = 100;
//        int physicalDefense = 100;
//        int speed = 75;
//        int msgHp = 100;
//        int specialAttack = 100;
//        int specialDefense = 100;
//        for (PokemonStat stat : stats) {
//            switch (stat.getStat().getName()) {
//                case "attack":
//                    physicalAttack = stat.getBaseStat();
//                    break;
//                case "defense":
//                    physicalDefense = stat.getBaseStat();
//                    break;
//                case "speed":
//                    speed = stat.getBaseStat();
//                    break;
//                case "hp":
//                    msgHp = stat.getBaseStat();
//                    break;
//                case "special-attack":
//                    specialAttack = stat.getBaseStat();
//                    break;
//                case "special-defense":
//                    specialDefense = stat.getBaseStat();
//                    break;
//            }
//        }
//
//        BigDecimal FIVE_HUNDRED = new BigDecimal("500.00");
//        BigDecimal speedModifier = BigDecimal.valueOf(speed - 75).
//                divide(FIVE_HUNDRED).
//                add(BigDecimal.ONE);
//        System.out.println(speedModifier);
//
//        BigDecimal SEVEN_EIGHTHS = BigDecimal.valueOf(7).divide(BigDecimal.valueOf(8));
//        BigDecimal ONE_EIGHTH = BigDecimal.ONE.divide(BigDecimal.valueOf(8));
//        BigDecimal FIVE_EIGHTS = BigDecimal.valueOf(5).divide(BigDecimal.valueOf(8));
//        BigDecimal THREE_EIGHTS = BigDecimal.valueOf(3).divide(BigDecimal.valueOf(8));
//
//        BigDecimal minAttack = BigDecimal.valueOf(Math.min(physicalAttack, specialAttack));
//        BigDecimal maxAttack = BigDecimal.valueOf(Math.max(physicalAttack, specialAttack));
//        BigDecimal attack = SEVEN_EIGHTHS.multiply(maxAttack).
//                add(ONE_EIGHTH.multiply(minAttack)).
//                multiply(BigDecimal.valueOf(2)).
//                multiply(speedModifier).
//                setScale(0, HALF_UP);
//        System.out.println(attack);
//
//        BigDecimal minDefense = BigDecimal.valueOf(Math.min(physicalDefense, specialDefense));
//        BigDecimal maxDefense = BigDecimal.valueOf(Math.max(physicalDefense, specialDefense));
//        BigDecimal defense = FIVE_EIGHTS.multiply(maxDefense).
//                add(THREE_EIGHTS.multiply(minDefense)).
//                multiply(BigDecimal.valueOf(2)).multiply(speedModifier).
//                setScale(0, HALF_UP);
//        System.out.println(defense);
//
//        BigDecimal stamina = new BigDecimal("1.75").
//                multiply(BigDecimal.valueOf(msgHp)).
//                add(BigDecimal.valueOf(50)).
//                setScale(0, HALF_UP);
//        System.out.println(stamina);

        // find level for 0/0/0
        BigDecimal attack = baseStats.getAttack();
        BigDecimal defense = baseStats.getDefense();
        BigDecimal stamina = baseStats.getStamina();
        double zeroLevel = 0.0;
        for (double level = 1.0; level <= 40.0; level += 0.5) {
            StatProduct statProduct = new StatProduct(baseStats, IndividualValues.ZERO, level);
            int cp = statProduct.getCp();
            if (cp <= 1500) {
                zeroLevel = level;
                System.out.println(statProduct);
            }
        }
        System.out.println(zeroLevel);

        // find level for 15/15/15
        double perfectLevel = 0.0;
        for (double level = 1.0; level <= 40.0; level += 0.5) {
            StatProduct statProduct = new StatProduct(baseStats, IndividualValues.PERFECT, level);
            int cp = statProduct.getCp();
            if (cp <= 1500) {
                perfectLevel = level;
                System.out.println(statProduct);
            }
        }
        System.out.println(perfectLevel);
    }
}
