package com.hood.transcoder.domain;

import java.io.File;
import java.util.Objects;

import com.google.common.io.Files;
import com.hood.transcoder.domain.movie.Movie;
import com.hood.transcoder.domain.movie.MovieFormat;

public class TranscodeRequest
{
    private final File inputFile;
    private final File outputFile;

    public TranscodeRequest( final Movie movie, final MovieFormat destinationFormat )
    {
        this.inputFile = movie.getFile();
        this.outputFile = new File( this.getOutputFilename( destinationFormat ) );
    }

    public File getInputFile()
    {
        return this.inputFile;
    }

    private String getOutputFilename( final MovieFormat destinationFormat )
    {
        final String inputFilename = this.inputFile.getName();
        final String outputFilename =
                Files.getNameWithoutExtension( inputFilename ) + "." + destinationFormat.getExtension();
        return outputFilename;
    }

    public File getOutputFile()
    {
        return this.outputFile;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.inputFile, this.outputFile );
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
            return Objects.equals( this.inputFile, other.inputFile )
                    && Objects.equals( this.outputFile, other.outputFile );
        }
        return false;
    }
}
