package main.api;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class defaults {
    static JSONObject AllowedPorts = new JSONObject()
            .put("tcp", "88")
            .put("http", "80")
            .put("https", "443")
            .put("openssl", "8443");
    static List<String> AllowedUpdates = new ArrayList<>();
    static String endpoint = "/telegram-webhook";
    static Integer max_connections = 40;

    static {  // Special block that gets executed before any other static method when loaded into memory
        AllowedUpdates.add("message");
        AllowedUpdates.add("edited_message");
        AllowedUpdates.add("channel_post");
        AllowedUpdates.add("edited_channel_post");
    }
}
