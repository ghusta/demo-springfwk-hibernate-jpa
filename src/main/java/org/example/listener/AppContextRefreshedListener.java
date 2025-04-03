package org.example.listener;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AppContextRefreshedListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextRefreshedListener.class);

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        log.info("Context refreshed: {}", applicationContext.getId());

        EntityManagerFactory entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        Map<String, Object> entityManagerFactoryProperties = entityManagerFactory.getProperties();

        Map<String, Object> hibernateMap = entityManagerFactoryProperties.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("hibernate."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Object> jakartaPersistenceMap = entityManagerFactoryProperties.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("jakarta.persistence."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int propsSize = entityManagerFactoryProperties.size();
    }

}
