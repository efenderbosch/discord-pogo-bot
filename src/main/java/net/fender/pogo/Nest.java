package net.fender.pogo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Nest {

    @Id
    private String name;
    private double latitude;
    private double longitude;
    private String pokemon;
    private String reportedBy;
    private ZonedDateTime reportedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPokemon() {
        return pokemon;
    }

    public void setPokemon(String pokemon) {
        this.pokemon = pokemon;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public ZonedDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(ZonedDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nest nest = (Nest) o;
        return Objects.equals(name, nest.name);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
