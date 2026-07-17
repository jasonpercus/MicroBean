package com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class Object13 {

    private final String text;

    public Object13(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.text;
    }
}
