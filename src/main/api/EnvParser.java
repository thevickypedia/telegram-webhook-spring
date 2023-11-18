package main.api;

import main.api.app.exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvParser {
    static Logger logger = LoggerFactory.getLogger(EnvParser.class);

    public static String parseBotToken(String bot_token) {
        if (bot_token == null || bot_token.isBlank()) {
            throw new InvalidParameterException("'bot_token' is required for startup.");
        }
        if (bot_token.length() > 120 | bot_token.length() < 8) {
            throw new InvalidParameterException("character size for 'bot_token' should be between 8 and 120");
        }
        return bot_token;
    }

    public static String parseSecretToken(String secret_token) {
        if (secret_token != null && !secret_token.isBlank()) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9_-]{1,256}$");
            Matcher matcher = pattern.matcher(secret_token);
            if (!matcher.find()) {
                throw new InvalidParameterException("secret_token should match the regex pattern ^[A-Za-z0-9_-]{1,256}$");
            }
        } else {
            logger.warn("It is highly recommended to set a value for 'secret_token', " +
                    "as it will ensure the request comes from a webhook set by you.");
        }
        return secret_token;
    }

    public static String parseEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            return defaults.endpoint;
        }
        if (!endpoint.startsWith("/")) {
            throw new InvalidParameterException("endpoint should start with the URL path '/'");
        }
        return endpoint;
    }

    public static Integer parsePort(String port) {
        if (port == null) {
            port = defaults.AllowedPorts.getString("openssl");
        } else {
            Iterator<String> keys = defaults.AllowedPorts.keys();
            boolean pass = false;
            while (keys.hasNext()) {
                String key = keys.next();
                String value = defaults.AllowedPorts.getString(key);
                if (port.equals(value)) {
                    logger.info("port [{}]:{}", key, value);
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                logger.error("'port' can only be one of {}", defaults.AllowedPorts.toMap().values());
                System.exit(1);
            }
        }
        return Integer.parseInt(port);
    }

    public static Integer parseMaxConnections(String maxConnections) {
        if (maxConnections == null || maxConnections.isBlank()) {
            return defaults.max_connections;
        }
        try {
            int max_connections = Integer.parseInt(maxConnections);
            if (max_connections > 100 || max_connections < 1) {
                throw new InvalidParameterException("'max_connections' should be between 1 and 100");
            }
            return max_connections;
        } catch (NumberFormatException error) {
            logger.error(error.getMessage());
            throw new InvalidParameterException("'max_connections' should be an integer value");
        }
    }

    //todo: Still unstable
    private static void useSelfSignedCertificate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            logger.info("Accepted issuers");
                            return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                                throws CertificateParsingException {
                            logger.info("Client certificate accepted: " + certs[0].getSubjectAlternativeNames());
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                                throws CertificateParsingException {
                            logger.info("Server certificate accepted: " + certs[0].getSubjectAlternativeNames());
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception error) {
            logger.error(error.getMessage());
        }
    }

    public static void testWebhook(String webhook) throws exceptions.StartupError {
        try {
            URL url = new URI(webhook).toURL();
            if (settings.certificate != null) {
                useSelfSignedCertificate();
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // tunneling apps return bad gateway if an app is not running in the said port
            if (connection.getResponseCode() <= 400 || connection.getResponseCode() >= 500) {
                return;
            }
            String error = connection.getResponseCode() + ": " + connection.getResponseMessage();
            logger.error(error);
            throw new exceptions.StartupError(error);
        } catch (URISyntaxException | IOException error) {
            throw new exceptions.StartupError(error.getMessage());
        }
    }

    public static String parseWebhook(String webhook) {
        if (webhook == null || webhook.isBlank()) {
            throw new InvalidParameterException("'webhook' is required for this project");
        }
        if (!webhook.startsWith("http")) {
            throw new InvalidParameterException("webhook should be a valid HTTP(s) url");
        }
        return webhook;
    }

    public static String parseIpAddress(String webhook_ip) {
        if (webhook_ip == null || webhook_ip.isBlank()) {
            webhook_ip = settings.webhook;  // Extract IP from hostname is IP is not given explicitly
        }
        try {
            String ipaddress = InetAddress.getByName(webhook_ip).getHostAddress();
            if (!webhook_ip.equals(ipaddress)) {
                logger.info("Resolved IP: {}", ipaddress);
            }
            return ipaddress;
        } catch (UnknownHostException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static File parseCertificate(String certificate) {
        if (certificate == null || certificate.isBlank()) {
            return null;
        }
        File cert = Paths.get(certificate).toFile();
        if (cert.isFile() && cert.exists()) {
            return cert;
        }
        throw new InvalidParameterException("Invalid certificate, isFile: " + cert.isFile() +
                ", exists: " + cert.exists());
    }

    public static List<String> asList(String list, String separator) {
        if (list == null || list.isBlank()) {
            return null;
        }
        List<String> listed = new ArrayList<>();
        try {
            for (String entry : list.split(separator)) {
                listed.add(entry.trim());
            }
            return listed;
        } catch (NullPointerException error) {
            logger.error(error.getMessage());
        }
        return null;
    }

    public static List<String> parseAllowedUpdates(String allowed_updates) {
        List<String> updates = new ArrayList<>();
        if (allowed_updates == null) {
            updates.add(defaults.AllowedUpdates.get(0));
            return updates;
        }
        for (String entry : asList(allowed_updates, ",")) {
            if (defaults.AllowedUpdates.contains(entry)) {
                updates.add(entry);
            } else {
                throw new InvalidParameterException("'allowed_updates' should be one of " + defaults.AllowedUpdates);
            }
        }
        return updates;
    }
}
