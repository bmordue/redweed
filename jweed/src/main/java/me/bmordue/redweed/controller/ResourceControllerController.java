package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.*;

@Controller("/resourceController")
public class ResourceControllerController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}