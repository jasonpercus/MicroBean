package com.jasonpercus.microbean.cucumber.runner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

public class SystemOuputStream {

    private PrintStream originalOut;
    private ByteArrayOutputStream buffer;
    private boolean capturing;

    public SystemOuputStream() {
        this(true);
    }

    public SystemOuputStream(boolean capturing) {
        this.capturing = capturing;
    }

    public void change() {
        if (!capturing)
            return;

        originalOut = System.out;

        buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
    }

    public void restore() {
        if (!capturing)
            return;

        System.setOut(originalOut);
    }

    public void restore(long intTimeMillis) {
        if (!capturing)
            return;

        try {
            Thread.sleep(intTimeMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
    }

    public String getContent() {
        if (!capturing)
            throw new IllegalStateException("System output capturing is disabled.");

        return buffer.toString();
    }

    public Stream<String> lines() {
        return getContent().lines();
    }
}
