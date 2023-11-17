package main.api.routers;

import jakarta.servlet.http.HttpServletRequest;
import main.api.webhook.webhook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
public class service {
    static Logger logger = LoggerFactory.getLogger(service.class);

    public static void sendResponse(long chat_id) {
        URL url;
        try {
            url = new URI(webhook.BASE_URL + "/sendMessage").toURL();
        } catch (URISyntaxException | MalformedURLException error) {
            return;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            JSONObject payload = new JSONObject();
            payload.put("chat_id", chat_id);
            payload.put("text", "Received some text");  // todo: add something more
            byte[] out = payload.toString().getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(out.length));
            OutputStream stream = connection.getOutputStream();
            stream.write(out);
            logger.info("{}: {}", connection.getResponseCode(), connection.getResponseMessage());
        } catch (IOException error) {
            logger.error(error.getMessage());
        }
    }

    @PostMapping("/telegram-webhook")
    public Object telegram(HttpServletRequest request) {
        try {
            JSONObject requestData = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
            JSONObject chat = requestData.getJSONObject("message").getJSONObject("chat");
            sendResponse(chat.getLong("id"));
        } catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }
}
