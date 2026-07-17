package com.jasonpercus.microbean.cucumber.jdt.nominal.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;

@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class BackgroundService implements ApplicationEntryPoint {

    @Override
    public void main(String[] args) {
        System.out.println(getClass().getSimpleName() + " is running on thread [%s]".formatted(Thread.currentThread().getName()));
    }
}
