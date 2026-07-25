package com.jasonpercus.microbean.component.invoker;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.infrastructure.WebRouteMatchResult;
import com.jasonpercus.microbean.component.resolver.ArgumentResolver;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class DefaultWebMethodInvoker implements WebMethodInvoker {

    private final List<ArgumentResolver> argumentResolvers;

    public DefaultWebMethodInvoker() {
        argumentResolvers = MicroBean.getContext().getBeanTypesByAnnotation(WebExtension.class)
                .stream()
                .filter(ArgumentResolver.class::isAssignableFrom)
                .map(c -> (ArgumentResolver) MicroBean.getContext().getBean(c))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Object invoke(
            WebRouteMatchResult routeMatch,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        Method method = routeMatch.route().javaMethod();

        WebInvocationContext context = new WebInvocationContext(
                request,
                response,
                routeMatch.match().variables()
        );

        Object[] arguments = resolveArguments(method, context);

        return method.invoke(
                routeMatch.route().controller(),
                arguments
        );
    }

    private Object[] resolveArguments(Method method, WebInvocationContext context) {

        Parameter[] parameters = method.getParameters();

        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            Parameter parameter = parameters[i];

            arguments[i] = argumentResolvers.stream()
                    .filter(resolver -> resolver.supports(parameter))
                    .findFirst()
                    .map(resolver -> {
                        try {
                            return resolver.resolve(parameter, context);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(null);
        }

        return arguments;
    }
}
