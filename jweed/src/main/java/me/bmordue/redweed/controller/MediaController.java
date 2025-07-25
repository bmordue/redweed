package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestMp4ResponseDto;
import me.bmordue.redweed.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller("/media")
public class MediaController {
    private static final Logger log = LoggerFactory.getLogger(MediaController.class);


    private final MediaService mediaService;

    @Inject
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestMp4ResponseDto> upload(CompletedFileUpload file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".mp4");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            IngestMp4ResponseDto responseDto = mediaService.ingestMp4(tempFile);
            return HttpResponse.created(responseDto);
        } catch (IOException e) {
            log.error("Error processing MP4 file", e);
            return HttpResponse.serverError();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
