package net.fender.pvpoke;

import java.util.ArrayList;
import java.util.List;

public class Cup {

    private String name;
    private String title;
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
}
