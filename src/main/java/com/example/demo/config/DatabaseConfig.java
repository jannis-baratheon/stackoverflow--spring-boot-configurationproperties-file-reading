package com.example.demo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("database")
public class DatabaseConfig {
    private final String username;
    private final String password;

    public DatabaseConfig(Path username,
                          Path password) throws IOException {
        this.username = Files.readString(username);
        this.password = Files.readString(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
