package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Profile;

/**
 * Valide l'activation d'un composant en fonction du profil actif de l'application.
 * <p>
 * La validation compare le profil actif retourné par {@link MicroBean#getActiveProfile()}
 * avec la liste des profils autorisés définie par {@link Profile#value()}.
 * </p>
 */
public class ProfileValidator implements Validator<String[]> {

    /**
     * Annotation {@link Profile} portée par l'élément à valider.
     * <p>
     * Elle contient la liste des profils autorisés pour l'activation.
     * </p>
     */
    private final Profile profile;

    /**
     * Crée un validateur de profil basé sur l'annotation fournie.
     *
     * @param profile annotation de profil à utiliser pour la validation
     */
    public ProfileValidator(Profile profile) {
        this.profile = profile;
    }

    /**
     * Vérifie si le profil actif est autorisé.
     * <p>
     * La validation retourne {@code true} par défaut si le profil actif est absent/vidé,
     * ou si aucun profil autorisé n'est défini. Sinon, elle retourne {@code true}
     * uniquement si le profil actif est présent dans la liste autorisée.
     * </p>
     *
     * @param object paramètres additionnels (non utilisés dans cette implémentation)
     * @return {@code true} si le profil permet l'activation, sinon {@code false}
     */
    @Override
    public boolean validate(String[] object) {

        String activeProfile = MicroBean.getActiveProfile();
        String[] authorizedProfiles = profile.value();

        if (activeProfile == null || activeProfile.isEmpty() || authorizedProfiles == null || authorizedProfiles.length == 0)
            return true;

        for (String profile : authorizedProfiles) {
            if (profile.equals(activeProfile))
                return true;
        }

        return false;
    }
}
