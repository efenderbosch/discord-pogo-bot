package net.fender.pogo;

import net.fender.pvpoke.Pokemon;

import java.util.Objects;

public class PokemonStatProduct implements Comparable<PokemonStatProduct> {

    private final Pokemon pokemon;
    private final StatProduct statProduct;

    public PokemonStatProduct(Pokemon pokemon, StatProduct statProduct) {
        this.pokemon = pokemon;
        this.statProduct = statProduct;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public StatProduct getStatProduct() {
        return statProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonStatProduct that = (PokemonStatProduct) o;
        return Objects.equals(pokemon, that.pokemon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pokemon);
    }

    @Override
    public int compareTo(PokemonStatProduct o) {
        return this.statProduct.compareTo(o.statProduct);
    }
}
