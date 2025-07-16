package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.*;

@Controller("/graphController")
public class GraphControllerController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}