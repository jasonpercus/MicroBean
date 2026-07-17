package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.MicroBean.PROPERTY_MICROBEAN_OS;
import static com.jasonpercus.microbean.api.OS.LINUX;
import static com.jasonpercus.microbean.api.OS.MAC;
import static com.jasonpercus.microbean.api.OS.UNKNOWN;
import static com.jasonpercus.microbean.api.OS.WINDOWS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_AIX_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_1_MAC_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_2_MAC_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_LINUX_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_NIX_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_UNIX_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.FRAGMENT_NAME_WINDOWS_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.PROPERTY_OS_NAME;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.invalidOperatingSystemOverride;
import java.util.Arrays;
import java.util.Locale;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.infrastructure.factory.BeanDefinition;

/**
 * Classe utilitaire pour la détection et la gestion du système d'exploitation.
 */
public class OperatingSystemHelper {

    /**
     * Vérifie si le bean est compatible avec le système d'exploitation actuel.
     *
     * @param beanDefinition la définition du bean à vérifier
     * @return true si le bean est compatible avec le système d'exploitation actuel, false sinon
     */
    public static boolean isCompatibleWithCurrentOS(BeanDefinition<?> beanDefinition) {
        return isCompatibleWithCurrentOS(beanDefinition.getOs());
    }

    /**
     * Vérifie si le(s) système(s) d'exploitation spécifié(s) est/sont compatible(s) avec le système d'exploitation actuel.
     *
     * @param os le(s) système(s) d'exploitation à vérifier
     * @return true si le(s) système(s) d'exploitation spécifié(s) est/sont compatible(s) avec le système d'exploitation actuel, false sinon
     */
    public static boolean isCompatibleWithCurrentOS(OS[] os) {
        OS currentOs = getCurrentOS();

        return Arrays.stream(os)
                .anyMatch(o -> o == OS.ALL || o == currentOs);
    }

    /**
     * Récupère le système d'exploitation actuel, en tenant compte d'une éventuelle surcharge via une propriété système.
     *
     * @return le système d'exploitation actuel
     */
    public static OS getCurrentOS() {
        OS overriddenOs = getOverriddenOS();
        return overriddenOs != null ? overriddenOs : detectOS();
    }

    /**
     * Détecte le système d'exploitation en se basant sur la propriété système "os.name".
     *
     * @return le système d'exploitation détecté, ou UNKNOWN si le système d'exploitation ne peut pas être déterminé
     */
    private static OS detectOS() {
        String propertyOsName = System.getProperty(PROPERTY_OS_NAME);

        if (propertyOsName == null || propertyOsName.isBlank())
            return UNKNOWN;

        String normalizedOsName = propertyOsName.toLowerCase(Locale.ROOT);

        if (isMac(normalizedOsName))
            return MAC;
        else if (isWindows(normalizedOsName))
            return WINDOWS;
        else if (isLinux(normalizedOsName))
            return LINUX;

        return UNKNOWN;
    }

    /**
     * Récupère le système d'exploitation surchargé via la propriété système "microbean.os", si elle est définie.
     *
     * @return le système d'exploitation surchargé, ou null si aucune surcharge n'est définie
     * @throws IllegalArgumentException si la valeur de la propriété "microbean.os" n'est pas un OS valide
     */
    private static OS getOverriddenOS() {
        String configuredOs = System.getProperty(PROPERTY_MICROBEAN_OS);

        if (configuredOs == null || configuredOs.isBlank())
            return null;

        try {
            return OS.valueOf(configuredOs.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw invalidOperatingSystemOverride(PROPERTY_MICROBEAN_OS, configuredOs);
        }
    }

    /**
     * Vérifie si le nom de l'OS contient un fragment indiquant Windows.
     *
     * @param propertyOsName le nom de l'OS à vérifier
     * @return true si le nom de l'OS contient un fragment indiquant Windows, false sinon
     */
    private static boolean isWindows(String propertyOsName) {
        return propertyOsName.contains(FRAGMENT_NAME_WINDOWS_OS);
    }

    /**
     * Vérifie si le nom de l'OS contient un fragment indiquant macOS.
     *
     * @param propertyOsName le nom de l'OS à vérifier
     * @return true si le nom de l'OS contient un fragment indiquant macOS, false sinon
     */
    private static boolean isMac(String propertyOsName) {
        return propertyOsName.contains(FRAGMENT_NAME_1_MAC_OS) || propertyOsName.contains(FRAGMENT_NAME_2_MAC_OS);
    }

    /**
     * Vérifie si le nom de l'OS contient un fragment indiquant Linux ou un Unix de type Linux.
     *
     * @param propertyOsName le nom de l'OS à vérifier
     * @return true si le nom de l'OS contient un fragment indiquant Linux ou un Unix de type Linux, false sinon
     */
    private static boolean isLinux(String propertyOsName) {
        return propertyOsName.contains(FRAGMENT_NAME_LINUX_OS)
                || propertyOsName.contains(FRAGMENT_NAME_UNIX_OS)
                || propertyOsName.contains(FRAGMENT_NAME_NIX_OS)
                || propertyOsName.contains(FRAGMENT_NAME_AIX_OS);
    }
}
