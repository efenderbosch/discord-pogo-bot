package net.fender.discord.quartz;

import org.geojson.FeatureCollection;

public class SilphResponse {

    private FeatureCollection upcomingTournaments;

    public FeatureCollection getUpcomingTournaments() {
        return upcomingTournaments;
    }

    public void setUpcomingTournaments(FeatureCollection upcomingTournaments) {
        this.upcomingTournaments = upcomingTournaments;
    }
}
