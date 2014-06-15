package com.hood.transcoder.domain.transcoding;

public interface TranscodingJobRepository
{
    TranscodingJob get( final String jobId );

    void store( TranscodingJob transcodingJob );

    void delete( TranscodingJob transcodingJob );
}
