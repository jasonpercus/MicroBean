package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Définit la portée (cycle de vie) d'un bean géré par le conteneur MicroBean.
 * <p>
 * Cette énumération décrit les stratégies de création et de gestion des instances pour les composants
 * déclarés dans l'application.
 * Elle est utilisée par le cœur du framework ainsi que par les annotations de configuration qui spécifient
 * le comportement de cycle de vie d'un bean.
 * </p>
 *
 * <h3>Valeurs</h3>
 * <ul>
 *   <li>{@link #SINGLETON} : une seule instance est créée et réutilisée par le conteneur pour l'ensemble
 *   du contexte d'application.</li>
 *   <li>{@link #PROTOTYPE} : une nouvelle instance est créée à chaque fois que le bean est requis
 *   (par injection, par demande explicite, etc.).</li>
 * </ul>
 *
 * <h3>Bonnes pratiques</h3>
 * <ul>
 *   <li>Utilisez {@code SINGLETON} pour des objets thread-safe, coûteux à créer, ou qui doivent conserver
 *   un état partagé au sein d'un même contexte.</li>
 *   <li>Utilisez {@code PROTOTYPE} lorsque chaque injection doit obtenir une instance neuve
 *   (par exemple, objets non thread-safe, ou porteurs d'état transactionnel local).</li>
 * </ul>
 *
 * @since 1.0
 */
public enum Scope {

    /**
     * Portée singleton : une seule instance est créée et réutilisée par le conteneur
     * pour l'ensemble du contexte d'application.
     */
    SINGLETON,

    /**
     * Portée prototype : une nouvelle instance est créée à chaque fois que le bean est requis.
     */
    PROTOTYPE
}
