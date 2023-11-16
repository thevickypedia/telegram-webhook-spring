package main.api;

import java.io.File;
import java.nio.file.Paths;

public class config {
    public static String env_filename = System.getenv().getOrDefault("env_file",
            System.getenv().getOrDefault("ENV_FILE", ".env"));
    // similar to os.path.join in python
    public static File env_file = new File(Paths.get(System.getProperty("user.dir"), "src", env_filename).toString());
}
