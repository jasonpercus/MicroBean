package com.jasonpercus.microbean.cucumber.steps;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.ACTIVE_PROFILE;
import static com.jasonpercus.microbean.infrastructure.Constants.AT_LEAST_ONE_APPLICATION_ENTRY_POINT_CLASS_MUST_BE_PROVIDED;
import static com.jasonpercus.microbean.infrastructure.Constants.CLASS_SHOULD_NOT_BE_ANNOTATED_WITH_ENTRY_POINT_SERVICE;
import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND;
import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_CREATED_BEAN;
import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND;
import static com.jasonpercus.microbean.infrastructure.Constants.MISSING_ENTRY_POINT_SERVICE_ON_CLASS;
import static com.jasonpercus.microbean.infrastructure.Constants.MISSING_MICRO_BEAN_APPLICATION_ON_CLASS;
import static com.jasonpercus.microbean.infrastructure.Constants.NO_BANNER_FOUND_AT_PATH;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_ADAPTER_OS_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_NEGATE_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.cucumber.jdt.beandefinition.BD_BeanMethodeFixture;
import com.jasonpercus.microbean.cucumber.jdt.beandefinition.BD_BeanNonComposantFixture;
import com.jasonpercus.microbean.cucumber.jdt.beandefinition.BD_ConfigurationFixture;
import com.jasonpercus.microbean.cucumber.jdt.beandefinition.BD_EntryPointFixture;
import com.jasonpercus.microbean.cucumber.jdt.beandefinition.BD_ServiceFixture;
import com.jasonpercus.microbean.cucumber.jdt.beanfactory.BF_Fixtures;
import com.jasonpercus.microbean.cucumber.jdt.context.C_Fixtures;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_AdapterWindows;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_BeanConditionNegate;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_BeanNominal;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_ConfigurationBeanPrivate;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_ConfigurationConditionNegate;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_ConfigurationNominal;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_ServiceNominal;
import com.jasonpercus.microbean.cucumber.jdt.processor.P_ServiceProfileDev;
import com.jasonpercus.microbean.cucumber.runner.SystemOuputStream;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import com.jasonpercus.microbean.infrastructure.factory.BeanDefinition;
import com.jasonpercus.microbean.infrastructure.factory.BeanFactory;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;
import com.jasonpercus.microbean.infrastructure.run.Processor;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;

public class MicroBeanStepdefinitions {

    public static final HashMap<String, Object> CONTEXT_DATA = new HashMap<>();

    private final String originalProfile = System.getProperty("app.profile");

    Context context;
    BeanDefinition<?> definition;
    Object beanFactoryResult;
    Set<Class<?>> classes;

    SystemOuputStream output = new SystemOuputStream(true);

    Class<?> appClass;
    Consumer<Context> contextConsumer;
    String[] args;
    Class<? extends ApplicationEntryPoint>[] entryPoints;

    Throwable caughtException;

    //region AFTER EACH
    @AfterEach
    public void resetSystemProperties() {
        if (originalProfile == null)
            System.clearProperty("app.profile");
        else
            System.setProperty("app.profile", originalProfile);
    }
    //endregion

    //region GIVEN
    @Given("une appClass nommée {string}")
    public void define_class(String appClassName) throws ClassNotFoundException {
        this.appClass = getClass(appClassName);
    }

    @Given("un contexte Processor initialisé")
    public void un_contexte_processor_initialise() {
        this.context = new Context();
        this.classes = Set.of();
        this.caughtException = null;
    }

    @Given("un contexte BeanDefinition initialisé")
    public void un_contexte_beandefinition_initialise() {
        this.context = new Context();
        this.definition = null;
        this.caughtException = null;
    }

    @Given("un contexte BeanFactory initialisé")
    public void un_contexte_beanfactory_initialise() {
        this.context = new Context();
        this.beanFactoryResult = null;
        this.caughtException = null;
        BF_Fixtures.reset();
    }

    @Given("un contexte Context initialisé")
    public void un_contexte_context_initialise() {
        this.context = new Context();
        this.beanFactoryResult = null;
        this.caughtException = null;
    }

