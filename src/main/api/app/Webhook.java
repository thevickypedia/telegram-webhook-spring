package main.api.app;

import main.api.settings;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Webhook {
    static Logger logger = LoggerFactory.getLogger(Webhook.class);
    static String BASE_URL = "https://api.telegram.org/bot" + settings.bot_token;

    public static JSONObject getWebhook() {
        try {
            URL url = new URI(BASE_URL + "/getWebhookInfo").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (String line; (line = reader.readLine()) != null; ) {
                    var append_value = line + "\n";
                    result.append(append_value);
                }
                return new JSONObject(result);
            }
        } catch (URISyntaxException | IOException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static JSONObject setWebhook() {
        return new JSONObject();
    }
}
