package com.hood.transcoder.domain.movie;

import java.io.File;
import java.util.Objects;

public class Movie
{
    private final MovieId movieId;
    private final File file;

    public Movie( final MovieId movieId )
    {
        this( movieId, null );
    }

    public Movie( final MovieId movieId, final File file )
    {
        this.movieId = movieId;
        this.file = file;
    }

    public MovieId getMovieId()
    {
        return this.movieId;
    }

    public File getFile()
    {
        return this.file;
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
        if ( o instanceof Movie )
        {
            final Movie other = (Movie) o;
            return Objects.equals( this.movieId, other.movieId );
        }
        return false;
    }
}
