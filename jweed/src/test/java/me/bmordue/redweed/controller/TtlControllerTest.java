package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestTtlResponseDto;
import me.bmordue.redweed.service.TtlService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@MicronautTest
class TtlControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    private TtlService ttlService;

    @Test
    void testIngestTtl() {
        String ttl = "@prefix schema: <https://schema.org/> .\\n" +
            "<https://www.example.com/books/1> a schema:Book ;\\n" +
            "  schema:name \\\"The Great Gatsby\\\" .\\n\"";

        when(ttlService.ingestTtl(ttl)).thenReturn(new IngestTtlResponseDto(""));

        HttpRequest<String> request = HttpRequest.POST("/ttl", ttl);
        HttpResponse<IngestTtlResponseDto> response = client.toBlocking().exchange(request, IngestTtlResponseDto.class);
        assertEquals(io.micronaut.http.HttpStatus.OK, response.getStatus());

        verify(ttlService, times(1)).ingestTtl(ttl);
    }

    @MockBean(TtlService.class)
    private TtlService ttlService() {
        return mock(TtlService.class);
    }
}
