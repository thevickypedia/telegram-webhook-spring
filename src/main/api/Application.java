package main.api;

import main.api.webhook.webhook;
import main.api.app.exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws exceptions.StartupError {
        if (!config.env_file.exists()) {
            logger.error("'" + config.env_filename + "' doesn't exist");
            return;
        }
        settings.__init__();
        if (webhook.setWebhook() == null) {
            throw new exceptions.StartupError("Unable to set webhook");
        }
        SpringApplication app = new SpringApplication(Application.class);
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", settings.port);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
