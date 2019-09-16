package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Cup {

    private String name;
    private String title;
    private int restrictedPicks;
    private List<Filter> include = new ArrayList<>();
    private List<Filter> exclude = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRestrictedPicks() {
        return restrictedPicks;
    }

    public void setRestrictedPicks(int restrictedPicks) {
        this.restrictedPicks = restrictedPicks;
    }

    public List<Filter> getInclude() {
        return include;
    }

    public void setInclude(List<Filter> include) {
        this.include = include;
    }

    public List<Filter> getExclude() {
        return exclude;
    }

    public void setExclude(List<Filter> exclude) {
        this.exclude = exclude;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
