package main.api.webhook;

import main.api.settings;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class webhook {
    static Logger logger = LoggerFactory.getLogger(webhook.class);
    public static String BASE_URL = "https://api.telegram.org/bot" + settings.bot_token;

    public static JSONObject getResponseJSON(HttpURLConnection connection) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            for (String line; (line = reader.readLine()) != null; ) {
                var append_value = line + "\n";
                result.append(append_value);
            }
            logger.info(result.toString());
            return new JSONObject(result);
        } catch (IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static JSONObject getWebhook() {
        try {
            URL url = new URI(BASE_URL + "/getWebhookInfo").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getResponseJSON(connection);
            }
        } catch (URISyntaxException | IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static JSONObject setWebhook() {
        JSONObject payload = new JSONObject();
        payload.put("url", settings.webhook + settings.endpoint);
        payload.put("secret_token", settings.secret_token);
        payload.put("drop_pending_updates", settings.drop_pending_updates);
        payload.put("max_connections", settings.max_connections);
        payload.put("allowed_updates", settings.allowed_updates.toString());
        payload.put("ip_address", settings.webhook_ip);
        URL url;
        HttpURLConnection connection;
        try {
            url = new URI(BASE_URL + "/setWebhook").toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
        } catch (URISyntaxException | IOException error) {
            logger.error(error.getMessage());
            return null;
        }
        connection.setDoOutput(true);
        try {
            if (settings.certificate != null) {
                String boundary = Long.toHexString(System.currentTimeMillis());
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.getOutputStream().write(payload.toString().getBytes());
                connection.getOutputStream().write(("--" + boundary + "\r\n").getBytes());
                connection.getOutputStream().write(("Content-Disposition: form-data; name=\"certificate\"; filename=\"" + settings.certificate.getName() + "\"\r\n").getBytes());
                connection.getOutputStream().write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());
                Files.copy(settings.certificate.toPath(), connection.getOutputStream());
                connection.getOutputStream().write(("\r\n--" + boundary + "--\r\n").getBytes());
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return getResponseJSON(connection);
                }
            } else {
                connection.setRequestProperty("Content-Type", "application/json");
                // todo: validate the following payload addition with certificates
                byte[] out = payload.toString().getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    logger.info("Webhook has been set successfully!");
                    return getResponseJSON(connection);
                }
            }
        } catch (IOException error) {
            logger.error(error.getMessage());
            return null;
        }
        return null;
    }
}
