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
 * Indique qu'une méthode annotée doit être exécutée automatiquement
 * après l'initialisation complète de l'instance du bean par le conteneur.
 * <p>
 * Cette annotation est généralement utilisée pour effectuer des opérations
 * d'initialisation personnalisées, telles que l'ouverture de ressources,
 * la vérification de dépendances ou la configuration de l'état interne du bean.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * public class MonBean {
 *
 *     @PostConstruct
 *     public void initialiser() {
 *         // Code d'initialisation
 *     }
 *
 * }
 * }
 * </pre>
 * <p>
 * <b>Contraintes :</b>
 * <ul>
 *   <li>La méthode annotée ne doit pas avoir de paramètres.</li>
 *   <li>La méthode peut être privée, protégée ou publique.</li>
 *   <li>La méthode ne doit pas lever d'exception vérifiée.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostConstruct {

}
