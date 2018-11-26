package net.fender.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liqp.Template;
import net.fender.pogo.Report;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringJUnitConfig
public class TemplateTest {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateTest.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void test_render() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        Resource resource = resourceLoader.getResource("classpath:geojson.liquid");
        File file = resource.getFile();
        Template template = Template.parse(file);

        Report report = new Report();
        report.setPokestop("pokestop name goes here");
        report.setTask("task goes here");
        report.setReward("reward goes here");
        report.setLatitude(41.101655);
        report.setLongitude(-81.583875);

        List<Report> reports = Collections.singletonList(report);
        ArrayNode reportsNode = objectMapper.convertValue(reports, ArrayNode.class);
        ObjectNode root = objectMapper.createObjectNode();
        root.putArray("reports").addAll(reportsNode);
        String input = root.toString();
        LOG.info("input: {}", input);
        String json = template.render(input);
        LOG.info("json: {}", json);
    }
}
