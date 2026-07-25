package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.api.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de la classe Context")
class ContextTest {

    private final String originalMicroBeanOs = System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS);

    @AfterEach
    @SuppressWarnings("all")
    void doit_restaurer_l_os_force_apres_chaque_test() {

        // Given
        String osAttendu = originalMicroBeanOs;

        // When
        restorePropertyMicroBeanOS(originalMicroBeanOs);

        // Then
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isEqualTo(osAttendu);
    }

    @Test
    @DisplayName("Doit retourner un bean par type quand il est enregistré")
    void doit_retourner_un_bean_par_type_quand_il_est_enregistre() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        Object bean = context.getBean(ServiceSimple.class);

        // Then
        assertThat(bean).isInstanceOf(ServiceSimple.class);
    }

    @Test
    @DisplayName("Doit retourner la même instance quand un singleton pré-instancié est enregistré")
    void doit_retourner_la_meme_instance_quand_un_singleton_pre_instancie_est_enregistre() {

        // Given
        Context context = createContext();
        Environment environment = new Environment(new String[]{"--debug"});
        context.registerSingleton(Environment.class, environment);

        // When
        Environment bean1 = context.getBean(Environment.class);
        Environment bean2 = context.getBean(Environment.class);

        // Then
        assertThat(bean1).isSameAs(environment);
        assertThat(bean2).isSameAs(environment);
        assertThat(bean1.getArguments().getArgs()).containsExactly("--debug");
    }

    @Test
    @DisplayName("Doit retourner un bean par nom et type attendu")
    void doit_retourner_un_bean_par_nom_et_type_attendu() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceNomme.class, context));

        // When
        Object bean = context.getBean(ServiceNomme.class, "service-nomme");

        // Then
        assertThat(bean).isInstanceOf(ServiceNomme.class);
    }

    @Test
    @DisplayName("Doit échouer quand aucun bean n'est trouvé pour un type")
    void doit_echouer_quand_aucun_bean_n_est_trouve_pour_un_type() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatThrownBy(() -> context.getBean(ServiceSimple.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for type");
    }

    @Test
    @DisplayName("Doit échouer quand la liste de beans par type existe mais est vide")
    void doit_echouer_quand_la_liste_de_beans_par_type_existe_mais_est_vide() throws Exception {

        // Given
        Context context = createContext();
        forceEmptyTypeBucket(context, ServiceSimple.class);

        // When & Then
        assertThatThrownBy(() -> context.getBean(ServiceSimple.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for type");
    }

    @Test
    @DisplayName("Doit échouer quand validateResolvable reçoit une liste de type vide")
    void doit_echouer_quand_validateresolvable_recoit_une_liste_de_type_vide() throws Exception {

        // Given
        Context context = createContext();
        forceEmptyTypeBucket(context, ServiceSimple.class);

        // When & Then
        assertThatThrownBy(() -> context.validateResolvable(ServiceSimple.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for type");
    }

    @Test
    @DisplayName("Doit échouer quand aucun bean n'est trouvé pour un nom")
    void doit_echouer_quand_aucun_bean_n_est_trouve_pour_un_nom() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatThrownBy(() -> context.getBean(ServiceSimple.class, "inconnu"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit échouer quand la liste de beans par nom existe mais est vide")
    void doit_echouer_quand_la_liste_de_beans_par_nom_existe_mais_est_vide() throws Exception {

        // Given
        Context context = createContext();
        forceEmptyNameBucket(context, "service-vide");

        // When & Then
        assertThatThrownBy(() -> context.getBean(ServiceSimple.class, "service-vide"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit échouer quand validateResolvable reçoit une liste de nom vide")
    void doit_echouer_quand_validateresolvable_recoit_une_liste_de_nom_vide() throws Exception {

        // Given
        Context context = createContext();
        forceEmptyNameBucket(context, "service-vide");

        // When & Then
        assertThatThrownBy(() -> context.validateResolvable(ServiceSimple.class, "service-vide"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit échouer quand le type attendu ne correspond pas au bean nommé")
    void doit_echouer_quand_le_type_attendu_ne_correspond_pas_au_bean_nomme() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceNomme.class, context));

        // When & Then
        assertThatThrownBy(() -> context.getBean(ServiceSimple.class, "service-nomme"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit échouer quand validateResolvable trouve un nom mais aucun type assignable")
    void doit_echouer_quand_validateresolvable_trouve_un_nom_mais_aucun_type_assignable() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceNomme.class, context));

        // When & Then
        assertThatThrownBy(() -> context.validateResolvable(ServiceSimple.class, "service-nomme"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit résoudre le bean primaire quand plusieurs candidats existent")
    void doit_resoudre_le_bean_primaire_quand_plusieurs_candidats_existent() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSecondaire.class, context));
        context.register(new BeanDefinition<>(ServicePrimaire.class, context));

        // When
        Object bean = context.getBean(ContratService.class);

        // Then
        assertThat(bean).isInstanceOf(ServicePrimaire.class);
    }

    @Test
    @DisplayName("Doit échouer quand plusieurs candidats existent sans primaire")
    void doit_echouer_quand_plusieurs_candidats_existent_sans_primaire() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSansPrimaryUn.class, context));
        context.register(new BeanDefinition<>(ServiceSansPrimaryDeux.class, context));

        // When & Then
        assertThatThrownBy(() -> context.getBean(ContratSansPrimary.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Multiple beans found for type");
    }

    @Test
    @DisplayName("Doit échouer quand aucun bean n'est compatible avec l'OS courant")
    void doit_echouer_quand_aucun_bean_n_est_compatible_avec_l_os_courant() {

        // Given
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, OS.LINUX.name());
        Context context = createContext();
        context.register(new BeanDefinition<>(AdapterWindows.class, context));

        // When & Then
        assertThatThrownBy(() -> context.getBean(AdapterWindows.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean matching current OS");
    }

    @Test
    @DisplayName("Doit retourner la même instance pour un singleton")
    void doit_retourner_la_meme_instance_pour_un_singleton() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        Object bean1 = context.getBean(ServiceSimple.class);
        Object bean2 = context.getBean(ServiceSimple.class);

        // Then
        assertThat(bean1).isSameAs(bean2);
    }

    @Test
    @DisplayName("Doit retourner des instances différentes pour un prototype")
    void doit_retourner_des_instances_differentes_pour_un_prototype() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServicePrototype.class, context));

        // When
        Object bean1 = context.getBean(ServicePrototype.class);
        Object bean2 = context.getBean(ServicePrototype.class);

        // Then
        assertThat(bean1).isNotSameAs(bean2);
    }

    @Test
    @DisplayName("Doit enregistrer le bean sur son interface et sa superclasse")
    void doit_enregistrer_le_bean_sur_son_interface_et_sa_superclasse() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceAvecHeritage.class, context));

        // When
        Object beanViaInterface = context.getBean(ContratHerite.class);
        Object beanViaSuperclasse = context.getBean(SuperService.class);

        // Then
        assertThat(beanViaInterface).isInstanceOf(ServiceAvecHeritage.class);
        assertThat(beanViaSuperclasse).isInstanceOf(ServiceAvecHeritage.class);
    }

    @Test
    @DisplayName("Doit enregistrer le bean sur la superclasse quand register reçoit une définition")
    void doit_enregistrer_le_bean_sur_la_superclasse_quand_register_recoit_une_definition() {

        // Given
        Context context = createContext();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceAvecHeritage.class, context);

        // When
        context.register(definition);
        Object beanViaSuperclasse = context.getBean(SuperService.class);

        // Then
        assertThat(beanViaSuperclasse).isInstanceOf(ServiceAvecHeritage.class);
    }

    @Test
    @DisplayName("Doit enregistrer le bean sur la superclasse quand register reçoit un type explicite")
    void doit_enregistrer_le_bean_sur_la_superclasse_quand_register_recoit_un_type_explicite() {

        // Given
        Context context = createContext();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceAvecHeritage.class, context);

        // When
        context.register(ServiceAvecHeritage.class, definition);
        Object beanViaSuperclasse = context.getBean(SuperService.class);

        // Then
        assertThat(beanViaSuperclasse).isInstanceOf(ServiceAvecHeritage.class);
    }

    @Test
    @DisplayName("Doit valider la résolvabilité par type et par nom")
    void doit_valider_la_resolvabilite_par_type_et_par_nom() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceNomme.class, context));

        // When
        context.validateResolvable(ServiceNomme.class);
        context.validateResolvable(ServiceNomme.class, "service-nomme");

        // Then
        assertThat((Object) context.getBean(ServiceNomme.class)).isInstanceOf(ServiceNomme.class);
        assertThat((Object) context.getBean(ServiceNomme.class, "service-nomme")).isInstanceOf(ServiceNomme.class);
    }

    @Test
    @DisplayName("Doit échouer si validateResolvable ne trouve pas le type ou le nom")
    void doit_echouer_si_validateresolvable_ne_trouve_pas_le_type_ou_le_nom() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatThrownBy(() -> context.validateResolvable(ServiceSimple.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for type");

        assertThatThrownBy(() -> context.validateResolvable(ServiceSimple.class, "inconnu"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No bean found for name");
    }

    @Test
    @DisplayName("Doit retourner les beans primaires via la méthode utilitaire")
    void doit_retourner_les_beans_primaires_via_la_methode_utilitaire() {

        // Given
        Context context = createContext();
        BeanDefinition<?> primaire = new BeanDefinition<>(ServicePrimaire.class, context);
        BeanDefinition<?> secondaire = new BeanDefinition<>(ServiceSecondaire.class, context);

        // When
        List<BeanDefinition<?>> primaires = Context.getPrimaryBeanDefinitionList(List.of(secondaire, primaire));

        // Then
        assertThat(primaires).containsExactly(primaire);
    }

    @Test
    @DisplayName("Doit filtrer les beans assignables au type attendu")
    void doit_filtrer_les_beans_assignables_au_type_attendu() {

        // Given
        Context context = createContext();
        BeanDefinition<?> service = new BeanDefinition<>(ServiceSimple.class, context);
        BeanDefinition<?> prototype = new BeanDefinition<>(ServicePrototype.class, context);

        // When
        List<BeanDefinition<?>> filtered = Context.getBeanDefinitionsAssignableToType(List.of(service, prototype), ServiceSimple.class);

        // Then
        assertThat(filtered).containsExactly(service);
    }

    @Test
    @DisplayName("Doit retourner le premier bean de la liste via la méthode utilitaire")
    void doit_retourner_le_premier_bean_de_la_liste_via_la_methode_utilitaire() {

        // Given
        Context context = createContext();
        BeanDefinition<?> first = new BeanDefinition<>(ServiceSimple.class, context);
        BeanDefinition<?> second = new BeanDefinition<>(ServicePrototype.class, context);

        // When
        BeanDefinition<?> result = Context.getFirstBeanDefinitionInList(List.of(first, second));

        // Then
        assertThat(result).isSameAs(first);
    }

    @Test
    @DisplayName("Doit retourner l'instance en cache singleton quand elle existe")
    void doit_retourner_l_instance_en_cache_singleton_quand_elle_existe() {

        // Given
        Context context = createContext();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceSimple.class, context);
        Object sentinel = new ServiceSimple();
        context.addBeanDefinitionInSingletonCache(definition, sentinel);

        // When
        Object bean = context.createSingletonBean(definition);

        // Then
        assertThat(bean).isSameAs(sentinel);
    }

    @Test
    @DisplayName("Doit retourner l'instance du second check quand le singleton devient disponible dans le lock")
    void doit_retourner_l_instance_du_second_check_quand_le_singleton_devient_disponible_dans_le_lock() {

        // Given
        ContextAvecSecondCheckSingleton context = new ContextAvecSecondCheckSingleton();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceSimple.class, context);
        context.forceSingletonAfterFirstRead(new ServiceSimple());

        // When
        Object bean = context.createSingletonBean(definition);

        // Then
        assertThat(bean).isSameAs(context.forcedSingleton);
        assertThat(context.createBeanCalled).isFalse();
    }

    @Test
    @DisplayName("Doit créer et mettre en cache un singleton quand le cache est vide")
    void doit_creer_et_mettre_en_cache_un_singleton_quand_le_cache_est_vide() {

        // Given
        Context context = createContext();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceSimple.class, context);

        // When
        Object bean = context.createSingletonBean(definition);
        Object beanInCache = context.getBeanDefinitionInSingletonCache(definition);

        // Then
        assertThat(bean).isInstanceOf(ServiceSimple.class);
        assertThat(beanInCache).isSameAs(bean);
    }

    @Test
    @DisplayName("Doit ignorer l'enregistrement de superclasse quand le type de définition est une interface")
    void doit_ignorer_l_enregistrement_de_superclasse_quand_le_type_de_definition_est_une_interface() throws Exception {

        // Given
        Context context = createContext();
        ConfigurationTypeInterface config = new ConfigurationTypeInterface();
        BeanDefinition<?> definition = new BeanDefinition<>(
                config,
                ConfigurationTypeInterface.class.getDeclaredMethod("creer_contrat"),
                context
        );

        // When
        context.register(definition);

        // Then
        assertThat((Object) context.getBean(ContratSansSuperclasse.class)).isInstanceOf(ImplementationContratSansSuperclasse.class);
    }

    @Test
    @DisplayName("Doit ignorer l'enregistrement de superclasse quand register reçoit un type interface")
    void doit_ignorer_l_enregistrement_de_superclasse_quand_register_recoit_un_type_interface() {

        // Given
        Context context = createContext();
        BeanDefinition<?> definition = new BeanDefinition<>(ServiceSimple.class, context);

        // When
        context.register(ContratSansSuperclasse.class, definition);

        // Then
        assertThat((Object) context.getBean(ContratSansSuperclasse.class)).isInstanceOf(ServiceSimple.class);
    }

    @Test
    @DisplayName("Doit créer une seule fois le singleton quand le cache était null")
    void doit_creer_une_seule_fois_le_singleton_quand_le_cache_etait_null() {

        // Given
        Context context = createContext();
        CountingSingletonService.counter = 0;
        BeanDefinition<?> definition = new BeanDefinition<>(CountingSingletonService.class, context);

        // When
        Object bean1 = context.createSingletonBean(definition);
        Object bean2 = context.createSingletonBean(definition);

        // Then
        assertThat(bean1).isSameAs(bean2);
        assertThat(CountingSingletonService.counter).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner les instances dont la classe est annotée avec l'annotation donnée")
    void doit_retourner_les_instances_dont_la_classe_est_annotee_avec_l_annotation_donnee() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        List<Object> beans = context.getBeansByAnnotation(Service.class);

        // Then
        assertThat(beans).isNotEmpty();
        assertThat(beans).allSatisfy(b -> assertThat(b).isInstanceOf(ServiceSimple.class));
    }

    @Test
    @DisplayName("Doit retourner une liste vide de beans quand aucune classe ne porte l'annotation donnée")
    void doit_retourner_liste_vide_de_beans_quand_aucune_classe_ne_porte_l_annotation_donnee() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        List<Object> beans = context.getBeansByAnnotation(Primary.class);

        // Then
        assertThat(beans).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les types dont la classe est annotée avec l'annotation donnée")
    void doit_retourner_les_types_dont_la_classe_est_annotee_avec_l_annotation_donnee() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        List<Class<?>> types = context.getBeanTypesByAnnotation(Service.class);

        // Then
        assertThat(types).isNotEmpty();
        assertThat(types).contains(ServiceSimple.class);
    }

    @Test
    @DisplayName("Doit retourner une liste vide de types quand aucune classe ne porte l'annotation donnée")
    void doit_retourner_liste_vide_de_types_quand_aucune_classe_ne_porte_l_annotation_donnee() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));

        // When
        List<Class<?>> types = context.getBeanTypesByAnnotation(Primary.class);

        // Then
        assertThat(types).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les instances de plusieurs beans annotés avec la même annotation")
    void doit_retourner_les_instances_de_plusieurs_beans_annotes_avec_la_meme_annotation() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));
        context.register(new BeanDefinition<>(ServicePrototype.class, context));

        // When
        List<Object> beans = context.getBeansByAnnotation(Service.class);

        // Then
        assertThat(beans).hasSize(2);
        assertThat(beans).anySatisfy(b -> assertThat(b).isInstanceOf(ServiceSimple.class));
        assertThat(beans).anySatisfy(b -> assertThat(b).isInstanceOf(ServicePrototype.class));
    }

    @Test
    @DisplayName("Doit retourner les types de plusieurs beans annotés avec la même annotation")
    void doit_retourner_les_types_de_plusieurs_beans_annotes_avec_la_meme_annotation() {

        // Given
        Context context = createContext();
        context.register(new BeanDefinition<>(ServiceSimple.class, context));
        context.register(new BeanDefinition<>(ServicePrototype.class, context));

        // When
        List<Class<?>> types = context.getBeanTypesByAnnotation(Service.class);

        // Then
        assertThat(types).hasSize(2);
        assertThat(types).contains(ServiceSimple.class, ServicePrototype.class);
    }

    @Test
    @DisplayName("Doit retourner un ensemble vide de classes components quand le contexte est créé sans classes")
    void doit_retourner_un_ensemble_vide_de_composants_quand_le_contexte_est_cree_sans_classes() {

        // Given
        Context context = createContext();

        // When
        Set<Class<?>> result = context.getComponentClasses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les classes components passées au constructeur")
    void doit_retourner_les_classes_components_passees_au_constructeur() {

        // Given
        TreeSet<Class<?>> components = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        components.add(ServiceSimple.class);
        components.add(ServicePrototype.class);
        Context context = new Context(components, new TreeSet<>(Comparator.comparing(Class::getCanonicalName)));

        // When
        Set<Class<?>> result = context.getComponentClasses();

        // Then
        assertThat(result).containsExactlyInAnyOrder(ServiceSimple.class, ServicePrototype.class);
    }

    @Test
    @DisplayName("Doit retourner un ensemble non modifiable pour getComponentClasses")
    void doit_retourner_un_ensemble_non_modifiable_pour_getcomponentclasses() {

        // Given
        TreeSet<Class<?>> components = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        components.add(ServiceSimple.class);
        Context context = new Context(components, new TreeSet<>(Comparator.comparing(Class::getCanonicalName)));

        // When
        Set<Class<?>> result = context.getComponentClasses();

        // Then
        assertThat(result).containsExactly(ServiceSimple.class);
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> result.add(ServicePrototype.class)
        );
    }

    @Test
    @DisplayName("Doit retourner un ensemble vide de autres classes quand le contexte est créé sans autres classes")
    void doit_retourner_un_ensemble_vide_de_autres_classes_quand_le_contexte_est_cree_sans_autres_classes() {

        // Given
        Context context = createContext();

        // When
        Set<Class<?>> result = context.getOtherClasses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les autres classes passées au constructeur")
    void doit_retourner_les_autres_classes_passees_au_constructeur() {

        // Given
        TreeSet<Class<?>> others = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        others.add(ServiceNomme.class);
        others.add(ServiceAvecHeritage.class);
        Context context = new Context(new TreeSet<>(Comparator.comparing(Class::getCanonicalName)), others);

        // When
        Set<Class<?>> result = context.getOtherClasses();

        // Then
        assertThat(result).containsExactlyInAnyOrder(ServiceNomme.class, ServiceAvecHeritage.class);
    }

    @Test
    @DisplayName("Doit retourner un ensemble non modifiable pour getOtherClasses")
    void doit_retourner_un_ensemble_non_modifiable_pour_getotherclasses() {

        // Given
        TreeSet<Class<?>> others = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        others.add(ServiceNomme.class);
        Context context = new Context(new TreeSet<>(Comparator.comparing(Class::getCanonicalName)), others);

        // When
        Set<Class<?>> result = context.getOtherClasses();

        // Then
        assertThat(result).containsExactly(ServiceNomme.class);
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> result.add(ServiceSimple.class)
        );
    }

    @Test
    @DisplayName("Doit maintenir l'indépendance entre componentClasses et otherClasses")
    void doit_maintenir_l_independance_entre_component_et_other_classes() {

        // Given
        TreeSet<Class<?>> components = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        components.add(ServiceSimple.class);
        TreeSet<Class<?>> others = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        others.add(ServiceNomme.class);
        Context context = new Context(components, others);

        // When
        Set<Class<?>> componentResult = context.getComponentClasses();
        Set<Class<?>> otherResult = context.getOtherClasses();

        // Then
        assertThat(componentResult).doesNotContain(ServiceNomme.class);
        assertThat(otherResult).doesNotContain(ServiceSimple.class);
    }

    private static void restorePropertyMicroBeanOS(String value) {
        if (value == null)
            System.clearProperty(MicroBean.PROPERTY_MICROBEAN_OS);
        else
            System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, value);
    }

    @SuppressWarnings("all")
    private static void forceEmptyTypeBucket(Context context, Class<?> type) throws Exception {
        Field field = Context.class.getDeclaredField("BEANS_BY_TYPE");
        field.setAccessible(true);
        Map<Class<?>, List<BeanDefinition<?>>> map = (Map<Class<?>, List<BeanDefinition<?>>>) field.get(context);
        map.put(type, context.createBeanList(List.class));
    }

    @SuppressWarnings("all")
    private static void forceEmptyNameBucket(Context context, String name) throws Exception {
        Field field = Context.class.getDeclaredField("BEANS_BY_NAME");
        field.setAccessible(true);
        Map<String, List<BeanDefinition<?>>> map = (Map<String, List<BeanDefinition<?>>>) field.get(context);
        map.put(name, context.createBeanList(List.class));
    }

    private static Context createContext() {
        Comparator<Class<?>> classComparator = Comparator.comparing(Class::getName);
        return new Context(new TreeSet<>(classComparator), new TreeSet<>(classComparator));
    }

    interface ContratService {
    }

    @Service
    static class ServiceSecondaire implements ContratService {
    }

    @Primary
    @Service
    static class ServicePrimaire implements ContratService {
    }

    interface ContratSansPrimary {
    }

    @Service
    static class ServiceSansPrimaryUn implements ContratSansPrimary {
    }

    @Service
    static class ServiceSansPrimaryDeux implements ContratSansPrimary {
    }

    @Service
    static class ServiceSimple {
    }

    interface ContratSansSuperclasse {
    }

    static class ImplementationContratSansSuperclasse implements ContratSansSuperclasse {
    }

    static class ConfigurationTypeInterface {

        @com.jasonpercus.microbean.api.Bean
        public ContratSansSuperclasse creer_contrat() {
            return new ImplementationContratSansSuperclasse();
        }
    }

    @Service
    static class CountingSingletonService {
        static int counter;

        CountingSingletonService() {
            counter++;
        }
    }

    @Service(name = "service-nomme")
    static class ServiceNomme {
    }

    @Service(scope = Scope.PROTOTYPE)
    static class ServicePrototype {
    }

    interface ContratHerite {
    }

    static class SuperService {
    }

    @Service
    static class ServiceAvecHeritage extends SuperService implements ContratHerite {
    }

    @Adapter(os = OS.WINDOWS)
    static class AdapterWindows {
    }

    static class ContextAvecSecondCheckSingleton extends Context {
        private int getCacheCallCount;
        Object forcedSingleton;
        boolean createBeanCalled;

        ContextAvecSecondCheckSingleton() {
            super(new TreeSet<>(Comparator.comparing(Class::getCanonicalName)), new TreeSet<>(Comparator.comparing(Class::getCanonicalName)));
        }

        void forceSingletonAfterFirstRead(Object singleton) {
            this.forcedSingleton = singleton;
            this.getCacheCallCount = 0;
            this.createBeanCalled = false;
        }

        @Override
        Object getBeanDefinitionInSingletonCache(BeanDefinition<?> def) {
            getCacheCallCount++;
            if (getCacheCallCount == 1)
                return null;
            return forcedSingleton;
        }

        @Override
        Object createBean(BeanDefinition<?> beanDefinition) {
            createBeanCalled = true;
            return super.createBean(beanDefinition);
        }
    }
}
