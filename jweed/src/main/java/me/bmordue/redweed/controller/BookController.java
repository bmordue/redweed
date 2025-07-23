package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestEpubResponseDto;
import me.bmordue.redweed.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller("/books")
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    @Inject
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestEpubResponseDto> upload(CompletedFileUpload file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".epub");
            try (java.io.InputStream data = file.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                data.transferTo(fos);
            }
            IngestEpubResponseDto responseDto = bookService.ingestEpub(tempFile);
            return HttpResponse.created(responseDto);
        } catch (IOException e) {
            log.error("Error processing EPUB file", e);
            return HttpResponse.serverError();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
