package org.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppStartStopListener {

    private static final Logger log = LoggerFactory.getLogger(AppStartStopListener.class);

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        String applicationName = event.getApplicationContext().getApplicationName();
        log.info("App '{}' Started 🥳", applicationName);
    }

    @EventListener
    public void handleContextStopped(ContextStoppedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        log.info("App Stopped 😥");
    }

}
