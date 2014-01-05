package com.hood.transcoder.application;


public interface TranscodeEventHandler
{
    void onTranscodeComplete( TranscodingJob completedTranscodeJob );
}
