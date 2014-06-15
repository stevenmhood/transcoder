package com.hood.transcoder;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.hood.transcoder.application.TranscoderApplication;
import com.hood.transcoder.infrastructure.TranscodeNotificationListener;

public class Launcher
{
    public static void main( final String[] args ) throws InterruptedException
    {
        // Initialize log4j
        PropertyConfigurator.configure( Loader.getResource( "log4j.properties" ) );

        // Initialize Spring
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan( "com.hood.transcoder.spring" );
        context.refresh();

        // Run application
        final TranscoderApplication transcoderApplication =
                (TranscoderApplication) context.getBean( "transcoderApplication" );
        final TranscodeNotificationListener transcodeNotificationListener =
                (TranscodeNotificationListener) context.getBean( "transcodeNotificationListener" );
        final Thread transcodeNotificationListenerThread =
                new Thread( transcodeNotificationListener, "TranscodeNotificationListener" );
        transcodeNotificationListenerThread.start();

        if ( args != null )
        {
            for ( final String arg : args )
            {
                final Path path = FileSystems.getDefault().getPath( arg );
                transcoderApplication.transcodePath( path );
            }
        }

        while ( transcoderApplication.hasOutstandingJobs() )
        {
            Thread.sleep( TimeUnit.SECONDS.toMillis( 1 ) );
        }

        context.close();
    }
}
