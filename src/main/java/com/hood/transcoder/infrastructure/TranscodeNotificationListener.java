package com.hood.transcoder.infrastructure;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.hood.transcoder.application.TranscodeEvent;
import com.hood.transcoder.application.TranscodeEventHandler;
import com.hood.transcoder.domain.movie.MovieId;

public class TranscodeNotificationListener implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger( TranscodeNotificationListener.class );
    private final String queueURL;
    private final AmazonSQS sqs;
    private final TranscodeEventHandler transcodeEventHandler;
    private boolean isShutdown;
    private final AtomicBoolean isReceiving;

    public TranscodeNotificationListener( final AmazonSQS sqs,
                                          final String queueURL,
                                          final TranscodeEventHandler transcodeEventHandler )
    {
        super();
        this.sqs = sqs;
        this.queueURL = queueURL;
        this.transcodeEventHandler = transcodeEventHandler;
        this.isShutdown = false;
        this.isReceiving = new AtomicBoolean( true );
    }

    public void shutdown() throws InterruptedException
    {
        this.isShutdown = true;
        logger.info( "Shutting down." );
        while ( this.isReceiving.get() )
        {
            Thread.sleep( TimeUnit.SECONDS.toMillis( 1 ) );
        }
    }

    @Override
    public void run()
    {
        while ( !this.isShutdown )
        {
            final ReceiveMessageRequest receiveMessageRequest =
                    new ReceiveMessageRequest().withQueueUrl( this.queueURL );
            logger.info( "Listening for transcode notifications." );
            this.isReceiving.set( true );
            final ReceiveMessageResult receiveMessageResult = this.sqs.receiveMessage( receiveMessageRequest );

            for ( final Message message : receiveMessageResult.getMessages() )
            {
                try
                {
                    final JSONObject jsonRawMessage = new JSONObject( message.getBody() );
                    final JSONObject jsonMessage = new JSONObject( jsonRawMessage.getString( "Message" ) );
                    logger.info( "Message: {}", jsonMessage );
                    final String jobId = jsonMessage.getString( "jobId" );
                    final String state = jsonMessage.getString( "state" );
                    logger.info( "Job {} is {}", jobId, state );
                    if ( "COMPLETED".equalsIgnoreCase( state ) )
                    {
                        this.notifyComplete( jsonMessage );
                    }
                }
                catch ( final JSONException e )
                {
                    logger.error( "Malformed ETS Notification in {}!", message, e );
                }
                finally
                {
                    this.sqs.deleteMessage( new DeleteMessageRequest( this.queueURL, message.getReceiptHandle() ) );
                }
            }
            this.isReceiving.set( false );
            try
            {
                Thread.sleep( TimeUnit.SECONDS.toMillis( 3 ) );
            }
            catch ( final InterruptedException e )
            {
                // TODO: Figure out how to handle these appropriately.
                logger.error( "Interrupted!" );
            }

        }
    }

    private void notifyComplete( final JSONObject jsonMessage ) throws JSONException
    {
        final String jobId = jsonMessage.getString( "jobId" );
        final String inputKey = jsonMessage.getJSONObject( "input" ).getString( "key" );
        final JSONArray jsonOutputList = jsonMessage.getJSONArray( "outputs" );
        for ( int i = 0; i < jsonOutputList.length(); i++ )
        {
            final JSONObject jsonOutput = jsonOutputList.getJSONObject( i );
            final String outputKey = jsonOutput.getString( "key" );
            final TranscodeEvent completedTranscodeEvent =
                    new TranscodeEvent( jobId, new MovieId( inputKey ), new MovieId( outputKey ) );
            this.transcodeEventHandler.onTranscodeComplete( completedTranscodeEvent );
        }
    }
}
