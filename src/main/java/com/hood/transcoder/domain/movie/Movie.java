package com.hood.transcoder.domain.movie;

import java.nio.file.Path;
import java.util.Objects;

public class Movie
{
    private final MovieId movieId;
    private final Path path;

    public Movie( final MovieId movieId )
    {
        this( movieId, null );
    }

    public Movie( final MovieId movieId, final Path path )
    {
        this.movieId = movieId;
        this.path = path;
    }

    public MovieId getMovieId()
    {
        return this.movieId;
    }

    public Path getPath()
    {
        return this.path;
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

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).add( "movieId", this.movieId )
                .add( "path", this.path ).toString();
    }
}
