package com.hood.transcoder.domain.movie;

public interface MovieRepository
{
    Movie get( MovieId movieId );

    void store( Movie movie ) throws MovieNotStoredException;

    void remove( Movie movie );
}