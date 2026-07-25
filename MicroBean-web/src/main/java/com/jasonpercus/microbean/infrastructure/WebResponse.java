package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

public class WebResponse {

    private int status;
    private String contentType;
    private Map<String, String> headers;
    private Object body;

    private WebResponse() {
        this.headers = new HashMap<>();
    }

    public WebResponse(int status, String contentType, Map<String, String> headers, Object body) {
        this.status = status;
        this.contentType = contentType;
        this.headers = headers == null ? new HashMap<>() : new HashMap<>(headers);
        this.body = body;
    }

    public int status() {
        return status;
    }

    public String contentType() {
        return contentType;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Object body() {
        return body;
    }

    public WebResponse status(int status) {
        this.status = status;
        return this;
    }

    public WebResponse contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public WebResponse headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public WebResponse header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public WebResponse body(Object body) {
        this.body = body;
        return this;
    }

    public static WebResponse ok(Object body) {
        return new WebResponse()
                .status(HttpServletResponse.SC_OK)
                .body(body);
    }

    public static WebResponse ok(Object body, String contentType) {
        return new WebResponse()
                .status(HttpServletResponse.SC_OK)
                .contentType(contentType)
                .body(body);
    }

    public static WebResponse download(Object body, String filename) {
        return download(body, "application/octet-stream", filename);
    }

    public static WebResponse download(Object body, String contentType, String filename) {
        return new WebResponse()
                .status(HttpServletResponse.SC_OK)
                .contentType(contentType)
                .header("Content-Disposition", "attachment; filename=\"%s\"".formatted(filename))
                .body(body);
    }

    public static WebResponse created(String location, Object body) {
        return new WebResponse()
                .status(HttpServletResponse.SC_CREATED)
                .header("Location", location)
                .body(body);
    }

    public static WebResponse status(int status, Object body) {
        return new WebResponse()
                .status(status)
                .body(body);
    }

    public static WebResponse status(int status, String contentType, Object body) {
        return new WebResponse()
                .status(status)
                .contentType(contentType)
                .body(body);
    }
}
