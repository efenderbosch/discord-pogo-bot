package net.fender.aws;

//@Configuration
public class AwsConfiguration {

//    @Bean
//    @Profile("local")
//    public AWSCredentialsProvider staticCredentialsProvider(@Value("${aws.access-key}") String accessKey,
//                                                            @Value("${aws.secret-key}") String secretKey) {
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
//        return new AWSStaticCredentialsProvider(awsCredentials);
//    }
//
//    @Bean
//    @Profile("!local")
//    public AWSCredentialsProvider credentialsProvider() {
//        return DefaultAWSCredentialsProviderChain.getInstance();
//    }
//
//    @Bean
//    public AmazonRekognition rekognition(AWSCredentialsProvider credentialsProvider) {
//        return AmazonRekognitionClient.builder().withCredentials(credentialsProvider).build();
//    }
//
//    @Bean
//    public AmazonS3 s3(AWSCredentialsProvider credentialsProvider) {
//        return AmazonS3Client.builder().withCredentials(credentialsProvider).build();
//    }
}
