package com.hood.transcoder.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieFormat;
import com.hood.transcoder.domain.movie.MovieId;
import com.hood.transcoder.domain.movie.MovieNotStoredException;
import com.hood.transcoder.domain.movie.MovieRepository;
import com.hood.transcoder.domain.transcoding.TranscodeRequest;
import com.hood.transcoder.domain.transcoding.TranscodingJob;
import com.hood.transcoder.domain.transcoding.TranscodingJobRepository;
import com.hood.transcoder.domain.transcoding.TranscodingService;

public class TranscoderApplication implements TranscodeEventHandler
{
    private static final Logger logger = LoggerFactory.getLogger( TranscoderApplication.class );

    private final MovieRepository movieRepository;
    private final TranscodingJobRepository transcodingJobRepository;
    private final TranscodingService transcodingService;

    public TranscoderApplication( final MovieRepository movieRepository,
                                  final TranscodingJobRepository transcodingJobRepository,
                                  final TranscodingService transcodingService )
    {
        super();
        this.movieRepository = movieRepository;
        this.transcodingJobRepository = transcodingJobRepository;
        this.transcodingService = transcodingService;
    }

    public void transcodePath( final Path path )
    {
        final MovieId movieId = new MovieId( path.getFileName().toString() );
        final Movie movie = new Movie( movieId, path );
        try
        {
            this.movieRepository.store( movie );
        }
        catch ( final MovieNotStoredException e )
        {
            logger.error( "Movie storage failed for {}.", e.getMovie() );
            return;
        }
        final TranscodeRequest transcodeRequest = new TranscodeRequest( movie, MovieFormat.MP4 );
        final Movie outputMovie = new Movie( new MovieId( transcodeRequest.getOutputKey() ) );
        this.movieRepository.remove( outputMovie );

        final TranscodingJob transcodingJob = this.transcodingService.transcode( transcodeRequest );
        if ( transcodingJob != null )
        {
            this.transcodingJobRepository.store( transcodingJob );
        }
    }

    @Override
    public void onTranscodeComplete( final TranscodeEvent transcodeEvent )
    {
        final MovieId movieId = transcodeEvent.getInputMovieId();

        this.movieRepository.remove( new Movie( movieId ) );

        final TranscodingJob transcodingJob = this.transcodingJobRepository.get( transcodeEvent.getId() );
        final Movie outputMovie = this.movieRepository.get( transcodeEvent.getOutputMovieId() );

        final Path sourcePath = outputMovie.getPath();
        final Path destinationPath = transcodingJob.getOutputPath();
        try
        {
            Files.copy( sourcePath, destinationPath );
            logger.info( "Movie retrieved: {}", destinationPath );
            this.transcodingJobRepository.delete( transcodingJob );
        }
        catch ( final IOException e )
        {
            logger.error( "Could not copy from {} to {}", sourcePath, destinationPath, e );
        }

    }

    public boolean hasOutstandingJobs()
    {
        return true; // this.activeTranscodingJobs.size() != 0;
    }
}
