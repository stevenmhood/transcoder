package com.hood.transcoder.domain.movie;

public enum MovieFormat
{
    MOV( "mov" ), MP4( "mp4" );

    private final String extension;

    private MovieFormat( final String extension )
    {
        this.extension = extension;
    }

    public String getExtension()
    {
        return this.extension;
    }
}