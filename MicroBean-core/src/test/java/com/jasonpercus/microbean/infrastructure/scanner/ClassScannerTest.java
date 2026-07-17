package com.jasonpercus.microbean.infrastructure.scanner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.empty.SimpleClasseWithoutAnnotation;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded.AnnotationAnnotatedService;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded.AbstractServiceAnnotated;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded.ValidConcreteServiceWithPackageExcluded;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded.ServiceInterfaceAnnotated;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.invalidated.ServiceProfiledInvalid;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid.AdapterValid;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid.ClasseNotAnnotated;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid.ConfigurationValid;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid.EntryPointValid;
import com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid.ServiceValid;

@DisplayName("Tests unitaires de la classe ClassScanner")
class ClassScannerTest {

    private final String originalProfile = System.getProperty("app.profile");

    @AfterEach
    void doit_restaurer_le_profil_actif_apres_chaque_test() {

        // When
        restoreProperty(originalProfile);

        // Then
        assertThat(System.getProperty("app.profile")).isEqualTo(originalProfile);
    }

    @Test
    @DisplayName("Doit retourner les classes annotées valides dans le package ciblé")
    void doit_retourner_les_classes_annotees_valides_dans_le_package_cible() {

        // Given
        String[] packages = {"com.jasonpercus.microbean.infrastructure.scanner.fixtures.valid"};
        ClassScanner scanner = new ClassScanner(packages, new String[0]);

        // When
        Set<Class<?>> result = scanner.searchAnnotatedClass();

        // Then
        assertThat(result)
                .contains(ServiceValid.class, AdapterValid.class, ConfigurationValid.class, EntryPointValid.class)
                .doesNotContain(ClasseNotAnnotated.class);
    }

    @Test
    @DisplayName("Doit exclure les interfaces, classes abstraites et annotations même si elles sont annotées")
    void doit_exclure_les_interfaces_classes_abstraites_et_annotations_meme_si_elles_sont_annotees() {

        // Given
        String[] packages = {"com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded"};
        ClassScanner scanner = new ClassScanner(packages, new String[0]);

        // When
        Set<Class<?>> result = scanner.searchAnnotatedClass();

        // Then
        assertThat(result)
                .contains(ValidConcreteServiceWithPackageExcluded.class)
                .doesNotContain(ServiceInterfaceAnnotated.class)
                .doesNotContain(AbstractServiceAnnotated.class)
                .doesNotContain(AnnotationAnnotatedService.class);
    }

    @Test
    @DisplayName("Doit retourner un ensemble vide quand aucun composant annoté n'est trouvé")
    void doit_retourner_un_ensemble_vide_quand_aucun_composant_annote_n_est_trouve() {

        // Given
        String[] packages = {"com.jasonpercus.microbean.infrastructure.scanner.fixtures.empty"};
        ClassScanner scanner = new ClassScanner(packages, new String[0]);

        // When
        Set<Class<?>> result = scanner.searchAnnotatedClass();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(result).doesNotContain(SimpleClasseWithoutAnnotation.class);
    }

    @Test
    @DisplayName("Doit ignorer une classe annotée quand le ScanningValidator l'invalide")
    void doit_ignorer_une_classe_annotee_quand_le_scanningvalidator_l_invalide() {

        // Given
        System.setProperty("app.profile", "test");
        String[] packages = {"com.jasonpercus.microbean.infrastructure.scanner.fixtures.invalidated"};
        ClassScanner scanner = new ClassScanner(packages, new String[0]);

        // When
        Set<Class<?>> result = scanner.searchAnnotatedClass();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(result).doesNotContain(ServiceProfiledInvalid.class);
    }

    @Test
    @DisplayName("Doit couvrir complètement filterRetentionAndTarget selon retention et target")
    void doit_couvrir_completement_filterretentionandtarget_selon_retention_et_target() throws Exception {

        // Given
        Method method = ClassScanner.class.getDeclaredMethod("filterRetentionAndTarget", Class.class);
        method.setAccessible(true);

        // When
        boolean sansRetention = (boolean) method.invoke(null, SansRetentionAvecTargetType.class);
        boolean retentionClass = (boolean) method.invoke(null, RetentionClassAvecTargetType.class);
        boolean runtimeSansTarget = (boolean) method.invoke(null, RuntimeSansTarget.class);
        boolean runtimeTargetMethod = (boolean) method.invoke(null, RuntimeAvecTargetMethod.class);
        boolean runtimeTargetType = (boolean) method.invoke(null, RuntimeAvecTargetType.class);

        // Then
        assertThat(sansRetention).isFalse();
        assertThat(retentionClass).isFalse();
        assertThat(runtimeSansTarget).isFalse();
        assertThat(runtimeTargetMethod).isFalse();
        assertThat(runtimeTargetType).isTrue();
    }

    private static void restoreProperty(String value) {
        if (value == null)
            System.clearProperty("app.profile");
        else
            System.setProperty("app.profile", value);
    }

    @Target(ElementType.TYPE)
    private @interface SansRetentionAvecTargetType {
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    private @interface RetentionClassAvecTargetType {
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface RuntimeSansTarget {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface RuntimeAvecTargetMethod {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    private @interface RuntimeAvecTargetType {
    }
}
