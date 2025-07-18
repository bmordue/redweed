package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestEpubResponseDto;
import me.bmordue.redweed.service.BookService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller("/books")
public class BookController {

    @Inject
    private BookService bookService;

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestEpubResponseDto> upload(CompletedFileUpload file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".epub");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            IngestEpubResponseDto responseDto = bookService.ingestEpub(tempFile);
            return HttpResponse.created(responseDto);
        } catch (IOException e) {
            return HttpResponse.serverError();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
