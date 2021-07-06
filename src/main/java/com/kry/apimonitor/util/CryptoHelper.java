package com.kry.apimonitor.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
class CryptoHelper {

    static String publicKey() throws IOException {
        return read("public_key.pem");
    }

    static String privateKey() throws IOException {
        return read("private_key.pem");
    }

    private static String read(String file) throws IOException {
        Path path = Paths.get(".", file);
        if (!path.toFile().exists()) {
            path = Paths.get("..", ".", file);
        }
        return String.join("\n", Files.readAllLines(path, StandardCharsets.UTF_8));
    }
}
