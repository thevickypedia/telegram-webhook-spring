package main.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class settings {
    static Dotenv dotenv = Dotenv.configure()
            .directory("src")
            .filename(config.env_filename)
            .load();
    static String bot_token = dotenv.get("bot_token", dotenv.get("BOT_TOKEN"));
    static String endpoint = dotenv.get("endpoint", dotenv.get("ENDPOINT", "/telegram-webhook"));
    static String webhook = dotenv.get("webhook", dotenv.get("WEBHOOK"));
    static String secret_token = dotenv.get("secret_token", dotenv.get("SECRET_TOKEN"));
    static Integer port;
    static String port_raw = dotenv.get("port", dotenv.get("PORT"));
    static boolean drop_pending_updates;
    static String drop_pending_updates_raw = dotenv.get("drop_pending_updates", dotenv.get("DROP_PENDING_UPDATES"));
    static Integer max_connections;
    static String max_connections_raw = dotenv.get("max_connections", dotenv.get("MAX_CONNECTIONS"));
    static InetAddress webhook_ip;
    static String webhook_ip_raw = dotenv.get("webhook_ip", dotenv.get("WEBHOOK_IP"));
    static File certificate;
    static String certificate_raw = dotenv.get("certificate", dotenv.get("CERTIFICATE"));
    static String allowed_updates_raw = dotenv.get("allowed_updates", dotenv.get("ALLOWED_UPDATES"));
    static Logger logger = LoggerFactory.getLogger(settings.class);

    public static void __init__() {
        if (bot_token == null) {
            throw new InvalidParameterException("'bot_token' is required for startup.");
        }
        if (bot_token.length() > 120 | bot_token.length() < 8) {
            throw new InvalidParameterException("character size for 'bot_token' should be between 8 and 120");
        }
        if (secret_token != null && !secret_token.isBlank()) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9_-]{1,256}$");
            Matcher matcher = pattern.matcher(secret_token);
            if (!matcher.find()) {
                throw new InvalidParameterException("secret_token should match the regex pattern ^[A-Za-z0-9_-]{1,256}$");
            }
        } else {
            logger.warn("It is highly recommended to set a value for `secret_token`, " +
                    "as it will ensure the request comes from a webhook set by you.");
        }
        if (!endpoint.startsWith("/")) {
            throw new InvalidParameterException("endpoint should start with the URL path '/'");
        }
        drop_pending_updates = drop_pending_updates_raw != null;
        if (webhook != null && !webhook.isBlank()) {
            Pattern pattern = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\n");
            Matcher matcher = pattern.matcher(webhook);
            if (!matcher.find()) {
                throw new InvalidParameterException("webhook should be a HTTP url");
            }
        }
        port = EnvParser.parsePort(port_raw);
        max_connections = EnvParser.parseMaxConnections(max_connections_raw);
        webhook_ip = EnvParser.parseIpAddress(webhook_ip_raw);
        certificate = EnvParser.parseCertificate(certificate_raw);
    }
}
