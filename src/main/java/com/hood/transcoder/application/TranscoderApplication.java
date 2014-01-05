package com.hood.transcoder.application;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hood.transcoder.domain.TranscodeRequest;
import com.hood.transcoder.domain.TranscodingService;
import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieFormat;
import com.hood.transcoder.domain.movie.MovieId;
import com.hood.transcoder.domain.movie.MovieNotStoredException;
import com.hood.transcoder.domain.movie.MovieRepository;

public class TranscoderApplication implements TranscodeEventHandler
{
    private static final Logger logger = LoggerFactory.getLogger( TranscoderApplication.class );

    private final MovieRepository movieRepository;
    private final TranscodingService transcodingService;
    private final Set<TranscodingJob> activeTranscodingJobs;

    public TranscoderApplication( final MovieRepository movieRepository, final TranscodingService transcodingService )
    {
        super();
        this.movieRepository = movieRepository;
        this.transcodingService = transcodingService;
        this.activeTranscodingJobs = new HashSet<>();
    }

    public void transcodeFile( final File file )
    {
        final MovieId movieId = new MovieId( file.getName() );
        final Movie movie = new Movie( movieId, file );
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
        final Movie outputMovie = new Movie( new MovieId( transcodeRequest.getOutputFile().getName() ) );
        this.movieRepository.remove( outputMovie );
        final TranscodingJob transcodingJob = this.transcodingService.transcode( transcodeRequest );
        if ( transcodingJob != null )
        {
            this.activeTranscodingJobs.add( transcodingJob );
        }
    }

    @Override
    public void onTranscodeComplete( final TranscodingJob transcodingJob )
    {
        final MovieId movieId = transcodingJob.getInputMovie();
        this.movieRepository.remove( new Movie( movieId ) );
        this.activeTranscodingJobs.remove( transcodingJob );
    }

    public boolean hasOutstandingJobs()
    {
        return this.activeTranscodingJobs.size() != 0;
    }
}
