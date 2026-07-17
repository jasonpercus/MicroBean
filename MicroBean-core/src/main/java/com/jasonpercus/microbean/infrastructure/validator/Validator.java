package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Interface générique pour la validation d'un objet d'instance.
 * <p>
 * <b>Utilisation :</b> L'objet à valider doit être passé au constructeur de la classe qui implémente cette interface,
 * puis stocké dans un attribut d'instance. Les méthodes de validation agissent sur cet attribut.
 * <br>
 * Le paramètre {@code T} des méthodes {@code validate(T parameter)} et {@code invalidate(T parameter)}
 * sert uniquement à fournir des données additionnelles nécessaires à la validation de l'attribut de la classe,
 * et non à représenter l'objet principal à valider.
 * </p>
 *
 * @param <T> le type de données additionnelles utilisées lors de la validation
 */
public interface Validator<T> {


    /**
     * Valide l'attribut d'instance de la classe (objet à valider),
     * en utilisant éventuellement des données additionnelles.
     *
     * @param parameter données additionnelles utilisées pour la validation (peut être null)
     * @return {@code true} si l'attribut est valide, {@code false} sinon
     */
    boolean validate(T parameter);


    /**
     * Valide l'attribut d'instance de la classe (objet à valider) sans données additionnelles.
     *
     * @return {@code true} si l'attribut est valide, {@code false} sinon
     */
    @SuppressWarnings("unused")
    default boolean validate() {
        return validate(null);
    }


    /**
     * Indique si l'attribut d'instance de la classe (objet à valider) est invalide,
     * en utilisant éventuellement des données additionnelles.
     *
     * @param parameter données additionnelles utilisées pour la validation (peut être null)
     * @return {@code true} si l'attribut est invalide, {@code false} sinon
     */
    default boolean invalidate(T parameter) {
        return !validate(parameter);
    }


    /**
     * Indique si l'attribut d'instance de la classe (objet à valider) est invalide sans données additionnelles.
     *
     * @return {@code true} si l'attribut est invalide, {@code false} sinon
     */
    default boolean invalidate() {
        return invalidate(null);
    }
}
