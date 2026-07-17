package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.ACTIVE_PROFILE;
import static com.jasonpercus.microbean.infrastructure.Constants.NO_BANNER_FOUND_AT_PATH;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.banner;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.info;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.trace;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;

/**
 * Gère l'affichage de la bannière de démarrage MicroBean.
 * <p>
 * Cette classe lit la configuration portée par l'annotation
 * {@link MicroBeanApplication} de la classe application afin de déterminer
 * si la bannière doit être affichée et quelle ressource utiliser.
 * </p>
 * <p>
 * En cas d'affichage, elle écrit le contenu de la bannière sur la sortie
 * standard puis affiche le profil actif courant.
 * </p>
 */
public class Banner {

    /**
     * Indique si la bannière doit être affichée.
     */
    private final boolean showBanner;

    /**
     * Chemin de la ressource bannière à charger depuis le classpath.
     */
    private final String bannerPath;

    /**
     * Construit une instance de bannière à partir de la classe application.
     * <p>
     * Si la classe n'est pas annotée {@link MicroBeanApplication}, les valeurs
     * par défaut sont utilisées ({@code showBanner=true}, {@code banner.txt}).
     * </p>
     *
     * @param appClass classe principale de l'application
     */
    Banner(Class<?> appClass) {

        MicroBeanApplication annotation = getMicroBeanApplicationAnnotation(appClass);

        if (annotation == null) {
            showBanner = true;
            bannerPath = "banner.txt";
        } else {
            String resource = annotation.bannerResource();
            showBanner = annotation.showBanner();
            bannerPath = resource == null || resource.isBlank()
                    ? "banner.txt"
                    : resource;
        }
    }

    /**
     * Point d'entrée utilitaire pour afficher rapidement la bannière d'une
     * classe application.
     *
     * @param appClass classe principale de l'application
     */
    public static void show(Class<?> appClass) {
        new Banner(appClass).show();
    }

    /**
     * Affiche la bannière et le profil actif selon la configuration.
     * <p>
     * Si l'affichage est désactivé, la méthode retourne immédiatement.
     * Si la ressource de bannière est introuvable, un message est tracé et
     * l'exécution continue sans lever d'erreur.
     * </p>
     * <p>
     * Toute {@link IOException} pendant la lecture est encapsulée dans une
     * {@link MicroBeanException}.
     * </p>
     */
    void show() {

        if (!showBanner)
            return;

        try (InputStream is = getResourceAsStream()) {

            if (is == null) {
                trace(NO_BANNER_FOUND_AT_PATH, bannerPath);
                return;
            }

            printInputStream(is);
            printActiveProfile();

        } catch (IOException e) {
            throw new MicroBeanException(e);
        }
    }

    /**
     * Récupère l'annotation {@link MicroBeanApplication} associée à la classe
     * application.
     * <p>
     * Méthode exposée en visibilité de package pour faciliter les tests.
     * </p>
     *
     * @param appClass classe à inspecter
     * @return annotation trouvée, ou {@code null} si absente
     */
    MicroBeanApplication getMicroBeanApplicationAnnotation(Class<?> appClass) {
        return appClass.getAnnotation(MicroBeanApplication.class);
    }

    /**
     * Ouvre le flux d'entrée vers la ressource bannière configurée.
     * <p>
     * Méthode exposée en visibilité de package pour faciliter les tests.
     * </p>
     *
     * @return flux d'entrée de la ressource, ou {@code null} si introuvable
     */
    InputStream getResourceAsStream() {
        return MicroBean.class.getClassLoader().getResourceAsStream(bannerPath);
    }

    /**
     * Lit et affiche le contenu d'un flux de bannière.
     *
     * @param is flux de la bannière
     * @throws IOException si la lecture du flux échoue
     */
    void printInputStream(InputStream is) throws IOException {
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        banner(content);
    }

    /**
     * Affiche la ligne de profil actif associée au démarrage.
     * <p>
     * Si le profil actif est absent ou vide, la valeur {@code [unknown]} est
     * affichée.
     * </p>
     */
    static void printActiveProfile() {
        String activeProfile = MicroBean.getActiveProfile();
        info(ACTIVE_PROFILE, activeProfile == null || activeProfile.isBlank() ? "[unknown]" : activeProfile);
    }
}
