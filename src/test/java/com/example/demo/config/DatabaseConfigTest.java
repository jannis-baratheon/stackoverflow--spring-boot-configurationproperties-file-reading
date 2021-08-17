package com.example.demo.config;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.write;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DatabaseConfigTest {

    @EnableConfigurationProperties(DatabaseConfig.class)
    static class TestConfig {
    }

    private static String[] propertyPairs(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of key-value pairs.");
        }

        return IntStream.range(0, keysAndValues.length)
            .filter(ix -> ix % 2 == 0)
            .mapToObj(ix -> new SimpleImmutableEntry<>(
                keysAndValues[ix],
                keysAndValues[ix + 1]))
            .collect(toMap(
                SimpleImmutableEntry::getKey,
                SimpleImmutableEntry::getValue))
            .entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .toArray(String[]::new);
    }

    @TempDir
    Path tempDir;

    @Test
    void propertiesBindCorrectly() throws IOException {
        Path usernameFile = createTempFile(tempDir, "username", "");
        Path passwordFile = createTempFile(tempDir, "password", "");

        write(usernameFile, "bob".getBytes(StandardCharsets.UTF_8));
        write(passwordFile, "secret".getBytes(StandardCharsets.UTF_8));

        new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                propertyPairs(
                    "database.username", usernameFile,
                    "database.password", passwordFile))
            .run(context ->
                assertThat(context)
                    .getBean(DatabaseConfig.class)
                    .satisfies(databaseConfig -> assertThat(databaseConfig)
                        .extracting(DatabaseConfig::getUsername)
                        .isEqualTo("bob"))
                    .satisfies(databaseConfig -> assertThat(databaseConfig)
                        .extracting(DatabaseConfig::getPassword)
                        .isEqualTo("secret")));
    }
}
