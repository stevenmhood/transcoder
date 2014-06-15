package com.hood.transcoder.application;

import java.util.Objects;

import com.hood.transcoder.domain.movie.MovieId;

public class TranscodeEvent
{
    private final String id;
    private final MovieId inputMovieId;
    private final MovieId outputMovieId;

    public TranscodeEvent( final String id, final MovieId inputMovieId, final MovieId outputMovieId )
    {
        this.id = id;
        this.inputMovieId = inputMovieId;
        this.outputMovieId = outputMovieId;
    }

    public String getId()
    {
        return this.id;
    }

    public MovieId getInputMovieId()
    {
        return this.inputMovieId;
    }

    public MovieId getOutputMovieId()
    {
        return this.outputMovieId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.id );
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
        if ( o instanceof TranscodeEvent )
        {
            final TranscodeEvent other = (TranscodeEvent) o;
            return Objects.equals( this.id, other.id );
        }
        return false;
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).add( "id", this.id )
                .add( "inputMovie", this.inputMovieId ).add( "outputMovie", this.outputMovieId ).toString();
    }
}
