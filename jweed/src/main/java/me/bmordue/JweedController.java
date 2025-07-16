package me.bmordue;

import io.micronaut.http.annotation.*;

@Controller("/jweed")
public class JweedController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}