package net.fender.pogo;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestPokeAPI implements PokeAPI {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Pokemon getPokemon(String name) {
        ResponseEntity<Pokemon> responseEntity = restTemplate.getForEntity("https://pokeapi.co/api/v2/pokemon/" + name, Pokemon.class);
        return responseEntity.getBody();
    }
}
