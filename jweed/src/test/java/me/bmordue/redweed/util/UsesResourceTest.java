package me.bmordue.redweed.util;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.net.URL;

public class UsesResourceTest {
    @NotNull
    protected File getTestResource(String resourceName) {
        URL res = getClass().getResource(resourceName);
        if (res == null) {
            Assertions.fail("Test resource not found: " + resourceName);
        }

        File file = new File(res.getFile());
        if (!file.exists()) {
            Assertions.fail("Test file not found: " + resourceName);
        }
        return file;
    }
}
