package com.hood.transcoder.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieNotStoredException;
import com.hood.transcoder.domain.movie.MovieRepository;

public class S3MovieRepository implements MovieRepository
{
    private static final Logger logger = LoggerFactory.getLogger( S3MovieRepository.class );
    private static final String S3_BUCKET_HOOD_ETS_SOURCE = "hood-ets-source";

    private final AmazonS3 s3;

    public S3MovieRepository( final AmazonS3 s3 )
    {
        this.s3 = s3;
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
        putObjectRequest.setGeneralProgressListener( progressListener );
        try
        {
            this.s3.putObject( putObjectRequest );
        }
        catch ( final AmazonClientException e )
        {
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
        this.s3.deleteObject( S3_BUCKET_HOOD_ETS_SOURCE, key );
    }
}
