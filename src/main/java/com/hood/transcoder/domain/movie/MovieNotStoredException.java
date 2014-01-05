package com.hood.transcoder.domain.movie;

public class MovieNotStoredException extends Exception
{
    private static final long serialVersionUID = -6404828557137470512L;

    private final Movie movie;

    public MovieNotStoredException( final Movie movie, final Exception e )
    {
        super( e );
        this.movie = movie;
    }

    public Movie getMovie()
    {
        return this.movie;
    }

}
