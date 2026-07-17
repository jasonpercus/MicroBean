package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.MicroBean;

/**
 * Représente l'environnement d'exécution de l'application, encapsulant les arguments de la ligne de commande
 * et fournissant des méthodes d'accès aux informations contextuelles telles que le profil actif.
 */
public class Environment {

    /** Arguments de la ligne de commande passés à l'application. */
    private final Arguments arguments;

    /**
     * Crée une instance d'Environment à partir des arguments de la ligne de commande.
     *
     * @param args les arguments de la ligne de commande, ou {@code null} pour une liste vide
     */
    public Environment(String[] args) {
        this.arguments = new Arguments(args);
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
}
