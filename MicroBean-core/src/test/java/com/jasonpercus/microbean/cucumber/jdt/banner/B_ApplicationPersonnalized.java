package com.jasonpercus.microbean.cucumber.jdt.banner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.run.Banner;

@MicroBeanApplication(bannerResource = "banner-test.txt")
public class B_ApplicationPersonnalized {

    public static void main(String[] args) {
        System.clearProperty("app.profile");
        Banner.show(B_ApplicationPersonnalized.class);
    }
}
