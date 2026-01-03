package com.filefilter.util;

import com.filefilter.model.FilterConfig;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

/**
 * Utility class for loading YAML configuration
 */
public class ConfigLoader {

    /**
     * Load FilterConfig from a YAML file in resources
     *
     * @param resourcePath Path to YAML file in resources (e.g., "filter-config.yaml")
     * @return Loaded FilterConfig object
     * @throws RuntimeException if file not found or parsing fails
     */
    public static FilterConfig load(String resourcePath) {
        try (InputStream inputStream = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + resourcePath);
            }

            // SnakeYAML 2.x requires LoaderOptions for security
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(FilterConfig.class, loaderOptions);
            Yaml yaml = new Yaml(constructor);

            return yaml.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from: " + resourcePath, e);
        }
    }
}
