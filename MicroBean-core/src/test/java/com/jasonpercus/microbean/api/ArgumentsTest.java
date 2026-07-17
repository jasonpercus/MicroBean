package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Iterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Tests unitaires pour la classe Arguments")
class ArgumentsTest {

    @Test
    @DisplayName("Doit retourner une liste vide si les arguments sont null")
    void doit_retourner_une_liste_vide_si_les_arguments_sont_null() {

        // Given + When
        Arguments args = new Arguments(null);

        // Then
        assertEquals(0, args.size());
        assertEquals(0, args.getArgs().length);
    }

    @Test
    @DisplayName("Doit retourner les arguments correctement")
    void doit_retourner_les_arguments_correctement () {

        // Given
        Arguments args = new Arguments(new String[]{"a", "b", "c"});

        // When
        int size = args.size();
        String arg0 = args.getArg(0);
        String arg1 = args.getArg(1);
        String arg2 = args.getArg(2);
        String[] allArgs = args.getArgs();

        // Then
        assertThat(size).isEqualTo(3);
        assertThat(arg0).isEqualTo("a");
        assertThat(arg1).isEqualTo("b");
        assertThat(arg2).isEqualTo("c");
        assertThat(allArgs).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("Doit lever une exception si l'index est hors limites")
    void doit_lever_une_exception_si_IndexOutOfBounds() {

        // Given
        Arguments args = new Arguments(new String[]{"a"});

        // When + Then
        assertThrows(IndexOutOfBoundsException.class, () -> args.getArg(5));
    }

    @Test
    @DisplayName("Doit trouver l'index de la première et dernière occurrence d'un argument")
    void doit_trouver_element_a_index_et_dernier_index() {

        // Given
        Arguments args = new Arguments(new String[]{"a", "b", "a"});

        // When
        int firstIndex = args.indexOf("a");
        int lastIndex = args.lastIndexOf("a");
        int notFoundIndex = args.indexOf("z");

        // Then
        assertThat(firstIndex).isEqualTo(0);
        assertThat(lastIndex).isEqualTo(2);
        assertThat(notFoundIndex).isEqualTo(-1);
    }

    @ParameterizedTest
    @CsvSource({
        "x, true",
        "y, true",
        "z, false"
    })
    @DisplayName("Doit vérifier la présence d'un argument")
    void doit_tester_contains(String arg, boolean expected) {

        // Given
        Arguments args = new Arguments(new String[]{"x", "y"});

        // When
        boolean contains = args.contains(arg);

        // Then
        assertThat(contains).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "x, y, false",
        "z, b, true"
    })
    @DisplayName("Doit vérifier la présence d'au moins un argument parmi plusieurs")
    void doit_tester_containsAny(String arg1, String arg2, boolean expected) {

        // Given
        Arguments args = new Arguments(new String[]{"a", "b", "c"});

        // When
        boolean containsAny = args.containsAny(arg1, arg2);

        // Then
        assertThat(containsAny).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "--he, 0",
        "--ver, 1",
        "--nope, -1"
    })
    @DisplayName("Doit retourner l'index du premier argument commençant par un préfixe")
    void doit_retourner_index_si_element_commence_par(String prefix, int expectedIndex) {

        // Given
        Arguments args = new Arguments(new String[]{"--help", "--version", "run"});

        // When
        int index = args.indexOfArgWithPrefix(prefix);

        // Then
        assertThat(index).isEqualTo(expectedIndex);
    }

    @ParameterizedTest
    @CsvSource({
        "--h, true",
        "--x, false"
    })
    @DisplayName("Doit vérifier si au moins un argument commence par un préfixe")
    void doit_retourner_true_si_un_element_commence_par(String prefix, boolean expected) {

        // Given
        Arguments args = new Arguments(new String[]{"--help", "run"});

        // When
        boolean hasPrefix = args.hasArgWithPrefix(prefix);

        // Then
        assertThat(hasPrefix).isEqualTo(expected);
    }

    @Test
    @DisplayName("Doit permettre d'itérer sur les arguments")
    void doit_tester_iterator() {

        // Given
        Arguments args = new Arguments(new String[]{"a", "b", "c"});

        // When
        Iterator<String> it = args.iterator();

        // Then
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        assertEquals("b", it.next());
        assertEquals("c", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Doit retourner une représentation en chaîne des arguments")
    void doit_tester_toString() {

        // Given
        Arguments args = new Arguments(new String[]{"a", "b", "c"});

        // When
        String result = args.toString();

        // Then
        assertThat(result).isEqualTo("a b c");
    }

    @Test
    @DisplayName("Doit entourer les arguments avec des guillemets s'ils contiennent des espaces")
    void doit_entourer_les_arguments_avec_des_guillemets_s_ils_contiennent_des_espaces() {

        // Given
        Arguments args = new Arguments(new String[]{"hello world", "foo"});

        // When
        String result = args.toString();

        // Then
        assertThat(result.contains("\"hello world\"")).isTrue();
        assertThat(result.contains("foo")).isTrue();
    }

    @Test
    @DisplayName("Doit échapper les guillemets dans les arguments")
    void doit_echapper_les_guillemets_dans_les_arguments() {

        // Given
        Arguments args = new Arguments(new String[]{"he\"llo", "world"});

        // When
        String result = args.toString();

        // Then
        assertThat(result.contains("he\\\"llo")).isTrue();
        assertThat(result.contains("world")).isTrue();
    }

    @Test
    @DisplayName("Doit supporter les méthodes de CharSequence")
    void doit_supporter_les_methodes_de_CharSequence() {

        // Given
        CharSequence args = new Arguments(new String[]{"abc"});

        // When
        int length = args.length();
        char charAt0 = args.charAt(0);
        CharSequence subSeq = args.subSequence(0, 2);

        // Then
        assertThat(length).isEqualTo(3);
        assertThat(charAt0).isEqualTo('a');
        assertThat(subSeq).isEqualTo("ab");
    }
}
