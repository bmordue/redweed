package me.bmordue.redweed.controller;

import io.micronaut.core.annotation.Introspected;

import java.net.URI;

@Introspected
public record VCardImportRequest(URI addressbookUrl, String username, String password) {
}
