package com.hood.transcoder.domain.movie;

public interface MovieRepository
{
    void store( Movie movie ) throws MovieNotStoredException;

    void remove( Movie movie );
}