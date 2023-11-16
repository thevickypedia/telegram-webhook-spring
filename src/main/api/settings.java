package main.api;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.util.List;

public class settings {
    static Dotenv dotenv = Dotenv.configure()
            .directory("src")
            .filename(config.env_filename)
            .load();
    public static String bot_token;
    public static String secret_token;
    public static String webhook;
    public static String endpoint;
    public static Integer port;
    public static boolean drop_pending_updates;
    public static Integer max_connections;
    public static String webhook_ip;
    public static File certificate;
    public static List<String> allowed_updates;

    public static void __init__() {
        port = EnvParser.parsePort(dotenv.get("port", dotenv.get("PORT")));
        webhook = EnvParser.parseWebhook(dotenv.get("webhook", dotenv.get("WEBHOOK")));
        endpoint = EnvParser.parseEndpoint(dotenv.get("endpoint", dotenv.get("ENDPOINT")));
        bot_token = EnvParser.parseBotToken(dotenv.get("bot_token", dotenv.get("BOT_TOKEN")));
        webhook_ip = EnvParser.parseIpAddress(dotenv.get("webhook_ip", dotenv.get("WEBHOOK_IP")));
        certificate = EnvParser.parseCertificate(dotenv.get("certificate", dotenv.get("CERTIFICATE")));
        secret_token = EnvParser.parseSecretToken(dotenv.get("secret_token", dotenv.get("SECRET_TOKEN")));
        allowed_updates = EnvParser.parseAllowedUpdates(dotenv.get("allowed_updates", dotenv.get("ALLOWED_UPDATES")));
        max_connections = EnvParser.parseMaxConnections(dotenv.get("max_connections", dotenv.get("MAX_CONNECTIONS")));
        drop_pending_updates = dotenv.get("drop_pending_updates", dotenv.get("DROP_PENDING_UPDATES")) != null;
    }
}
