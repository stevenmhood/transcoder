package com.hood.transcoder.persistence;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieNotStoredException;
import com.hood.transcoder.domain.movie.MovieRepository;

public class S3MovieRepository implements MovieRepository
{
    private static final Logger logger = LoggerFactory.getLogger( S3MovieRepository.class );
    private static final String S3_BUCKET_HOOD_ETS_SOURCE = "hood-ets-source";

    private final TransferManager transferManager;

    public S3MovieRepository( final TransferManager transferManager )
    {
        this.transferManager = transferManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hood.transcoder.MovieRepository#store(com.hood.transcoder.Movie)
     */
    @Override
    public void store( final Movie movie ) throws MovieNotStoredException
    {
        // Extract Storage class
        final String key = movie.getMovieId().getMovieId();
        logger.info( "Uploading {} to S3 key {}", movie, key );
        final PutObjectRequest putObjectRequest =
                new PutObjectRequest( S3_BUCKET_HOOD_ETS_SOURCE, key, movie.getFile() );
        final ProgressListener progressListener = new S3ProgressListener( key, movie.getFile().length() );
        Upload upload = null;
        try
        {
            upload = this.transferManager.upload( putObjectRequest );
            upload.addProgressListener( progressListener );
            upload.waitForCompletion();
        }
        catch ( AmazonClientException | InterruptedException e )
        {
            this.transferManager.abortMultipartUploads( S3_BUCKET_HOOD_ETS_SOURCE, new Date() );
            throw new MovieNotStoredException( movie, e );
        }
        logger.info( "Upload complete." );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hood.transcoder.MovieRepository#remove(com.hood.transcoder.Movie)
     */
    @Override
    public void remove( final Movie movie )
    {
        final String key = movie.getMovieId().getMovieId();
        logger.info( "Deleting file: {}", key );
        this.transferManager.getAmazonS3Client().deleteObject( S3_BUCKET_HOOD_ETS_SOURCE, key );
    }
}
