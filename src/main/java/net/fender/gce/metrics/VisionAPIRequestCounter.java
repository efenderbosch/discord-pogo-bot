package net.fender.gce.metrics;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListTimeSeriesPagedResponse;
import com.google.monitoring.v3.*;
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

import static com.google.monitoring.v3.Aggregation.Aligner.ALIGN_SUM;
import static com.google.monitoring.v3.Aggregation.Reducer.REDUCE_SUM;
import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class VisionAPIRequestCounter {

    private static final Logger LOG = LoggerFactory.getLogger(VisionAPIRequestCounter.class);

    private static final String PROJECT_NAME = ProjectName.of("fendersapp").toString();
    private static final Duration ONE_DAY = Duration.newBuilder().setSeconds(86400).build();
    private static final Aggregation AGGREGATION = Aggregation.newBuilder().
            setAlignmentPeriod(ONE_DAY).
            setCrossSeriesReducer(REDUCE_SUM).
            setPerSeriesAligner(ALIGN_SUM).
            build();

    private static final String FILTER = "metric.type = \"serviceruntime.googleapis.com/api/request_count\" " +
            "AND resource.labels.service = \"vision.googleapis.com\"";

    public long count() {
        try (MetricServiceClient metricServiceClient = MetricServiceClient.create()) {
            return count(metricServiceClient);
        } catch (IOException e) {
            LOG.error("exception with MetricServiceClient", e);
        }
        return 0;
    }

    private long count(MetricServiceClient metricServiceClient) {
        TimeInterval interval = buildInterval();
        // fields timeSeries/points/value/int64Value
        ListTimeSeriesRequest request = ListTimeSeriesRequest.newBuilder().
                setName(PROJECT_NAME).
                setAggregation(AGGREGATION).
                setFilter(FILTER).
                setInterval(interval).
                build();
        ListTimeSeriesPagedResponse response = metricServiceClient.listTimeSeries(request);
        long count = 0;
        for (TimeSeries timeSeries : response.iterateAll()) {
            for (Point point : timeSeries.getPointsList()) {
                count += point.getValue().getInt64Value();
            }
        }
        LOG.info("GCE Vision API usage: {}", count);
        return count;
    }

    private static TimeInterval buildInterval() {
        Instant now = Instant.now();
        Instant oneMonthAgo = now.minus(30, DAYS);
        Timestamp start = Timestamps.fromMillis(oneMonthAgo.toEpochMilli());
        Timestamp end = Timestamps.fromMillis(now.toEpochMilli());
        return TimeInterval.newBuilder().setStartTime(start).setEndTime(end).build();
    }

}
