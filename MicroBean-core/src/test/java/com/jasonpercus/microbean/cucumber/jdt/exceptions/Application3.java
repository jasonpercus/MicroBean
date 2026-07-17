package com.jasonpercus.microbean.cucumber.jdt.exceptions;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.cucumber.steps.MicroBeanStepdefinitions.CONTEXT_DATA;
import java.util.function.Consumer;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.factory.Context;

@MicroBeanApplication
public class Application3 {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        Class<?> appClass = (Class<?>) CONTEXT_DATA.get("appClass");
        Consumer<Context> contextConsumer = (Consumer<Context>) CONTEXT_DATA.get("contextConsumer");
        Class<? extends ApplicationEntryPoint>[] appEntryPoint = (Class<? extends ApplicationEntryPoint>[]) CONTEXT_DATA.get("appEntryPoint");

        MicroBean.run(appClass, contextConsumer, args, appEntryPoint);
    }
}
