package net.fender.pogo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pokestop;
    private String task;
    private String reward;
    private double latitude;
    private double longitude;
    private LocalDate reportedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPokestop() {
        return pokestop;
    }

    public void setPokestop(String pokestop) {
        this.pokestop = pokestop;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
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

    public LocalDate getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDate reportedAt) {
        this.reportedAt = reportedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report that = (Report) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this);
    }
}
