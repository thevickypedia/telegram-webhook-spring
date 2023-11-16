package main.api.app;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class main {
    static Logger logger = LoggerFactory.getLogger(main.class);

    @GetMapping("/health")
    public Object health() {
        return ResponseEntity.ok().body("Healthy");
    }

    @GetMapping("/getWebhook")  // todo: remove mapping
    public Object get_webhook() {
        JSONObject webhook_response = Webhook.getWebhook();
        if (webhook_response == null || webhook_response.isEmpty()) {
            return ResponseEntity.notFound();
        }
        logger.info(webhook_response.toString());
        return ResponseEntity.ok().body(webhook_response.toString());
    }
}
