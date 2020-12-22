package com.renansouza.config;

import java.net.URI;

public class DefaultController {

    protected URI location(String root) {
        return URI.create(root);
    }

    protected URI location(String root, Long id) {
        return URI.create(root + "/" + id);
    }

}