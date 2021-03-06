package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class Filter {

    private FilterType filterType;
    private List<Object> values;

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
