package com.jasonpercus.microbean.cucumber.jdt.banner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.run.Banner;

@MicroBeanApplication(showBanner = false, bannerResource = "banner-test.txt")
public class B_ApplicationDisabled {

    public static void main(String[] args) {
        Banner.show(B_ApplicationDisabled.class);
    }
}
