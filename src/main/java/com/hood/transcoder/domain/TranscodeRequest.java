package com.hood.transcoder.domain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.google.common.io.Files;
import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieFormat;

public class TranscodeRequest
{
    private final Path parentPath;
    private final String inputKey;
    private final String outputKey;

    public TranscodeRequest( final Movie movie, final MovieFormat destinationFormat )
    {
        this.parentPath = movie.getPath().getParent();
        this.inputKey = movie.getPath().getFileName().toString();
        this.outputKey = this.getOutputKey( destinationFormat );
    }

    public String getInputKey()
    {
        return this.inputKey;
    }

    private String getOutputKey( final MovieFormat destinationFormat )
    {
        return Files.getNameWithoutExtension( this.inputKey ) + "." + destinationFormat.getExtension();
    }

    public String getOutputKey()
    {
        return this.outputKey;
    }

    public Path getInputPath()
    {
        return Paths.get( this.parentPath.toString(), this.inputKey );
    }

    public Path getOutputPath()
    {
        return Paths.get( this.parentPath.toString(), this.outputKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.parentPath, this.inputKey, this.outputKey );
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
        if ( o instanceof TranscodeRequest )
        {
            final TranscodeRequest other = (TranscodeRequest) o;
            return Objects.equals( this.parentPath, other.parentPath )
                    && Objects.equals( this.inputKey, other.inputKey )
                    && Objects.equals( this.outputKey, other.outputKey );
        }
        return false;
    }
}
