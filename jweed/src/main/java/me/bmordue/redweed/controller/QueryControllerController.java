package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.*;

@Controller("/queryController")
public class QueryControllerController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}