package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;

@DisplayName("Tests unitaires de la classe Banner")
class BannerTest {

    private final String originalProfile = System.getProperty("app.profile");
    private final PrintStream originalOut = System.out;

    @AfterEach
    void doit_restaurer_les_proprietes_et_la_sortie_standard_apres_chaque_test() {

        // When
        restoreProfileProperty(originalProfile);
        System.setOut(originalOut);

        // Then
        assertThat(System.getProperty("app.profile")).isEqualTo(originalProfile);
        assertThat(System.out).isSameAs(originalOut);
    }

    @Test
    @DisplayName("Doit configurer la bannière par défaut quand la classe n'est pas annotée")
    void doit_configurer_la_banniere_par_defaut_quand_la_classe_n_est_pas_annotee() throws Exception {

        // Given
        Banner banner = new Banner(AppSansAnnotation.class);

        // When
        boolean showBanner = readShowBannerField(banner);
        String bannerPath = readBannerPathField(banner);

        // Then
        assertThat(showBanner).isTrue();
        assertThat(bannerPath).isEqualTo("banner.txt");
    }

    @Test
    @DisplayName("Doit utiliser le chemin de bannière personnalisé quand il est défini")
    void doit_utiliser_le_chemin_de_banniere_personnalise_quand_il_est_defini() throws Exception {

        // Given
        Banner banner = new Banner(AppAvecBannierePersonnalisee.class);

        // When
        boolean showBanner = readShowBannerField(banner);
        String bannerPath = readBannerPathField(banner);

        // Then
        assertThat(showBanner).isTrue();
        assertThat(bannerPath).isEqualTo("banner-test.txt");
    }

    @Test
    @DisplayName("Doit utiliser la bannière par défaut quand la ressource est vide")
    void doit_utiliser_la_banniere_par_defaut_quand_la_ressource_est_vide() throws Exception {

        // Given
        Banner banner = new Banner(AppAvecBanniereVide.class);

        // When
        String bannerPath = readBannerPathField(banner);

        // Then
        assertThat(bannerPath).isEqualTo("banner.txt");
    }

    @Test
    @DisplayName("Doit utiliser la bannière par défaut quand bannerResource vaut null")
    void doit_utiliser_la_banniere_par_defaut_quand_bannerresource_vaut_null() throws Exception {

        // Given
        Banner banner = new BannerAvecAnnotationMockeeNull(AppSansAnnotation.class);

        // When
        boolean showBanner = readShowBannerField(banner);
        String bannerPath = readBannerPathField(banner);

        // Then
        assertThat(showBanner).isTrue();
        assertThat(bannerPath).isEqualTo("banner.txt");
    }

    @Test
    @DisplayName("Doit ne rien afficher quand la bannière est désactivée")
    void doit_ne_rien_afficher_quand_la_banniere_est_desactivee() {

        // Given
        Banner banner = new Banner(AppAvecBanniereDesactivee.class);

        // When
        String output = captureOutput(banner::show);

        // Then
        assertThat(output).isBlank();
    }

    @Test
    @DisplayName("Doit afficher un message quand la ressource de bannière est introuvable")
    void doit_afficher_un_message_quand_la_ressource_de_banniere_est_introuvable() {

        // Given
        Banner banner = new Banner(AppAvecBanniereIntrouvable.class);

        // When
        String output = captureOutput(banner::show);

        // Then
        assertThat(output).contains("No banner found at path: banner-missing-test.txt");
    }

    @Test
    @DisplayName("Doit afficher la bannière et le profil inconnu quand aucun profil n'est défini")
    void doit_afficher_la_banniere_et_le_profil_inconnu_quand_aucun_profil_n_est_defini() {

        // Given
        System.clearProperty("app.profile");
        Banner banner = new Banner(AppAvecBannierePersonnalisee.class);

        // When
        String output = captureOutput(banner::show);

        // Then
        assertThat(output).contains("BANNIERE DE TEST");
        assertThat(output).contains("Active profile: [unknown]");
    }

