package main.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Iterator;

public class EnvParser {
    static Logger logger = LoggerFactory.getLogger(Application.class);

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
                logger.error("'port' can only be {}", defaults.AllowedPorts.toMap().values());
                System.exit(1);
            }
        }
        return Integer.parseInt(port);
    }

    public static Integer parseMaxConnections(String maxConnections) {
        if (maxConnections == null || maxConnections.isBlank()) {
            return 40;
        } else {
            return Integer.parseInt(maxConnections);
        }
    }

    public static InetAddress parseIpAddress(String webhook_ip) {
        if (webhook_ip == null || webhook_ip.isBlank()) {
            return null;
        }
        try {
            return InetAddress.getByName(webhook_ip);
        } catch (UnknownHostException error) {
            logger.error(error.toString());
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
}
