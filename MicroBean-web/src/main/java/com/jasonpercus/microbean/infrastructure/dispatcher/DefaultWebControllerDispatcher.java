package com.jasonpercus.microbean.infrastructure.dispatcher;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ControllerWeb;
import com.jasonpercus.microbean.api.HttpMethod;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.api.WebRoute;
import com.jasonpercus.microbean.component.handler.DefaultWebResponseHandler;
import com.jasonpercus.microbean.component.handler.WebResponseHandler;
import com.jasonpercus.microbean.component.invoker.DefaultWebMethodInvoker;
import com.jasonpercus.microbean.component.invoker.WebMethodInvoker;
import com.jasonpercus.microbean.component.routematcher.SpringWebRouteMatcher;
import com.jasonpercus.microbean.component.routematcher.WebRouteMatcher;
import com.jasonpercus.microbean.infrastructure.DefaultWebResponseBuilder;
import com.jasonpercus.microbean.infrastructure.WebResponse;
import com.jasonpercus.microbean.infrastructure.WebResponseBuilder;
import com.jasonpercus.microbean.infrastructure.WebRouteDefinition;
import com.jasonpercus.microbean.infrastructure.WebRouteMatchResult;
import com.jasonpercus.microbean.component.handler.WebExceptionHandler;
import com.jasonpercus.microbean.infrastructure.model.WebRouteMatch;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class DefaultWebControllerDispatcher implements WebControllerDispatcher {

    private final WebRouteMatcher routeMatcher;
    private final List<WebRouteDefinition> routes;
    private final List<WebExceptionHandler> exceptionHandlers;
    private final WebMethodInvoker methodInvoker;
    private final WebResponseBuilder responseBuilder;
    private final WebResponseHandler responseHandler;

    public DefaultWebControllerDispatcher() {
        this.routeMatcher = getRootMatcher();
        this.routes = scanRoutes();
        this.methodInvoker = getWebMethodInvoker();
        this.exceptionHandlers = getDefaultExceptionHandlers();
        this.responseBuilder = new DefaultWebResponseBuilder();
        this.responseHandler = getDefaultWebResponseHandler();
    }

    @Override
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = request.getRequestURI();

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        WebRouteMatchResult matchedRoute = findRoute(path, method);

        if (matchedRoute == null)
            return false;

        WebRouteDefinition route = matchedRoute.route();

        if (route == null)
            return false;

        try {

            Object result = methodInvoker.invoke(matchedRoute, request, response);

            if (result != null) {
                WebResponse webResponse = responseBuilder.build(result);

                responseHandler.write(webResponse, response);
            }

        } catch (Throwable throwable) {

            Throwable cause = unwrap(throwable);

            WebExceptionHandler handler = exceptionHandlers.stream()
                    .filter(h -> h.supports(cause))
                    .findFirst()
                    .orElseThrow();

            handler.handle(cause, request, response);
        }

        return true;
    }

    private List<WebRouteDefinition> scanRoutes() {

        List<WebRouteDefinition> routes = new ArrayList<>();

        for (Object controller : MicroBean.getContext().getBeansByAnnotation(ControllerWeb.class)) {

            for (Method method : controller.getClass().getDeclaredMethods()) {

                WebRoute annotation = method.getAnnotation(WebRoute.class);

                if (annotation == null)
                    continue;

                routes.add(new WebRouteDefinition(
                        annotation.path(),
                        annotation.method(),
                        controller,
                        method
                ));
            }
        }

        return routes;
    }

    private WebRouteMatchResult findRoute(String path, HttpMethod method) {

        for (WebRouteDefinition route : routes) {

            if (route.method() != method)
                continue;

            WebRouteMatch match = routeMatcher.match(route.path(), path);

            if (match.matched())
                return new WebRouteMatchResult(route, match);
        }

        return null;
    }

    private Throwable unwrap(Throwable throwable) {

        if (throwable instanceof InvocationTargetException && throwable.getCause() != null)
            return throwable.getCause();

        return throwable;
    }

    private static WebMethodInvoker getWebMethodInvoker() {
        return MicroBean.getContext().getBeanSilently(DefaultWebMethodInvoker.class);
    }

    private static WebRouteMatcher getRootMatcher() {
        return MicroBean.getContext().getBeanSilently(SpringWebRouteMatcher.class);
    }

    private static WebResponseHandler getDefaultWebResponseHandler() {
        return MicroBean.getContext().getBeanSilently(DefaultWebResponseHandler.class);
    }

    private static List<WebExceptionHandler> getDefaultExceptionHandlers() {
        return MicroBean.getContext().getBeanTypesByAnnotation(WebExtension.class)
                .stream()
                .filter(WebExceptionHandler.class::isAssignableFrom)
                .map(c -> (WebExceptionHandler) MicroBean.getContext().getBean(c))
                .filter(Objects::nonNull)
                .toList();
    }
}
