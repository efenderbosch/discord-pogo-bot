package net.fender.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ssm.SsmClient;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

@Configuration
public class AwsConfiguration {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(Environment env) {
        String accessKeyId = env.getProperty("aws.access-key-id");
        String secretAccessKey = env.getProperty("aws.secret-key");
        if (accessKeyId != null && secretAccessKey != null) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            return StaticCredentialsProvider.create(credentials);
        }

        String profileName = env.getProperty("aws.profile-name");
        if (profileName != null) {
            return ProfileCredentialsProvider.create(profileName);
        }

        return DefaultCredentialsProvider.create();
    }

    @Bean
    public RekognitionClient rekognitionClient(AwsCredentialsProvider credentialsProvider) {
        return RekognitionClient.builder().credentialsProvider(credentialsProvider).build();
    }

    @Bean
    public SsmClient ssmClient(AwsCredentialsProvider credentialsProvider) {
        return SsmClient.builder().credentialsProvider(credentialsProvider).build();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder().credentialsProvider(credentialsProvider).region(US_EAST_1).build();
    }

}
