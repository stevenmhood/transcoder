package com.hood.transcoder.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;

public class S3ProgressListener implements ProgressListener
{
    private static final Logger logger = LoggerFactory.getLogger( S3ProgressListener.class );

    private final String filename;
    private final long fileSize;

    private long bytesTransferred;
    private long lastNotifiedBytesTransferred;

    public S3ProgressListener( final String filename, final long fileSize )
    {
        super();
        this.filename = filename;
        this.fileSize = fileSize;
        this.bytesTransferred = 0;
    }

    @Override
    public void progressChanged( final ProgressEvent progressEvent )
    {
        this.bytesTransferred += progressEvent.getBytesTransferred();
        if ( this.bytesTransferred >= this.lastNotifiedBytesTransferred + Math.pow( 2, 20 ) )
        {
            logger.info( "Transferring {}, {} of {} bytes complete.",
                         this.filename,
                         this.bytesTransferred,
                         this.fileSize );
            this.lastNotifiedBytesTransferred = this.bytesTransferred;
        }
    }
}
