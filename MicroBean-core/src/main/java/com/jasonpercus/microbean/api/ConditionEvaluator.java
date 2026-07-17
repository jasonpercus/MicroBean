package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.validator.Validator;

/**
 * Interface pour l'évaluation conditionnelle basée sur les arguments en entrée de l'application.
 * <p>
 * Cette interface hérite de {@link Validator} et permet d'implémenter des règles de validation
 * conditionnelle sur un objet d'instance, en utilisant un tableau d'arguments comme données additionnelles.
 * <p>
 * <b>Utilisation :</b> L'objet à valider doit être passé au constructeur de la classe qui implémente cette interface,
 * puis stocké dans un attribut d'instance. Les méthodes de validation agissent sur cet attribut, en utilisant
 * éventuellement les arguments de l'application en paramètre pour affiner la logique de validation.
 * <p>
 * <b>Exemple d'implémentation :</b>
 * <pre>{@code
 * public class MaConditionEvaluator implements ConditionEvaluator {
 *
 *     private final MonObjet objet;
 *
 *     public MonConditionEvaluator(MonObjet objet) {
 *         this.objet = objet;
 *     }
 *
 *     @Override
 *     public boolean validate(String[] args) {
 *         // Logique de validation conditionnelle sur objet
 *         return ...;
 *     }
 * }
 * }
 * </pre>
 *
 * @see Validator
 */
public interface ConditionEvaluator extends Validator<String[]> {

}
