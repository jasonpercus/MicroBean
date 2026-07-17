package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour déclarer une classe de configuration dans une application MicroBean.
 * <p>
 * Une classe annotée {@code @Configuration} sert à regrouper des méthodes de configuration
 * (souvent annotées {@link Bean}) qui produisent des beans gérés par le conteneur MicroBean.
 * Cela permet de centraliser la définition des composants applicatifs, de leurs dépendances
 * et de leur cycle de vie.
 * </p>
 *
 * <b>Exemple d'utilisation :</b>
 * <pre>
 * {@code
 * @Configuration
 * public class AppConfig {
 *
 *     @Bean
 *     public MainService mainService(OrderService orderService) {
 *         return new MainService(orderService);
 *     }
 *
 * }
 * }
 * </pre>
 *
 * @see Bean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {

}
