package com.hood.transcoder.domain.movie;

import java.util.Objects;

public class MovieId
{
    private final String movieId;

    public MovieId( final String movieId )
    {
        super();
        this.movieId = movieId;
    }

    public String getMovieId()
    {
        return this.movieId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.movieId );
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
        if ( o instanceof MovieId )
        {
            final MovieId other = (MovieId) o;
            return Objects.equals( this.movieId, other.movieId );
        }
        return false;
    }
}
