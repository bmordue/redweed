package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestMp3ResponseDto;
import me.bmordue.redweed.service.MusicService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller("/music")
public class MusicController {

    @Inject
    private MusicService musicService;

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestMp3ResponseDto> upload(CompletedFileUpload file) {
        try {
            File tempFile = File.createTempFile("upload-", ".mp3");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            IngestMp3ResponseDto responseDto = musicService.ingestMp3(tempFile);
            tempFile.delete();
            return HttpResponse.created(responseDto);
        } catch (IOException e) {
            return HttpResponse.serverError();
        }
    }
}
