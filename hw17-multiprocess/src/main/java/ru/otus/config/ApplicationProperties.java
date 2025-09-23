package ru.otus.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ApplicationProperties {
    private static final Map<String, Object> config;

    private ApplicationProperties() {
        throw new IllegalStateException("Utility class");
    }

    static {
        try (InputStream in = ApplicationProperties.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (in == null) {
                throw new IllegalStateException("application.yml not found in resources");
            }
            Yaml yaml = new Yaml();
            config = yaml.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load application.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static String getServerHost() {
        return ((Map<String, Object>) ((Map<String, Object>) config.get("grpc")).get("server"))
                .get("host")
                .toString();
    }

    @SuppressWarnings("unchecked")
    public static int getServerPort() {
        return Integer.parseInt(((Map<String, Object>) ((Map<String, Object>) config.get("grpc")).get("server"))
                .get("port")
                .toString());
    }
}
