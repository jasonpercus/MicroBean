package ${package};

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import java.util.Arrays;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * Vérifie les contraintes d'architecture du projet via ArchUnit.
 *
 * <p>Ces règles protègent l'isolation du domaine et imposent les frontières
 * entre les couches métier (domain) et infrastructure (config/gateway).
 * L'analyse porte sur le package {@code ${package}} en excluant
 * les classes de test.</p>
 */
@AnalyzeClasses(
        packages = "${package}",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
@SuppressWarnings("unused")
public class ArchUnitTest {

    private static final String BASE_PACKAGE = "${package}";

    private static final String CONFIG = BASE_PACKAGE + ".config";
    private static final String DOMAIN = BASE_PACKAGE + ".domain";
    private static final String GATEWAY = BASE_PACKAGE + ".gateway";

    private static final String DOMAIN_API = DOMAIN + ".api";
    private static final String DOMAIN_SPI = DOMAIN + ".spi";
    private static final String DOMAIN_MODEL = DOMAIN + ".model";
    private static final String DOMAIN_SERVICE = DOMAIN + ".service";

    private static final String[] TECHNICAL_ALLOWED_PACKAGES = {
            "java..",
            "javax..",
            "jakarta..",
            "org.slf4j..",
            "com.jasonpercus.microbean.."
    };

    /**
     * Le domaine ne doit jamais dépendre des couches d'infrastructure.
     */
    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure_layers =
            noClasses()
                    .that().resideInAPackage(DOMAIN + "..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            CONFIG + "..",
                            GATEWAY + ".."
                    );

    /**
     * Les services du domaine ne doivent dépendre que des ports et types métier autorisés.
     */
    @ArchTest
    static final ArchRule domain_service_should_only_use_ports_or_domain_types =
            noClasses()
                    .that().resideInAPackage(DOMAIN_SERVICE + "..")
                    .should().dependOnClassesThat()
                    .resideOutsideOfPackages(withTechnicalAllowedPackages(
                            DOMAIN_SERVICE + "..",
                            DOMAIN_API + "..",
                            DOMAIN_SPI + "..",
                            DOMAIN_MODEL + ".."
                    ));

    /**
     * Le domaine doit rester propre et isolé de toute dépendance externe non autorisée.
     */
    @ArchTest
    static final ArchRule domain_should_be_clean_and_isolated =
            noClasses()
                    .that().resideInAPackage(DOMAIN + "..")
                    .should().dependOnClassesThat()
                    .resideOutsideOfPackages(withTechnicalAllowedPackages(DOMAIN + ".."));

    /**
     * Les ports API et SPI ne doivent pas contenir de classes concrètes.
     */
    @ArchTest
    static final ArchRule domain_api_and_spi_should_not_have_concrete_classes =
            classes()
                    .that().resideInAnyPackage(DOMAIN_API + "..", DOMAIN_SPI + "..")
                    .should(new ArchCondition<>("be an interface or an abstract class") {

                        @Override
                        public void check(JavaClass javaClass, ConditionEvents events) {

                            boolean isValid = javaClass.isInterface()
                                    || javaClass.getModifiers().contains(JavaModifier.ABSTRACT);

                            String message = "Port '" + javaClass.getFullName()
                                    + "' is a concrete class. "
                                    + "In '" + DOMAIN_API + "..' and '" + DOMAIN_SPI + "..', "
                                    + "use only interfaces or abstract classes.";

                            events.add(new SimpleConditionEvent(javaClass, isValid, message));
                        }
                    });

    /**
     * Concatène les packages métier autorisés avec les packages techniques communs.
     * <p>
     * Cette méthode permet de centraliser les packages techniques autorisés (comme java, javax,
     * com.jasonpercus.microbean) et de les ajouter facilement à chaque règle métier sans les répéter.
     *
     * @param businessAllowedPackages les packages métier spécifiques à la règle
     * @return un tableau de packages combinant les packages métier et les packages techniques autorisés
     */
    private static String[] withTechnicalAllowedPackages(String... businessAllowedPackages) {

        String[] merged = Arrays.copyOf(
                businessAllowedPackages,
                businessAllowedPackages.length + TECHNICAL_ALLOWED_PACKAGES.length
        );

        System.arraycopy(
                TECHNICAL_ALLOWED_PACKAGES,
                0,
                merged,
                businessAllowedPackages.length,
                TECHNICAL_ALLOWED_PACKAGES.length
        );

        return merged;
    }
}
