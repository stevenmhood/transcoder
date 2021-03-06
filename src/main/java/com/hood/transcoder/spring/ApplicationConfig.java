package com.hood.transcoder.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.hood.transcoder.application.TranscoderApplication;
import com.hood.transcoder.domain.movie.MovieRepository;
import com.hood.transcoder.domain.transcoding.TranscodingJobRepository;
import com.hood.transcoder.domain.transcoding.TranscodingService;
import com.hood.transcoder.infrastructure.TranscodeNotificationListener;
import com.hood.transcoder.persistence.DynamoDBTranscodingJobRepository;
import com.hood.transcoder.persistence.S3MovieRepository;

@Configuration
class ApplicationConfig
{
    @Bean
    TranscoderApplication transcoderApplication()
    {
        return new TranscoderApplication( this.movieRepository(),
                                          this.transcodingJobRepository(),
                                          this.transcodingService() );
    }

    @Bean
    MovieRepository movieRepository()
    {
        return new S3MovieRepository( this.amazonTransferManager() );
    }

    @Bean
    TranscodingJobRepository transcodingJobRepository()
    {
        return new DynamoDBTranscodingJobRepository( this.amazonDynamoDBClient() );
    }

    @Bean
    TranscodingService transcodingService()
    {
        return new TranscodingService( this.amazonElasticTranscoderClient() );
    }

    @Bean( destroyMethod = "shutdown" )
    TranscodeNotificationListener transcodeNotificationListener()
    {
        return new TranscodeNotificationListener( this.amazonSQSClient(),
                                                  "https://sqs.us-west-2.amazonaws.com/382431199127/hood-ets-messages",
                                                  this.transcoderApplication() );
    }

    @Bean( destroyMethod = "shutdownNow" )
    TransferManager amazonTransferManager()
    {
        return new TransferManager( this.amazonS3Client() );
    }

    @Bean( destroyMethod = "shutdown" )
    AmazonS3 amazonS3Client()
    {
        return this.awsRegion().createClient( AmazonS3Client.class, this.awsCredentials(), new ClientConfiguration() );
    }

    @Bean( destroyMethod = "shutdown" )
    AmazonElasticTranscoder amazonElasticTranscoderClient()
    {
        return this.awsRegion().createClient( AmazonElasticTranscoderClient.class,
                                              this.awsCredentials(),
                                              new ClientConfiguration() );
    }

    @Bean( destroyMethod = "shutdown" )
    AmazonSQS amazonSQSClient()
    {
        return this.awsRegion().createClient( AmazonSQSClient.class, this.awsCredentials(), new ClientConfiguration() );
    }

    @Bean( destroyMethod = "shutdown" )
    AmazonDynamoDB amazonDynamoDBClient()
    {
        return this.awsRegion().createClient( AmazonDynamoDBClient.class,
                                              this.awsCredentials(),
                                              new ClientConfiguration() );
    }

    @Bean
    AWSCredentialsProvider awsCredentials()
    {
        return new ClasspathPropertiesFileCredentialsProvider( "aws-credentials.properties" );
    }

    private Region awsRegion()
    {
        return Region.getRegion( Regions.US_WEST_2 );
    }
}
