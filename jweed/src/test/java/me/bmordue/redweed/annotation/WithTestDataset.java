package me.bmordue.redweed.annotation;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@MicronautTest(propertySources = "classpath:application-test.yml")
public @interface WithTestDataset {
}
