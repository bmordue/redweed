package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record EdgeDTO(String from, String to, String label) {
}
