package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jasonpercus.microbean.MicroBean;

/**
 * Représente l'environnement d'exécution de l'application, encapsulant les arguments de la ligne de commande
 * et fournissant des méthodes d'accès aux informations contextuelles telles que le profil actif.
 */
public class Environment {

    /** Arguments de la ligne de commande passés à l'application. */
    private final Arguments arguments;

    /** Propriétés aplaties de configuration de l'application */
    private final Map<String, Object> flatProperties;

    /** Propriétés de configuration de l'application */
    private final Map<String, Object> properties;

    /**
     * Crée une instance d'Environment à partir des arguments de la ligne de commande.
     *
     * @param args les arguments de la ligne de commande, ou {@code null} pour une liste vide
     */
    public Environment(String[] args) {
        this.arguments = new Arguments(args);
        this.flatProperties = new HashMap<>();
        this.properties = new HashMap<>();
    }

    /**
     * Retourne les arguments de la ligne de commande passés à l'application.
     *
     * @return arguments de la ligne de commande
     */
    public Arguments getArguments() {
        return arguments;
    }

    /**
     * Retourne le profil actif de l'application.
     *
     * @return le profil actif, ou {@code null} si aucun profil n'est actif
     */
    public String getProfile() {
        return MicroBean.getActiveProfile();
    }

    /**
     * Retourne les propriétés de configuration de l'application sous forme d'un objet de type {@code T}.
     *
     * @param <T> le type de l'objet de propriétés
     * @param type la classe représentant les propriétés
     * @return un objet contenant les propriétés de configuration, ou {@code null} si aucune propriété n'est trouvée
     */
    public <T> T getProperties(Class<T> type) {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(properties, type);
    }

    /**
     * Retourne les propriétés de configuration de l'application.
     *
     * @return les propriétés de configuration
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Retourne les propriétés aplaties de configuration de l'application.
     *
     * @return les propriétés de configuration
     */
    public Map<String, Object> getFlatProperties() {
        return flatProperties;
    }

    /**
     * Retourne la valeur d'une propriété de configuration spécifique.
     *
     * @param key la clé de la propriété
     * @return la valeur de la propriété, ou {@code null} si la propriété n'existe pas
     */
    public Object getProperty(String key) {
        return flatProperties.get(key);
    }

    /**
     * Pousse une propriété de configuration dans l'environnement.
     *
     * @param key la clé de la propriété
     * @param value la valeur de la propriété
     */
    public void putProperty(String key, Object value) {
        flatProperties.put(key, value);
    }

    /**
     * Pousse un ensemble de propriétés de configuration dans l'environnement.
     *
     * @param properties un ensemble de propriétés à ajouter
     */
    public void putProperties(Map<String, Object> properties) {
        if (properties == null || properties.isEmpty())
            return;

        mergeProperties(this.properties, properties);
    }

    /**
     * Fusionne récursivement les propriétés imbriquées.
     *
     * @param target la carte cible dans laquelle fusionner les propriétés
     * @param source la carte source contenant les propriétés à fusionner
     */
    @SuppressWarnings("unchecked")
    private void mergeProperties(Map<String, Object> target, Map<String, Object> source) {

        for (Map.Entry<String, Object> entry : source.entrySet()) {

            Object sourceValue = entry.getValue();
            Object targetValue = target.get(entry.getKey());

            if (sourceValue instanceof Map && targetValue instanceof Map) {
                mergeProperties((Map<String, Object>) targetValue, (Map<String, Object>) sourceValue);
            } else if (sourceValue instanceof Map) {
                Map<String, Object> nestedTarget = new HashMap<>();
                mergeProperties(nestedTarget, (Map<String, Object>) sourceValue);
                target.put(entry.getKey(), nestedTarget);
            } else {
                target.put(entry.getKey(), sourceValue);
            }
        }
    }
}
