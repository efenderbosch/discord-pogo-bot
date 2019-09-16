package net.fender.discord.quartz;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
public class SilphJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(SilphJob.class);

    @Autowired
    private JDA jda;

    @Autowired
    private ObjectMapper objectMapper;

    public SilphJob() { }

    SilphJob(JDA jda, ObjectMapper objectMapper) {
        this.jda = jda;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        new DateTimeFormatterBuilder();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://silph.gg/pages/getLocalTournaments.json");
            List<NameValuePair> formparams = new ArrayList<>();
            formparams.add(new BasicNameValuePair("lat1", "41.83287959053783"));
            formparams.add(new BasicNameValuePair("lng1", " -82.75461610091742"));
            formparams.add(new BasicNameValuePair("lat2", "40.5106483671473"));
            formparams.add(new BasicNameValuePair("lng2", "-80.4467838990867"));
            formparams.add(new BasicNameValuePair("zoom", "8.7"));
            formparams.add(new BasicNameValuePair("center_lat", "41.1751"));
            formparams.add(new BasicNameValuePair("center_lng", "-81.6007"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, StandardCharsets.UTF_8);
            post.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            SilphResponse silphResponse = objectMapper.readValue(responseEntity.getContent(), SilphResponse.class);
            FeatureCollection upcomingTournaments = silphResponse.getUpcomingTournaments();

            TextChannel pvpGeneral = jda.getTextChannelsByName("pvp-general", true).get(0);

            List<Feature> features = upcomingTournaments.getFeatures();

            for (Feature feature : features) {
                Point point = (Point) feature.getGeometry();
                LngLatAlt lngLatAlt = point.getCoordinates();
                double latitude = lngLatAlt.getLatitude();
                double longitude = lngLatAlt.getLongitude();

                Map<String, Object> props = feature.getProperties();
                Tournament tournament = objectMapper.convertValue(props, Tournament.class);
                tournament.setLatitude(latitude);
                tournament.setLongitude(longitude);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(tournament.getTitle(), "https://silph.gg/t/" + tournament.getSlug());
                embedBuilder.setAuthor(tournament.getCommunity());
                embedBuilder.setThumbnail(tournament.getCommunityLogo());
                embedBuilder.addField("Address", tournament.getAddress(), false);
                embedBuilder.addField("Start Time", tournament.getStartTime().atZone(ZoneId.of("America/New_York")).format(DateTimeFormatter.RFC_1123_DATE_TIME), false);

                MessageBuilder builder = new MessageBuilder();
                builder.setEmbed(embedBuilder.build());
                Message message = builder.build();
                message = pvpGeneral.sendMessage(message).complete();
                // TODO pin/unpin
            }
        } catch (IOException e) {
            LOG.error("error fetching nearby tournaments", e);
        }
    }
}