    @Test
    @DisplayName("Doit afficher le profil inconnu quand le profil actif est vide ou blanc")
    void doit_afficher_le_profil_inconnu_quand_le_profil_actif_est_vide_ou_blanc() {

        // Given
        System.setProperty("app.profile", "   ");
        Banner banner = new Banner(AppAvecBannierePersonnalisee.class);

        // When
        String output = captureOutput(banner::show);

        // Then
        assertThat(output).contains("BANNIERE DE TEST");
        assertThat(output).contains("Active profile: [unknown]");
    }

    @Test
    @DisplayName("Doit afficher la bannière et le profil actif quand il est défini")
    void doit_afficher_la_banniere_et_le_profil_actif_quand_il_est_defini() {

        // Given
        System.setProperty("app.profile", "integration");

        // When
        String output = captureOutput(() -> Banner.show(AppAvecBannierePersonnalisee.class));

        // Then
        assertThat(output).contains("BANNIERE DE TEST");
        assertThat(output).contains("Active profile: integration");
    }

    @Test
    @DisplayName("Doit lever une MicroBeanException quand une IOException survient pendant la lecture de la bannière")
    void doit_lever_une_microbeanexception_quand_une_ioexception_survient_pendant_la_lecture_de_la_banniere() {

        // Given
        Banner banner = new BannerAvecFluxEnErreur(AppAvecBannierePersonnalisee.class);

        // When & Then
        assertThatThrownBy(banner::show)
                .isInstanceOf(MicroBeanException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private static boolean readShowBannerField(Object target) throws Exception {
        Field field = Banner.class.getDeclaredField("showBanner");
        field.setAccessible(true);
        return field.getBoolean(target);
    }

    private static String readBannerPathField(Object target) throws Exception {
        Field field = Banner.class.getDeclaredField("bannerPath");
        field.setAccessible(true);
        return (String) field.get(target);
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        System.setOut(printStream);
        try {
            action.run();
        } finally {
            printStream.flush();
            System.setOut(originalOut);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void restoreProfileProperty(String value) {
        if (value == null)
            System.clearProperty("app.profile");
        else
            System.setProperty("app.profile", value);
    }

    static class AppSansAnnotation {
    }

    @MicroBeanApplication(bannerResource = "banner-test.txt")
    static class AppAvecBannierePersonnalisee {
    }

    @MicroBeanApplication(bannerResource = "")
    static class AppAvecBanniereVide {
    }

    @MicroBeanApplication(showBanner = false, bannerResource = "banner-test.txt")
    static class AppAvecBanniereDesactivee {
    }

    @MicroBeanApplication(bannerResource = "banner-missing-test.txt")
    static class AppAvecBanniereIntrouvable {
    }

    static class BannerAvecAnnotationMockeeNull extends Banner {

        BannerAvecAnnotationMockeeNull(Class<?> appClass) {
            super(appClass);
        }

        @Override
        MicroBeanApplication getMicroBeanApplicationAnnotation(Class<?> appClass) {
            InvocationHandler handler = (proxy, method, args) -> switch (method.getName()) {
                case "showBanner" -> true;
                case "bannerResource" -> null;
                case "scanPackages" -> new String[0];
                case "bannerResource$default" -> null;
                case "annotationType" -> MicroBeanApplication.class;
                case "showBanner$default" -> true;
                case "toString" -> "@MicroBeanApplication(mockedNullBannerResource)";
                case "hashCode" -> 0;
                case "equals" -> proxy == args[0];
                default -> method.getDefaultValue();
            };

            return (MicroBeanApplication) Proxy.newProxyInstance(
                    MicroBeanApplication.class.getClassLoader(),
                    new Class[]{MicroBeanApplication.class},
                    handler
            );
        }
    }

    static class BannerAvecFluxEnErreur extends Banner {

        BannerAvecFluxEnErreur(Class<?> appClass) {
            super(appClass);
        }

        @Override
        InputStream getResourceAsStream() {
            return new InputStream() {

                @Override
                public int read() throws IOException {
                    throw new IOException("Lecture impossible");
                }
            };
        }
    }
}
