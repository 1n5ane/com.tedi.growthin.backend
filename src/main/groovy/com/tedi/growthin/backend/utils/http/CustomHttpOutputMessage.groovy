package com.tedi.growthin.backend.utils.http

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpOutputMessage

import java.io.OutputStream

class CustomHttpOutputMessage implements HttpOutputMessage {

    private final OutputStream body

    public CustomHttpOutputMessage(OutputStream body) {
        this.body = body
    }

    @Override
    public OutputStream getBody() {
        return body
    }

    @Override
    public HttpHeaders getHeaders() {
        return new HttpHeaders()
    }
}