    @Given("les classes Context suivantes sont enregistrées:")
    public void les_classes_context_suivantes_sont_enregistrees(List<String> classNames) {
        classNames.stream()
                .map(name -> {
                    try {
                        return getClass(name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(clazz -> context.register(new BeanDefinition<>(clazz, context)));
    }

    @And("la classe {string} est annotée avec {string}")
    @SuppressWarnings({"unchecked", "unused"})
    public void la_classe_est_annotee(String className, String annotationName) throws ClassNotFoundException {
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = this.appClass.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
    }

    @And("la classe {string} n'est pas annotée avec {string}")
    @SuppressWarnings({"unchecked", "unused"})
    public void la_classe_n_est_pas_annotee(String className, String annotationName) throws ClassNotFoundException {
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = this.appClass.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isFalse();
    }

    @And("il n'y a pas d'arguments")
    public void l_application_n_a_pas_d_argument() {
        this.args = new String[0];
    }

    @And("les arguments de l'application sont:")
    public void les_args_de_l_application_sont(List<String> args) {

        boolean isNotEmpty = args != null && !args.isEmpty();

        assertTrue(isNotEmpty, "args null ou vide. Privilégiez la step \"Et qu il n'y a pas d'arguments\"");

        this.args = args.toArray(new String[0]);

        saveInProperties(this.args);
    }

    @And("un consumer de Context n'est pas défini")
    public void un_consumer_de_context_n_est_pas_defini() {
        contextConsumer = null;
    }

    @And("un consumer de Context est défini avec l'action {string}")
    public void un_consumer_de_context_est_defini(String action) {
        contextConsumer = context -> {
            Runnable consumerAction = getExecutedContextConsumer(action);
            if (consumerAction != null)
                consumerAction.run();
        };
    }

    @And("les entryPoints de l'application sont:")
    @SuppressWarnings("unchecked")
    public void les_entrypoints_de_l_application_sont(List<String> entryPointNames) {
        entryPoints = entryPointNames.stream().map(name -> {
            try {
                return (Class<? extends ApplicationEntryPoint>) getClass(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).toArray(Class[]::new);
    }

    @And("le système d'exploitation est {string}")
    public void le_systemed_d_exploitation_est(String osName) {

        System.setProperty("os.name", osName.toUpperCase());
    }

    @And("aucun entryPoint n'est défini")
    @SuppressWarnings("unchecked")
    public void aucun_entry_point_n_est_defini() {
        entryPoints = new Class[0];
    }

    @And("la classe {string} est annotée avec @MicroBeanApplication et @EntryPointService")
    public void la_classe_principale_est_annotee_avec(String appClassName) {
        try {
            Class<?> c = getClass(appClassName);

            boolean isAnnotatedWithMicroBeanApplication = c.isAnnotationPresent(MicroBeanApplication.class);
            boolean isAnnotatedWithEntryPointService = c.isAnnotationPresent(EntryPointService.class);

            assertThat(isAnnotatedWithMicroBeanApplication).isTrue();
            assertThat(isAnnotatedWithEntryPointService).isTrue();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @And("les classes Processor suivantes:")
    public void les_classes_processor_suivantes(List<String> classNames) {
        this.classes = classNames.stream()
                .map(s -> {
                    try {
                        return getClass(s);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    @And("le profil actif est {string}")
    public void le_profil_actif_est(String profile) {
        System.setProperty("app.profile", profile);
    }
    //endregion

    //region WHEN
    @When("j'exécute le framework MicroBean")
    public void execution_du_framework_microbean() {
        when(() -> {
            MicroBean.setEnabledDebugMicroBean(true);
            MicroBean.run(appClass, contextConsumer, args, entryPoints);
        }, () -> {
            if (entryPointsWithLongRunning())
                output.restore(5000);
            else
                output.restore();
        });
    }

    @When("j'exécute la classe {string}")
    public void execution_de_la_classe(String className) {

        when(() -> {
            try {
                Class<?> c = getClass(className);

                CONTEXT_DATA.put("appClass", c);
                CONTEXT_DATA.put("contextConsumer", contextConsumer);
                CONTEXT_DATA.put("args", args);
                CONTEXT_DATA.put("appEntryPoint", entryPoints);

                c.getMethod("main", String[].class).invoke(null, (Object) args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof MicroBeanException m)
                    caughtException = m;
                else
                    throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                CONTEXT_DATA.clear();
            }
        }, () -> output.restore());
    }

    @When("j'exécute Processor avec les arguments:")
    public void execute_processor_avec_les_arguments(List<String> args) {
        executeProcessor(args.toArray(new String[0]));
    }

    @When("j'exécute Processor sans argument")
    public void execute_processor_sans_argument() {
        executeProcessor(new String[0]);
    }

    @When("je construis une BeanDefinition depuis la méthode bean")
    public void construit_une_beandefinition_depuis_la_methode_bean() {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            BD_ConfigurationFixture config = new BD_ConfigurationFixture();
            Method method = BD_ConfigurationFixture.class.getDeclaredMethod("creer_bean_depuis_methode");
            this.definition = new BeanDefinition<>(config, method, context);
        });
    }

    @When("je construis une BeanDefinition depuis une classe service")
    public void construit_une_beandefinition_depuis_une_classe_service() {
        this.caughtException = ThrowableAssert.catchThrowable(
                () -> this.definition = new BeanDefinition<>(BD_ServiceFixture.class, context)
        );
    }

    @When("je construis une BeanDefinition depuis une classe entrypoint")
    public void construit_une_beandefinition_depuis_une_classe_entrypoint() {
        this.caughtException = ThrowableAssert.catchThrowable(
                () -> this.definition = new BeanDefinition<>(BD_EntryPointFixture.class, context)
        );
    }

    @When("je construis une BeanDefinition depuis une méthode non bean")
    public void construit_une_beandefinition_depuis_une_methode_non_bean() {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            BD_ConfigurationFixture config = new BD_ConfigurationFixture();
            Method method = BD_ConfigurationFixture.class.getDeclaredMethod("methode_non_bean");
            this.definition = new BeanDefinition<>(config, method, context);
        });
    }

    @When("je construis une BeanDefinition depuis une classe non composant")
    public void construit_une_beandefinition_depuis_une_classe_non_composant() {
        this.caughtException = ThrowableAssert.catchThrowable(
                () -> this.definition = new BeanDefinition<>(BD_BeanNonComposantFixture.class, context)
        );
    }

    @When("je crée un bean BeanFactory depuis une méthode @Bean")
    public void cree_un_bean_beanfactory_depuis_une_methode_bean() {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            context.register(new BeanDefinition<>(BF_Fixtures.DependencyService.class, context));

            BF_Fixtures.ConfigurationFactory fixture = new BF_Fixtures.ConfigurationFactory();
            Method method = BF_Fixtures.ConfigurationFactory.class.getDeclaredMethod("createFromMethod", BF_Fixtures.Dependency.class);
            BeanFactory<BF_Fixtures.BeanFromMethod> factory = new BeanFactory<>(fixture, method, context);

            this.beanFactoryResult = factory.create();
        });
    }

    @When("je crée un bean BeanFactory depuis une classe avec constructeur prioritaire")
    public void cree_un_bean_beanfactory_depuis_une_classe_avec_constructeur_prioritaire() {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            context.register(new BeanDefinition<>(BF_Fixtures.DependencyService.class, context));
            this.beanFactoryResult = BeanFactory.create(BF_Fixtures.ConstructorMaxParamService.class, context);
        });
    }

    @When("je crée un bean BeanFactory depuis une classe avec dépendance nommée")
    public void cree_un_bean_beanfactory_depuis_une_classe_avec_dependance_nommee() {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            context.register(new BeanDefinition<>(BF_Fixtures.NamedDependencyService.class, context));
            this.beanFactoryResult = BeanFactory.create(BF_Fixtures.NamedConsumerService.class, context);
        });
    }

    @When("je crée un bean BeanFactory avec des PostConstruct hérités")
    public void cree_un_bean_beanfactory_avec_des_postconstruct_herites() {
        this.caughtException = ThrowableAssert.catchThrowable(
                () -> this.beanFactoryResult = BeanFactory.create(BF_Fixtures.MultiPostConstructService.class, context)
        );
    }

    @When("je crée un bean BeanFactory dont le PostConstruct échoue")
    public void cree_un_bean_beanfactory_dont_le_postconstruct_echoue() {
        this.caughtException = ThrowableAssert.catchThrowable(
                () -> this.beanFactoryResult = BeanFactory.create(BF_Fixtures.FailingPostConstructService.class, context)
        );
    }

    @When("je demande le bean Context par type {string}")
    public void demande_le_bean_context_par_type(String className) {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            Class<?> c = getClass(className);
            this.beanFactoryResult = context.getBean(c);
        });
    }

    @When("je demande le bean Context nommé {string} avec le type {string}")
    public void demande_le_bean_context_nomme_avec_le_type(String name, String className) {
        this.caughtException = ThrowableAssert.catchThrowable(() -> {
            Class<?> c = getClass(className);
            this.beanFactoryResult = context.getBean(c, name);
        });
    }
    //endregion

    //region THEN
    @Then("la bannière de démarrage s'affiche")
    public void la_banniere_est_affichee() {
        assertThat(output.getContent())
                .contains("===============================================================================")
                .contains("Nom...............: MicroBean")
                .contains("GroupId...........: com.jasonpercus.microbean")
                .contains("ArtifactId........: microbean-core")
                .contains("Auteur (inféré)...: Jason Percus");
    }

    @Then("la bannière personnalisée de test s'affiche")
    public void la_banniere_personnalisee_de_test_s_affiche() {
        assertThat(output.getContent())
                .contains("BANNIERE DE TEST")
                .contains("Ligne secondaire de validation");
    }

    @Then("le scanner retourne la classe {string}")
    public void le_scanner_retourne_la_classe(String classSimpleName) {
        assertOutputContainsLine("SCANNED:" + classSimpleName);
    }

    @Then("le scanner ne retourne pas la classe {string}")
    public void le_scanner_ne_retourne_pas_la_classe(String classSimpleName) {
        assertOutputNotContainsLine("SCANNED:" + classSimpleName);
    }

    @Then("le scanner retourne {int} classes")
    public void le_scanner_retourne_classes(int count) {
        assertOutputContainsLine("SCANNED_COUNT:" + count);
    }

    @Then("aucune sortie n'est affichée")
    public void aucune_sortie_n_est_affichee() {
        assertThat(output.getContent()).isBlank();
    }

    @Then("la sortie contient la ligne {string}")
    public void la_sortie_contient_la_ligne(String expectedContent) {
        assertOutputContainsLine(expectedContent);
    }

    @Then("la sortie contient {int} fois la ligne {string}")
    public void la_sortie_contient_fois_la_ligne(int expectedCount, String expectedContent) {
        long count = output.getContent().lines()
                .filter(line -> line.equals(expectedContent))
                .count();

        assertThat(count).isEqualTo(expectedCount);
    }

    @Then("un message indique que la bannière est introuvable au chemin {string}")
    public void un_message_indique_que_la_banniere_est_introuvable_au_chemin(String path) {
        assertThat(output.getContent()).contains(NO_BANNER_FOUND_AT_PATH.formatted(path));
    }

    @Then("une exception {string} est levée disant que la classe {string} n'est pas annotée avec @MicroBeanApplication")
    @SuppressWarnings("unchecked")
    public void une_exception_est_levee_disant_que_la_classe_n_est_pas_annotee_avec_MicroBeanApplication(String exceptionClassName, String className) throws ClassNotFoundException {
        assertThat(caughtException).isNotNull();

        Class<? extends Exception> expectedExceptionClass = (Class<? extends Exception>) getClass(exceptionClassName);
        assertThat(caughtException).isInstanceOf(expectedExceptionClass);

        Class<?> c = getClass(className);
        assertThat(caughtException.getMessage()).contains(MISSING_MICRO_BEAN_APPLICATION_ON_CLASS.formatted(StringHelper.abbreviateClassName(c)));
    }

    @Then("une exception {string} est levée disant que la classe principale {string} ne doit pas être annotée avec @EntryPointService")
    @SuppressWarnings("unchecked")
    public void une_exception_est_levee_disant_que_la_classe_principale_ne_doit_pas_etre_annotee_avec_EntryPointService(String exceptionClassName, String className) {
        assertThat(caughtException).isNotNull();

        try {
            Class<? extends Exception> expectedExceptionClass = (Class<? extends Exception>) getClass(exceptionClassName);
            assertThat(caughtException).isInstanceOf(expectedExceptionClass);

            Class<?> c = getClass(className);
            assertThat(caughtException.getMessage()).contains(CLASS_SHOULD_NOT_BE_ANNOTATED_WITH_ENTRY_POINT_SERVICE.formatted(StringHelper.abbreviateClassName(c)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("une exception {string} est levée disant que la classe {string} n'est pas annotée avec @EntryPointService")
    @SuppressWarnings("unchecked")
    public void une_exception_est_levee_disant_que_la_classe_n_est_pas_annotee_avec_EntryPointService(String exceptionClassName, String className) throws ClassNotFoundException {
        assertThat(caughtException).isNotNull();

        Class<? extends Exception> expectedExceptionClass = (Class<? extends Exception>) getClass(exceptionClassName);
        assertThat(caughtException).isInstanceOf(expectedExceptionClass);

        Class<?> c = getClass(className);
        assertThat(caughtException.getMessage()).contains(MISSING_ENTRY_POINT_SERVICE_ON_CLASS.formatted(StringHelper.abbreviateClassName(c)));
    }

    @And("le profil détecté et exécuté doit être {string}")
    public void le_profil_detecte_doit_etre(String profilName) {
        assertOutputContainsLine(ACTIVE_PROFILE.formatted(profilName));
    }

    @And("le consumer de Context doit être exécuté avec l'action {string}")
    public void le_context_consumer_doit_etre_execute(String action) {
        String expectedOutput;
        if ("EXECUTED".equals(action))
            expectedOutput = "Consumer de Context exécuté";
        else
            expectedOutput = "";

        assertOutputContainsLine(expectedOutput);
    }

    @And("la classe {string} est bien détectée comme {string}")
    @SuppressWarnings("unchecked")
    public void la_classe_est_bien_detectee_comme(String className, String annotationName) throws ClassNotFoundException {
        Class<?> c = getClass(className);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = c.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
        String canonicalName = StringHelper.abbreviateClassName(c);

        assertOutputContainsLine(DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND.formatted(canonicalName, annotationName.substring(1)));
    }

    @And("la classe {string} est bien détectée comme {string} mais skippée à cause de son profil")
    @SuppressWarnings("unchecked")
    public void la_classe_est_bien_detectee_mais_skippee_cause_profil(String className, String annotationName) throws ClassNotFoundException {
        Class<?> c = getClass(className);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = c.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
        String canonicalName = StringHelper.abbreviateClassName(c);

        String profil = MicroBean.getActiveProfile();

        assertOutputContainsLine(SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET.formatted(annotationName.substring(1), canonicalName, profil));
    }

    @And("la classe {string} est bien détectée comme {string} mais skippée à cause de son os")
    @SuppressWarnings("unchecked")
    public void la_classe_est_bien_detectee_mais_skippee_cause_os(String className, String annotationName) throws ClassNotFoundException {
        Class<?> c = getClass(className);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = c.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
        String canonicalName = StringHelper.abbreviateClassName(c);

        String os = System.getProperty("os.name");

        assertOutputContainsLine(SKIPPING_ADAPTER_OS_CONDITION_IS_NOT_MET.formatted(annotationName.substring(1), canonicalName, os));
    }

    @And("la classe {string} est bien détectée comme {string} mais skippée à cause de sa condition")
    @SuppressWarnings("unchecked")
    public void la_classe_est_bien_detectee_mais_skippee_cause_condition(String className, String annotationName) throws ClassNotFoundException {
        Class<?> c = getClass(className);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = c.isAnnotationPresent(annotationClass);
        Condition conditionAnnotation = c.getAnnotation(Condition.class);

        assertThat(isAnnotated).isTrue();
        String canonicalName = StringHelper.abbreviateClassName(c);

        if (conditionAnnotation.negate())
            assertOutputContainsLine(SKIPPING_COMPONENT_NEGATE_CONDITION_IS_NOT_MET.formatted(annotationName.substring(1), canonicalName));
        else
            assertOutputContainsLine(SKIPPING_COMPONENT_CONDITION_IS_NOT_MET.formatted(annotationName.substring(1), canonicalName));
    }

    @And("la méthode {string} est bien détectée comme {string}")
    @SuppressWarnings("unchecked")
    public void la_methode_est_bien_detectee_comme(String methodName, String annotationName) throws ClassNotFoundException {
        Method method = getMethod(methodName);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = method.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
        String canonicalMethodName = StringHelper.abbreviateMethodName(method.getDeclaringClass(), method);

        assertOutputContainsLine(DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND.formatted(canonicalMethodName, annotationName.substring(1)));
    }

    @And("la méthode {string} n'est pas détectée comme {string} valide")
    @SuppressWarnings("unchecked")
    public void la_methode_n_est_pas_detectee(String methodName, String annotationName) throws ClassNotFoundException {
        Method method = getMethod(methodName);
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationName);

        boolean isAnnotated = method.isAnnotationPresent(annotationClass);

        assertThat(isAnnotated).isTrue();
        String canonicalMethodName = StringHelper.abbreviateMethodName(method.getDeclaringClass(), method);
        assertOutputNotContainsLine(DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND.formatted(canonicalMethodName, annotationName.substring(1)));
    }

    @And("la classe {string} a bien son bean de créé")
    public void la_classe_a_bien_son_bean_de_cree(String className) throws ClassNotFoundException {
        Class<?> c = getClass(className);
        String canonicalName = StringHelper.abbreviateClassName(c);

        assertOutputStartLine(DEBUG_MESSAGE_CREATED_BEAN.substring(0, 23).formatted(canonicalName));
    }

    @And("la classe {string} a bien son bean de créé et injecté avec:")
    public void la_classe_a_bien_son_bean_de_cree_et_injecte_avec(String className, List<String> injectedClassNames) throws ClassNotFoundException {
        String canonicalName = StringHelper.abbreviateClassName(getClass(className));

        String injected = injectedClassNames.stream()
                .map(injectedClassName -> {
                    try {
                        return getClass(injectedClassName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(StringHelper::abbreviateClassName)
                .collect(Collectors.joining(", ", "[", "]"));

        assertOutputContainsLine(DEBUG_MESSAGE_CREATED_BEAN.formatted(canonicalName, injected));
    }

    @And("l'objet {string} retenu et injecté est bien annoté avec @Primary")
    public void objet_retenu_et_injecte_est_bien_annote_avec_primary(String className) {
        assertOutputContainsLine(className + ": OK");
    }

    @And("le premier objet {string} retenu et injecté est bien annoté avec @Named")
    public void premier_objet_retenu_et_injecte_est_bien_annote_avec_named(String className) {
        assertOutputContainsLine(className + ": 14.2");
    }

    @And("le second objet {string} retenu et injecté est bien annoté avec @Named + @Primary")
    public void second_objet_retenu_et_injecte_est_bien_annote_avec_named_primary(String className) {
        assertOutputContainsLine(className + ": 14.3");
    }

    @And("l'objet {string} retenu et injecté est bien un singleton")
    public void objet_retenu_et_injecte_est_bien_un_singleton(String className) {
        assertOutputContainsLine(className + " is a singleton");
    }

    @And("l'objet {string} retenu et injecté est bien un prototype")
    public void objet_retenu_et_injecte_est_bien_un_prototype(String className) {
        assertOutputContainsLine(className + " is a prototype");
    }

    @And("l'entrypoint {string} est bien exécutée au premier-plan")
    public void entrypoint_est_bien_executee_au_premier_plan(String entryPointName) {

        Pattern pattern = Pattern.compile("^%s is running on thread \\[(?<name>.+)]$".formatted(entryPointName));
        String threadName = output.lines()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group("name"))
                .findFirst()
                .orElse(null);

        assertThat(threadName).isNotNull();
        assertThat(threadName).isEqualToIgnoringCase("main");

        String expectedOutput = "%s is running on thread [%s]".formatted(entryPointName, threadName);
        assertOutputContainsLine(expectedOutput);
    }

    @And("l'entrypoint {string} est bien exécutée en arrière-plan")
    public void entrypoint_est_bien_executee_en_arriere_plan(String entryPointName) {

        Pattern pattern = Pattern.compile("^%s is running on thread \\[(?<name>.+)]$".formatted(entryPointName));
        String threadName = output.lines()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group("name"))
                .findFirst()
                .orElse(null);

        assertThat(threadName).isNotNull();
        assertThat(threadName).isEqualTo(threadName);
        assertThat(threadName).isNotEqualToIgnoringCase("main");

        String expectedOutput = "%s is running on thread [%s]".formatted(entryPointName, threadName);
        assertOutputContainsLine(expectedOutput);
    }

    @And("les {int} méthodes de @PostConstruct de {string} sont bien exécutées")
    public void les_methodes_de_post_construct_sont_bien_executees(int countPostConstruct, String appClassName) throws ClassNotFoundException {
        Class<?> appClass = getClass(appClassName);

        List<Method> postConstructMethods = Arrays.stream(appClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .toList();

        assertThat(postConstructMethods).hasSize(countPostConstruct);
        assertOutputContainsLine(appClass.getSimpleName() + " post init n°1");
        assertOutputContainsLine(appClass.getSimpleName() + " post init n°2");
    }

    @And("la classe {string} a bien été injectée")
    public void la_classe_a_bien_ete_injectee(String className) {
        assertOutputContainsLine(className + " is injected: true");
    }

    @Then("une exception {string} est levée disant qu'aucun entryPoint n'est défini dans la classe {string}")
    @SuppressWarnings({"unchecked", "unused"})
    public void une_exception_est_levee_disant_qu_aucun_EntryPoint_n_est_defini_dans_la_classe(String exceptionName, String className) throws ClassNotFoundException {
        assertThat(caughtException).isNotNull();

        Class<? extends Exception> expectedExceptionClass = (Class<? extends Exception>) getClass(exceptionName);
        assertThat(caughtException).isInstanceOf(expectedExceptionClass);
        assertThat(caughtException.getMessage()).contains(AT_LEAST_ONE_APPLICATION_ENTRY_POINT_CLASS_MUST_BE_PROVIDED);
    }

    @Then("le bean {string} est résolvable")
    public void le_bean_est_resolvable(String beanName) throws ClassNotFoundException {
        Class<?> beanType = getClass(beanName);
        assertThat((Object) context.getBean(beanType)).isNotNull();
    }

    @Then("le bean {string} n'est pas résolvable")
    public void le_bean_n_est_pas_resolvable(String beanName) throws ClassNotFoundException {
        Class<?> beanType = getClass(beanName);
        Throwable throwable = ThrowableAssert.catchThrowable(() -> context.getBean(beanType));
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }

    @Then("une exception Processor est levée contenant {string}")
    public void une_exception_processor_est_levee_contenant(String expectedText) {
        assertThat(caughtException).isNotNull();
        assertThat(containsInChain(caughtException, expectedText)).isTrue();
    }

    @Then("une exception {string} est levée contenant {string}")
    @SuppressWarnings("unchecked")
    public void une_exception_est_levee_contenant(String exceptionClassName, String expectedText) throws ClassNotFoundException {
        assertThat(caughtException).isNotNull();

        Class<? extends Exception> expectedExceptionClass = (Class<? extends Exception>) getClass(exceptionClassName);
        assertThat(caughtException).isInstanceOf(expectedExceptionClass);
        assertThat(containsInChain(caughtException, expectedText)).isTrue();
    }

    @Then("la BeanDefinition de méthode est construite avec les métadonnées attendues")
    public void la_beandefinition_de_methode_est_construite_avec_les_metadonnees_attendues() {
        assertThat(caughtException).isNull();
        assertThat(definition).isNotNull();
        assertThat(definition.getType()).isEqualTo(BD_BeanMethodeFixture.class);
        assertThat(definition.getName()).isEqualTo("bean-cucumber");
        assertThat(definition.isPrimary()).isTrue();
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Then("la BeanDefinition de service est construite avec les métadonnées attendues")
    public void la_beandefinition_de_service_est_construite_avec_les_metadonnees_attendues() {
        assertThat(caughtException).isNull();
        assertThat(definition).isNotNull();
        assertThat(definition.getType()).isEqualTo(BD_ServiceFixture.class);
        assertThat(definition.getName()).isEqualTo("service-cucumber");
        assertThat(definition.isPrimary()).isTrue();
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Then("la BeanDefinition d'entrypoint est construite avec les valeurs par défaut")
    public void la_beandefinition_d_entrypoint_est_construite_avec_les_valeurs_par_defaut() {
        assertThat(caughtException).isNull();
        assertThat(definition).isNotNull();
        assertThat(definition.getType()).isEqualTo(BD_EntryPointFixture.class);
        assertThat(definition.getName()).isEmpty();
        assertThat(definition.isPrimary()).isFalse();
        assertThat(definition.getScope()).isEqualTo(Scope.SINGLETON);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Then("une erreur BeanDefinition est levée contenant {string}")
    public void une_erreur_beandefinition_est_levee_contenant(String expectedText) {
        assertThat(caughtException).isNotNull();
        assertThat(containsInChain(caughtException, expectedText)).isTrue();
    }

    @Then("le bean BeanFactory issu d'une méthode contient une dépendance injectée")
    public void le_bean_beanfactory_issu_d_une_methode_contient_une_dependance_injectee() {
        assertThat(caughtException).isNull();
        assertThat(beanFactoryResult).isInstanceOf(BF_Fixtures.BeanFromMethod.class);

        BF_Fixtures.BeanFromMethod bean = (BF_Fixtures.BeanFromMethod) beanFactoryResult;
        assertThat(bean.dependency).isNotNull();
        assertThat(bean.dependency).isInstanceOf(BF_Fixtures.DependencyService.class);
    }

    @Then("le bean BeanFactory utilise le constructeur avec le plus de paramètres")
    public void le_bean_beanfactory_utilise_le_constructeur_avec_le_plus_de_parametres() {
        assertThat(caughtException).isNull();
        assertThat(beanFactoryResult).isInstanceOf(BF_Fixtures.ConstructorMaxParamService.class);

        BF_Fixtures.ConstructorMaxParamService bean = (BF_Fixtures.ConstructorMaxParamService) beanFactoryResult;
        assertThat(bean.constructorUsed).isEqualTo("MAX");
        assertThat(bean.dependency).isNotNull();
    }

    @Then("le bean BeanFactory résout correctement la dépendance nommée")
    public void le_bean_beanfactory_resout_correctement_la_dependance_nommee() {
        assertThat(caughtException).isNull();
        assertThat(beanFactoryResult).isInstanceOf(BF_Fixtures.NamedConsumerService.class);

        BF_Fixtures.NamedConsumerService bean = (BF_Fixtures.NamedConsumerService) beanFactoryResult;
        assertThat(bean.dependency).isNotNull();
        assertThat(bean.dependency).isInstanceOf(BF_Fixtures.NamedDependencyService.class);
    }

    @Then("les PostConstruct BeanFactory de la classe, de la superclasse et de l'interface sont exécutés")
    public void les_postconstruct_beanfactory_de_la_classe_de_la_superclasse_et_de_l_interface_sont_executes() {
        assertThat(caughtException).isNull();
        assertThat(beanFactoryResult).isInstanceOf(BF_Fixtures.MultiPostConstructService.class);
        assertThat(BF_Fixtures.parentCalled).isTrue();
        assertThat(BF_Fixtures.childCalled).isTrue();
        assertThat(BF_Fixtures.interfaceCalled).isTrue();
    }

    @Then("une erreur BeanFactory est levée contenant {string}")
    public void une_erreur_beanfactory_est_levee_contenant(String expectedText) {
        assertThat(caughtException).isNotNull();
        assertThat(containsInChain(caughtException, expectedText)).isTrue();
    }

    @Then("le bean Context retourné est de type {string}")
    public void le_bean_context_retourne_est_de_type(String className) throws ClassNotFoundException {
        assertThat(caughtException).isNull();
        assertThat(beanFactoryResult).isNotNull();
        assertThat(beanFactoryResult).isInstanceOf(getClass(className));
    }

    @Then("le bean Context retourne la même instance pour {string}")
    public void le_bean_context_retourne_la_meme_instance_pour(String className) throws ClassNotFoundException {

        // Given
        Class<?> c = getClass(className);

        // When
        Object bean1 = context.getBean(c);
        Object bean2 = context.getBean(c);

        // Then
        assertThat(bean1).isSameAs(bean2);
    }

    @Then("le bean Context retourne deux instances différentes pour {string}")
    public void le_bean_context_retourne_deux_instances_differentes_pour(String className) throws ClassNotFoundException {

        // Given
        Class<?> c = getClass(className);

        // When
        Object bean1 = context.getBean(c);
        Object bean2 = context.getBean(c);

        // Then
        assertThat(bean1).isNotSameAs(bean2);
    }

    @Then("une erreur Context est levée contenant {string}")
    public void une_erreur_context_est_levee_contenant(String expectedText) {
        assertThat(caughtException).isNotNull();
        assertThat(containsInChain(caughtException, expectedText)).isTrue();
    }
    //endregion

    //region UTILS
    private void when(Runnable action, Runnable howRestoreOutput) {

        if (action == null)
            return;

        output.change();

        action.run();
        howRestoreOutput.run();
    }

    private void executeProcessor(String[] args) {
        this.caughtException = ThrowableAssert.catchThrowable(() -> Processor.execute(classes, context, args));
    }

    private boolean containsInChain(Throwable throwable, String expectedText) {
        Throwable current = throwable;

        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(expectedText))
                return true;
            current = current.getCause();
        }

        return false;
    }

    private boolean entryPointsWithLongRunning() {
        boolean manyThread = false;

        if (entryPoints != null) {
            manyThread = Arrays.stream(entryPoints)
                    .filter(entryPoint -> entryPoint.isAnnotationPresent(EntryPointService.class))
                    .map(entryPoint -> entryPoint.getAnnotation(EntryPointService.class))
                    .map(EntryPointService::lifecycle)
                    .anyMatch(lifecycle -> lifecycle == LifecycleEntryPoint.LONG_RUNNING);
        }

        return manyThread;
    }

    private static void saveInProperties(String[] args) {
        Arrays.stream(args)
                .filter(arg -> arg.startsWith("-D"))
                .map(arg -> arg.substring(2).split("=", 2))
                .filter(parts -> parts.length == 2)
                .forEach(parts -> System.setProperty(parts[0], parts[1]));
    }

    private Runnable getExecutedContextConsumer(String action) {
        if ("EXECUTED".equals(action)) {
            return () -> System.out.println("Consumer de Context exécuté");
        } else {
            return null;
        }
    }

    private void assertOutputStartLine(String expectedContent) {
        boolean contains = output.getContent().lines().anyMatch(line -> line.startsWith(expectedContent));
        assertThat(contains).isTrue();
    }

    private void assertOutputContainsLine(String expectedContent) {
        boolean contains = output.getContent().lines().anyMatch(line -> line.equals(expectedContent));
        assertThat(contains).isTrue();
    }

    private void assertOutputNotContainsLine(String expectedContent) {
        boolean contains = output.getContent().lines().noneMatch(line -> line.equals(expectedContent));
        assertThat(contains).isTrue();
    }

    private Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(formatClassName(className));
    }

    private Method getMethod(String methodName) throws ClassNotFoundException {
        String classNameExtract = methodName.contains("#") ? methodName.split("#")[0] : null;
        String methodNameExtract = methodName.contains("#") ? methodName.split("#")[1] : null;

        return Arrays.stream(getClass(classNameExtract).getDeclaredMethods())
                .filter(method -> method.getName().equals(methodNameExtract))
                .findFirst()
                .orElse(null);
    }

    private String formatClassName(String className) {

        switch (className) {
            case "Application" -> {
                return "com.jasonpercus.microbean.cucumber.jdt.nominal." + className;
            }
            case "Application2", "Application3", "Application4", "EntryPointNotAnnotated" -> {
                return "com.jasonpercus.microbean.cucumber.jdt.exceptions." + className;
            }
            case "@MicroBeanApplication", "@EntryPointService", "@Configuration", "@Bean", "@Service", "@Adapter" -> {
                return "com.jasonpercus.microbean.api." + className.substring(1);
            }
            case "MicroBeanException" -> {
                return "com.jasonpercus.microbean.infrastructure.exception." + className;
            }
            case "MainService", "BackgroundService" -> {
                return "com.jasonpercus.microbean.cucumber.jdt.nominal.infrastructure." + className;
            }
            case "AE_Launcher", "AE_OneShotService", "AE_LongRunningService", "AE_SecondOneShotService" -> {
                return "com.jasonpercus.microbean.cucumber.jdt.appexecutor." + className;
            }
            case "ConfigurationNominale" -> {
                return P_ConfigurationNominal.class.getCanonicalName();
            }
            case "ServiceNominal" -> {
                return P_ServiceNominal.class.getCanonicalName();
            }
            case "BeanNominal" -> {
                return P_BeanNominal.class.getCanonicalName();
            }
            case "ServiceProfileDev" -> {
                return P_ServiceProfileDev.class.getCanonicalName();
            }
            case "AdapterWindows" -> {
                return P_AdapterWindows.class.getCanonicalName();
            }
            case "ConfigurationConditionNegatee" -> {
                return P_ConfigurationConditionNegate.class.getCanonicalName();
            }
            case "BeanConditionNegatee" -> {
                return P_BeanConditionNegate.class.getCanonicalName();
            }
            case "ConfigurationBeanPrive" -> {
                return P_ConfigurationBeanPrivate.class.getCanonicalName();
            }
            case "ContextSimpleService" -> {
                return C_Fixtures.SimpleService.class.getName();
            }
            case "ContextPrototypeService" -> {
                return C_Fixtures.PrototypeService.class.getName();
            }
            case "ContextSecondaryService" -> {
                return C_Fixtures.SecondaryService.class.getName();
            }
            case "ContextPrimaryService" -> {
                return C_Fixtures.PrimaryService.class.getName();
            }
            case "ContextNoPrimaryOneService" -> {
                return C_Fixtures.NoPrimaryOneService.class.getName();
            }
            case "ContextNoPrimaryTwoService" -> {
                return C_Fixtures.NoPrimaryTwoService.class.getName();
            }
            case "ContextContract" -> {
                return C_Fixtures.Contract.class.getName();
            }
        }

        if (Pattern.compile("^(Object\\d+)$").matcher(className).find())
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model." + className;

        if (className.startsWith("B_Application"))
            return "com.jasonpercus.microbean.cucumber.jdt.banner." + className;

        if (className.startsWith("CS_Application"))
            return "com.jasonpercus.microbean.cucumber.jdt.scanner." + className;

        if (className.startsWith("ConfigProfiled"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.config.profiled." + className;

        if (className.startsWith("ConfigConditioned"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.config.conditioned." + className;

        if (className.startsWith("ConfigPrimary"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.config.primary." + className;

        if (className.startsWith("ConfigNamed"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.config.named." + className;

        if (className.startsWith("ConfigScoped"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.config.scoped." + className;

        if (className.startsWith("ServiceConditioned"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.service.conditioned." + className;

        if (className.startsWith("ServiceNamed"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.service.named." + className;

        if (className.startsWith("ServicePrimary"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.service.primary." + className;

        if (className.startsWith("ServiceProfiled"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.service.profiled." + className;

        if (className.startsWith("ServiceScoped"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.service.scoped." + className;

        if (className.startsWith("AdapterConditioned"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.conditioned." + className;

        if (className.startsWith("AdapterNamed"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.named." + className;

        if (className.startsWith("AdapterPrimary"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.primary." + className;

        if (className.startsWith("AdapterProfiled"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.profiled." + className;

        if (className.startsWith("AdapterScoped"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.scoped." + className;

        if (className.startsWith("AdapterOS"))
            return "com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.os." + className;

        return null;
    }
    //endregion
}
