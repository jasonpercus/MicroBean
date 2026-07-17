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
 * Indique qu'un bean, un service ou un adaptateur doit être privilégié lors de l'injection si plusieurs candidats sont disponibles.
 * <p>
 * Cette annotation s'utilise sur une classe annotée {@link Service} ou {@link Adapter}, ou sur une méthode annotée {@link Bean},
 * pour désigner le composant comme principal lors de la résolution des dépendances.
 * <p>
 * Si plusieurs beans ou services du même type existent, celui annoté {@code @Primary} sera injecté par défaut,
 * sauf si une annotation {@link Named} est utilisée pour lever l'ambiguïté.
 * <p>
 * <b>Bonnes pratiques :</b> Privilégiez l'utilisation de {@link Named} pour une sélection explicite lorsque cela est possible.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * @Primary
 * @Service
 * public class DefaultPaymentService implements PaymentService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see Service
 * @see Bean
 * @see Adapter
 * @see Named
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Primary {

}
