package main.api;

import org.json.JSONObject;

public class defaults {
    static JSONObject AllowedPorts = new JSONObject()
            .put("tcp", "88")
            .put("http", "80")
            .put("https", "443")
            .put("openssl", "8443");
    static String[] AllowedUpdates = {"message", "edited_message", "channel_post", "edited_channel_post"};
}
