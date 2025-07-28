package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestMp3ResponseDto;
import me.bmordue.redweed.service.MusicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Controller for handling music uploads.
 */
@Controller("/music")
public class MusicController {

    private static final Logger log = LoggerFactory.getLogger(MusicController.class);

    private final MusicService musicService;

    /**
     * Constructor.
     *
     * @param musicService the music service
     */
    @Inject
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    /**
     * Upload an MP3 file.
     *
     * @param file the MP3 file
     * @return the response
     * @throws IOException if an I/O error occurs
     */
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestMp3ResponseDto> upload(CompletedFileUpload file) throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".mp3");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            IngestMp3ResponseDto responseDto = musicService.ingestMp3(tempFile);
            Files.delete(tempFile.toPath());
            return HttpResponse.created(responseDto);
        } catch (IOException e) {
            log.error("I/O error while processing MP3 file", e);
            return HttpResponse.serverError();
        } catch (RuntimeException e) {
            log.error("Unexpected error while processing MP3 file", e);
            return HttpResponse.serverError();
        } finally {
            if (tempFile != null) {
                Files.delete(tempFile.toPath());
            }
        }
    }
}
