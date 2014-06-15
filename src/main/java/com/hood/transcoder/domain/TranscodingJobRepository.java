package com.hood.transcoder.domain;

public interface TranscodingJobRepository
{
    TranscodingJob get( final String jobId );

    void store( TranscodingJob transcodingJob );

    void delete( TranscodingJob transcodingJob );
}
