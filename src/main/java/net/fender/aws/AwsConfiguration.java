package net.fender.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ssm.SsmClient;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

@Configuration
public class AwsConfiguration {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${aws.access.key.id}") String accessKeyId,
            @Value("${aws.secret.access.key}") String secretAccessKey) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return StaticCredentialsProvider.create(credentials);
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
