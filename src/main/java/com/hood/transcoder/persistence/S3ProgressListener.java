package com.hood.transcoder.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;

public class S3ProgressListener implements ProgressListener
{
    private static final Logger logger = LoggerFactory.getLogger( S3ProgressListener.class );
    private static final double ONE_MEGABYTE = Math.pow( 2, 20 );

    private final String filename;
    private final long fileSize;

    private long bytesTransferred;
    private long partsStarted;
    private long partsCompleted;
    private long lastNotifiedBytesTransferred;

    public S3ProgressListener( final String filename, final long fileSize )
    {
        super();
        this.filename = filename;
        this.fileSize = fileSize;
        this.bytesTransferred = 0;
        this.partsStarted = 0;
        this.partsCompleted = 0;
    }

    @Override
    public void progressChanged( final ProgressEvent progressEvent )
    {
        this.bytesTransferred += progressEvent.getBytesTransferred();
        if ( this.bytesTransferred >= this.lastNotifiedBytesTransferred + ONE_MEGABYTE )
        {
            logger.info( "Transferring {}, {} of {} bytes complete.",
                         this.filename,
                         this.bytesTransferred,
                         this.fileSize );
            this.lastNotifiedBytesTransferred = this.bytesTransferred;
        }
        switch ( progressEvent.getEventCode() )
        {
            case ProgressEvent.STARTED_EVENT_CODE:
                logger.info( "Started transferring {}, {} of {} bytes complete.",
                             this.filename,
                             this.bytesTransferred,
                             this.fileSize );
                break;

            case ProgressEvent.PART_STARTED_EVENT_CODE:
                this.partsStarted++;
                logger.info( "Started transferring part {} of {}, {} of {} bytes complete.",
                             this.partsStarted,
                             this.filename,
                             this.bytesTransferred,
                             this.fileSize );
                break;

            case ProgressEvent.PART_COMPLETED_EVENT_CODE:
                this.partsCompleted++;
                logger.info( "Completed transferring part {} of {}, {} of {} bytes complete.",
                             this.partsCompleted,
                             this.filename,
                             this.bytesTransferred,
                             this.fileSize );
                break;

            case ProgressEvent.COMPLETED_EVENT_CODE:
                logger.info( "Completed transferring {}, {} of {} bytes complete.",
                             this.filename,
                             this.bytesTransferred,
                             this.fileSize );
                break;
        }
    }
}
