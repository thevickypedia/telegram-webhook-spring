package main.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        if (!config.env_file.exists()) {
            logger.error("'" + config.env_filename + "' doesn't exist");
            return;
        }
        settings.__init__();
        SpringApplication app = new SpringApplication(Application.class);
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", settings.port);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
