package com.hood.transcoder.domain.transcoding;

import java.nio.file.Path;
import java.util.Objects;

public class TranscodingJob
{
    private final String id;
    private final Path inputPath;
    private final Path outputPath;

    public TranscodingJob( final String id, final Path inputPath, final Path outputPath )
    {
        this.id = id;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public String getId()
    {
        return this.id;
    }

    public Path getInputPath()
    {
        return this.inputPath;
    }

    public Path getOutputPath()
    {
        return this.outputPath;
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
        if ( o instanceof TranscodingJob )
        {
            final TranscodingJob other = (TranscodingJob) o;
            return Objects.equals( this.id, other.id );
        }
        return false;
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).add( "id", this.id )
                .add( "inputPath", this.inputPath ).add( "outputPath", this.outputPath ).toString();
    }
}