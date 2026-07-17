package com.jasonpercus.microbean.cucumber.jdt.appexecutor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.api.LifecycleEntryPoint.ONE_SHOT;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;

@EntryPointService(lifecycle = ONE_SHOT)
public class AE_SecondOneShotService implements ApplicationEntryPoint {

    @Override
    public void main(String[] args) {
        System.out.println(getClass().getSimpleName() + " is running on thread [" + Thread.currentThread().getName() + "]");
    }
}
