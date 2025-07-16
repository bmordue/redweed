package me.bmordue.redweed.util;

import me.bmordue.redweed.model.domain.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class VCardParser {

    public static Map<String, String> parse(String vCardString) {
        Map<String, String> vCardMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(vCardString))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    vCardMap.put(parts[0].toUpperCase(), parts[1]);
                }
            }
        } catch (IOException e) {
            // Should not happen with StringReader
        }
        return vCardMap;
    }
}
