package com.bajaj.qualifier.runner;

import com.bajaj.qualifier.service.WebhookService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(ApplicationArguments args) {
        webhookService.executeFlow();
    }
}
