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
 * Permet de restreindre l'activation d'un bean, d'un service, d'un adaptateur ou d'une configuration à un ou plusieurs profils d'exécution.
 * <p>
 * Cette annotation s'utilise sur une méthode {@link Bean} ou une classe annotée {@link Configuration}, {@link Service} ou {@link Adapter}
 * pour n'activer le composant que si le profil actif de l'application correspond à l'un des profils spécifiés.
 * <p>
 * Si l'annotation n'est pas présente, le composant est activé quel que soit le profil actif.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * @Profile( {"debug", "release"} )
 * @Service
 * public class DebugService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see Configuration
 * @see Bean
 * @see Service
 * @see Adapter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Profile {

    /**
     * Liste des profils pour lesquels le composant est activé.
     * <p>
     * Si le profil actif de l'application correspond à l'une des valeurs, le composant est activé.
     *
     * @return les profils autorisés
     */
    String[] value();
}
