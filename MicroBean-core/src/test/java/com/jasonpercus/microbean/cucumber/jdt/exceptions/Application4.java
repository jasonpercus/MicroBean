package com.jasonpercus.microbean.cucumber.jdt.exceptions;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.api.MicroBeanApplication;

@MicroBeanApplication
@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class Application4 {

    public static void main(String[] args) {
        MicroBean.run(Application4.class, args, EntryPoint.class);
    }

    public static class EntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {

        }
    }
}
