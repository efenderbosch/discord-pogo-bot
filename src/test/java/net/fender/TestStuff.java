package net.fender;

import org.junit.jupiter.api.Test;

//@ActiveProfiles("aws-credentials")
//@SpringBootTest(classes = {AwsConfiguration.class, DiscordConfiguration.class, JacksonAutoConfiguration.class},
//        webEnvironment = NONE)
public class TestStuff {

//    @Autowired
//    JDA jda;

    @Test
    public void test_api() {
//        PokeApi pokeApi = new PokeApiClient();
//        Pokemon pokemon = pokeApi.getPokemon("raticate-alola");
//        System.out.println(pokemon.getName());
//        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);
//
//        IndividualValues ivs = new IndividualValues(10, 11, 11);
//
//        StatProduct statProduct = stats.get(ivs);
//
//        SortedSet<StatProduct> betterStats = stats.values().stream().
//                filter(s -> s.getStatProduct() >= statProduct.getStatProduct()).
//                collect(Collectors.toCollection(TreeSet::new));
//        int rank = betterStats.size();
//        StatProduct bestStats = betterStats.first();
//
//        List<StatProduct> bestFriends = betterStats.stream().filter(StatProduct::isBestFriend).collect(toList());
//        double odds = Math.round(1000.0 * bestFriends.size() / 1331) / 10.0;
//
//        TextChannel rankBot = jda.getTextChannelsByName("rank-bot", true).get(0);
//
//        EmbedBuilder embedBuilder = new EmbedBuilder();
//        embedBuilder.setThumbnail(pokemon.getSprites().getFrontDefault());
//        embedBuilder.setTitle(pokemon.getName());
//        String ivDesc = ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina();
//        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStats.getStatProduct()) / 10.0;
//        String desc = "#" + rank + " | L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
//                percentBest + "%";
//        embedBuilder.addField(ivDesc, desc, false);
//
//        String bestDesc = "L" + bestStats.getLevel() + " | CP " + bestStats.getCp() + " | " +
//                bestStats.getIvs().getAttack() + "/" + bestStats.getIvs().getDefense() + "/" +
//                bestStats.getIvs().getStamina();
//        embedBuilder.addField("#1", bestDesc, false);
//        embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
//
//        MessageBuilder builder = new MessageBuilder();
//        builder.setEmbed(embedBuilder.build());
//        Message message = builder.build();
//        rankBot.sendMessage(message).submit();
    }
}
