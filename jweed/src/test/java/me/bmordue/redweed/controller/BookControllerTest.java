package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestEpubResponseDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class BookControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @Disabled("Implementation pending")
    void testUpload() throws IOException {
        File tempFile = File.createTempFile("test", ".epub");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("test data");
        writer.close();

        MultipartBody requestBody = MultipartBody.builder()
                .addPart("file", tempFile.getName(), MediaType.APPLICATION_OCTET_STREAM_TYPE, tempFile)
                .build();

        HttpRequest<MultipartBody> request = HttpRequest.POST("/books", requestBody)
                .contentType(MediaType.MULTIPART_FORM_DATA_TYPE);

        HttpResponse<IngestEpubResponseDto> response = client.toBlocking().exchange(request, IngestEpubResponseDto.class);

        assertEquals(201, response.getStatus().getCode());
        assertNotNull(response.body());
        assertNotNull(response.body().getBookUri());
    }
}
