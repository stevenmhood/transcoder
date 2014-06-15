package com.hood.transcoder.persistence;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.google.common.collect.ImmutableMap;
import com.hood.transcoder.domain.TranscodingJob;
import com.hood.transcoder.domain.TranscodingJobRepository;

public class DynamoDBTranscodingJobRepository implements TranscodingJobRepository
{
    private static final Logger logger = LoggerFactory.getLogger( DynamoDBTranscodingJobRepository.class );

    private final static String TABLE_NAME = "transcode-jobs";

    private final AmazonDynamoDB dynamoDB;

    public DynamoDBTranscodingJobRepository( final AmazonDynamoDB dynamoDB )
    {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public TranscodingJob get( final String jobId )
    {
        final ImmutableMap.Builder<String, AttributeValue> keyBuilder = ImmutableMap.builder();
        keyBuilder.put( "transcode-job-id", new AttributeValue( jobId ) );

        try
        {
            final GetItemResult result = this.dynamoDB.getItem( TABLE_NAME, keyBuilder.build() );
            return new TranscodingJob( result.getItem().get( "transcode-job-id" ).getS(), Paths.get( result.getItem()
                    .get( "inputMovie" ).getS() ), Paths.get( result.getItem().get( "outputMovie" ).getS() ) );
        }
        catch ( final AmazonServiceException e )
        {
            logger.error( "AmazonServiceException while getting {}.  RID: {}", jobId, e.getRequestId(), e );
            throw e;
        }
        catch ( final AmazonClientException e )
        {
            logger.error( "AmazonClientException while getting {}.", jobId, e );
            throw e;
        }
    }

    @Override
    public void store( final TranscodingJob transcodingJob )
    {
        final ImmutableMap.Builder<String, AttributeValue> itemBuilder = ImmutableMap.builder();
        itemBuilder.put( "transcode-job-id", new AttributeValue( transcodingJob.getId() ) );
        itemBuilder.put( "inputMovie", new AttributeValue( transcodingJob.getInputPath().toString() ) );
        itemBuilder.put( "outputMovie", new AttributeValue( transcodingJob.getOutputPath().toString() ) );

        try
        {
            this.dynamoDB.putItem( TABLE_NAME, itemBuilder.build() );
        }
        catch ( final AmazonServiceException e )
        {
            logger.error( "AmazonServiceException while storing {}.  RID: {}", transcodingJob, e.getRequestId(), e );
        }
        catch ( final AmazonClientException e )
        {
            logger.error( "AmazonClientException while storing {}.", transcodingJob, e );
        }
    }

    @Override
    public void delete( final TranscodingJob transcodingJob )
    {
        final ImmutableMap.Builder<String, AttributeValue> keyBuilder = ImmutableMap.builder();
        keyBuilder.put( "transcode-job-id", new AttributeValue( transcodingJob.getId() ) );

        try
        {
            this.dynamoDB.deleteItem( TABLE_NAME, keyBuilder.build() );
        }
        catch ( final AmazonServiceException e )
        {
            logger.error( "AmazonServiceException while storing {}.  RID: {}", transcodingJob, e.getRequestId(), e );
        }
        catch ( final AmazonClientException e )
        {
            logger.error( "AmazonClientException while storing {}.", transcodingJob, e );
        }
    }

}
