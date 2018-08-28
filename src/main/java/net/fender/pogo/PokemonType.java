package net.fender.pogo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class PokemonType {

    private final String name;
    private final List<PokemonType> strongVs = new ArrayList<>();
    private final List<PokemonType> weakVs = new ArrayList<>();

    public PokemonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<PokemonType> getStrongVs() {
        return strongVs;
    }

    public void addStrongVs(PokemonType strongVs) {
        this.strongVs.add(strongVs);
    }

    public List<PokemonType> getWeakVs() {
        return weakVs;
    }

    public void addWeakVs(PokemonType weakVs) {
        this.weakVs.add(weakVs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonType that = (PokemonType) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("name", name);
        List<String> strongNames = strongVs.stream().map(s -> s.getName()).collect(toList());
        builder.append("strongVs", strongNames);
        List<String> weakNames = weakVs.stream().map(w -> w.getName()).collect(toList());
        builder.append("weakVs", weakNames);
        return builder.build();
    }
}
