package com.hood.transcoder.domain.transcoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.elastictranscoder.model.Job;
import com.amazonaws.services.elastictranscoder.model.JobInput;

public class TranscodingService
{
    private static final Logger logger = LoggerFactory.getLogger( TranscodingService.class );
    private static final String MOV_TO_MP4_PIPELINE_ID = "1381642622684-mc1ehi";
    private static final String ETS_PRESET_1080P = "1351620000001-000001";

    private final AmazonElasticTranscoder elasticTranscoder;

    public TranscodingService( final AmazonElasticTranscoder elasticTranscoder )
    {
        this.elasticTranscoder = elasticTranscoder;
    }

    public TranscodingJob transcode( final TranscodeRequest transcodeRequest )
    {
        final String inputKey = transcodeRequest.getInputKey();
        final String outputKey = transcodeRequest.getOutputKey();

        // Extract TranscodingService
        logger.info( "Transcoding {} to {}.", inputKey, outputKey );
        final JobInput jobInput =
                new JobInput().withKey( inputKey ).withAspectRatio( "auto" ).withContainer( "auto" )
                        .withFrameRate( "auto" ).withInterlaced( "auto" ).withResolution( "auto" );
        final CreateJobOutput createJobOutput =
                new CreateJobOutput().withKey( outputKey ).withPresetId( ETS_PRESET_1080P ).withRotate( "auto" )
                        .withThumbnailPattern( "" );
        final CreateJobRequest jobRequest =
                new CreateJobRequest().withInput( jobInput ).withOutputs( createJobOutput )
                        .withPipelineId( MOV_TO_MP4_PIPELINE_ID );

        CreateJobResult jobResult;
        try
        {
            jobResult = this.elasticTranscoder.createJob( jobRequest );
        }
        catch ( final AmazonClientException e )
        {
            logger.error( "Error creating ElasticTranscoder Job!", e );
            return null;
        }

        if ( jobResult.getJob() == null )
        {
            logger.error( "Job is null.  Something went wrong!" );
            return null;
        }
        final Job job = jobResult.getJob();
        logger.info( "Job {} is {}.", job.getId(), job.getStatus() );
        return new TranscodingJob( job.getId(), transcodeRequest.getInputPath(), transcodeRequest.getOutputPath() );
    }
}
