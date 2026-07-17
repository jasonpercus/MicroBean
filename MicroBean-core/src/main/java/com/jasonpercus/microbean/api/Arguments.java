package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour la gestion des arguments applicatifs.
 * <p>
 * Cette classe encapsule les arguments passés à l'application et fournit des méthodes pratiques
 * pour les manipuler, les rechercher et les représenter sous forme de chaîne de caractères.
 * <p>
 * Elle implémente {@link Iterable} pour permettre une itération facile sur les arguments,
 * ainsi que {@link CharSequence} pour fournir une représentation en chaîne de caractères des arguments.
 */
public class Arguments implements Iterable<String>, CharSequence {

    /** Caractère de guillemet. */
    public static final String QUOTES = "\"";

    /** Caractère d'échappement pour les guillemets. */
    public static final String ESCAPED_QUOTES = "\\\"";

    /** Caractère d'espace. */
    public static final String SPACE = " ";

    /** Liste interne des arguments applicatifs. */
    private final List<String> args;

    /**
     * Crée une instance d'Arguments à partir d'un tableau de chaînes.
     *
     * @param args les arguments de la ligne de commande, ou {@code null} pour une liste vide
     */
    public Arguments(String[] args) {
        this.args = args == null ? new ArrayList<>() : Arrays.asList(args);
    }

    /**
     * Retourne une copie défensive des arguments applicatifs.
     *
     * @return arguments passés au démarrage
     */
    public String[] getArgs() {
        return args.toArray(new String[0]);
    }

    /**
     * Retourne l'argument à l'index spécifié.
     *
     * @param index position de l'argument à retourner
     * @return argument à l'index donné
     * @throws IndexOutOfBoundsException si l'index est hors des limites de la liste d'arguments
     */
    public String getArg(int index) {
        return args.get(index);
    }

    /**
     * Retourne le nombre total d'arguments applicatifs.
     *
     * @return nombre d'arguments
     */
    public int size() {
        return args.size();
    }

    /**
     * Retourne l'index de la première occurrence de l'argument spécifié, ou -1 s'il n'est pas présent.
     *
     * @param arg argument à rechercher
     * @return index de l'argument, ou -1 s'il n'est pas trouvé
     */
    public int indexOf(String arg) {
        return args.indexOf(arg);
    }

    /**
     * Retourne l'index de la dernière occurrence de l'argument spécifié, ou -1 s'il n'est pas présent.
     *
     * @param arg argument à rechercher
     * @return index de l'argument, ou -1 s'il n'est pas trouvé
     */
    public int lastIndexOf(String arg) {
        return args.lastIndexOf(arg);
    }

    /**
     * Vérifie si l'argument spécifié est présent dans la liste d'arguments.
     *
     * @param arg argument à vérifier
     * @return {@code true} si l'argument est présent, sinon {@code false}
     */
    public boolean contains(String arg) {
        return args.contains(arg);
    }

    /**
     * Vérifie si au moins un des arguments spécifiés est présent dans la liste d'arguments.
     *
     * @param args arguments à vérifier
     * @return {@code true} si au moins un argument est présent, sinon {@code false}
     */
    public boolean containsAny(String... args) {
        for (String candidate : args) {
            if (this.args.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne l'index de la première occurrence d'un argument commençant par le préfixe spécifié,
     * ou -1 s'il n'est pas présent.
     *
     * @param prefix préfixe à rechercher
     * @return index de l'argument, ou -1 s'il n'est pas trouvé
     */
    public int indexOfArgWithPrefix(String prefix) {
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Vérifie si au moins un argument commençant par le préfixe spécifié est présent dans la liste d'arguments.
     *
     * @param prefix préfixe à vérifier
     * @return {@code true} si au moins un argument avec le préfixe est présent, sinon {@code false}
     */
    public boolean hasArgWithPrefix(String prefix) {
        return args.stream().anyMatch(arg -> arg.startsWith(prefix));
    }

    /**
     * Retourne un itérateur sur les arguments applicatifs.
     *
     * @return itérateur sur les arguments
     */
    @Override
    public Iterator<String> iterator() {
        return args.iterator();
    }

    /**
     * Retourne la longueur totale de la représentation en chaîne de caractères des arguments.
     *
     * @return longueur de la chaîne de caractères représentant les arguments
     */
    @Override
    public int length() {
        return toString().length();
    }

    /**
     * Retourne le caractère à la position spécifiée dans la représentation en chaîne de caractères des arguments.
     *
     * @param index position du caractère à retourner
     * @return caractère à l'index donné
     * @throws IndexOutOfBoundsException si l'index est hors des limites de la chaîne de caractères
     */
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    /**
     * Retourne une sous-séquence de la représentation en chaîne de caractères des arguments,
     * allant de l'index de début (inclus) à l'index de fin (exclus).
     *
     * @param start index de début de la sous-séquence (inclus)
     * @param end index de fin de la sous-séquence (exclus)
     * @return sous-séquence des arguments
     * @throws IndexOutOfBoundsException si les index sont hors des limites de la chaîne de caractères
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    /**
     * Retourne une représentation en chaîne de caractères des arguments applicatifs,
     * avec les arguments séparés par des espaces.
     *
     * @return chaîne de caractères représentant les arguments
     */
    @Override
    public String toString() {
        return args.stream()
                .map(String::trim)
                .map(arg -> arg.contains(QUOTES)
                        ? arg.replace(QUOTES, ESCAPED_QUOTES)
                        : arg)
                .map(arg -> arg.contains(SPACE)
                        ? QUOTES + arg + QUOTES
                        : arg)
                .collect(Collectors.joining(SPACE));
    }
}
