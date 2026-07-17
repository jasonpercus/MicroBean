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
 * Indique le nom explicite à utiliser pour un paramètre lors de l'injection de dépendances.
 * <p>
 * Cette annotation permet de distinguer plusieurs beans du même type lors de l'injection,
 * en spécifiant un nom unique pour le paramètre concerné.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * public class MonService {
 *
 *     public MonService(@Named("specialBean") Bean bean) {
 *         // Utilisation du bean nommé "specialBean"
 *     }
 *
 * }
 * }
 * </pre>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Named {

    /**
     * Le nom explicite à utiliser pour identifier le bean à injecter.
     * <p>
     * Ce nom doit correspondre à un bean déclaré dans le contexte d'injection.
     *
     * @return le nom du bean à injecter
     */
    String value();
}
