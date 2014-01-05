package com.hood.transcoder.application;

import java.util.Objects;

import com.hood.transcoder.domain.movie.MovieId;

public class TranscodingJob
{
    private final MovieId inputMovie;
    private final MovieId outputMovie;

    public TranscodingJob( final MovieId inputMovie, final MovieId outputMovie )
    {
        this.inputMovie = inputMovie;
        this.outputMovie = outputMovie;
    }

    public MovieId getInputMovie()
    {
        return this.inputMovie;
    }

    public MovieId getOutputMovie()
    {
        return this.outputMovie;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.inputMovie, this.outputMovie );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null )
        {
            return false;
        }
        if ( o instanceof TranscodingJob )
        {
            final TranscodingJob other = (TranscodingJob) o;
            return Objects.equals( this.inputMovie, other.inputMovie )
                    && Objects.equals( this.outputMovie, other.outputMovie );
        }
        return false;
    }
}