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
 * Permet de conditionner l'activation d'un bean, d'un service, d'un adaptateur ou d'une configuration à une évaluation personnalisée.
 * <p>
 * Cette annotation s'utilise sur une méthode {@link Bean} ou une classe annotée {@link Configuration}, {@link Service} ou {@link Adapter}
 * pour n'activer le composant que si la condition définie par l'implémentation de {@link ConditionEvaluator} est validée.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * @Condition(value = MaConditionEvaluator.class, negate = false)
 * @Service
 * public class MonService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see ConditionEvaluator
 * @see Configuration
 * @see Bean
 * @see Service
 * @see Adapter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Condition {

    /**
     * Spécifie la classe d'évaluateur de condition à utiliser.
     *
     * @return la classe de l'évaluateur
     */
    Class<? extends ConditionEvaluator> value();

    /**
     * Indique si le résultat de l'évaluation doit être inversé (négation).
     * <p>
     * Si {@code true}, le composant est activé lorsque la condition échoue.
     *
     * @return {@code true} pour inverser la condition, {@code false} sinon
     */
    boolean negate() default false;
}
