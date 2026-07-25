package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.factory.Context;

/**
 * Classe de constantes utilisées dans l'infrastructure du framework.
 */
public class Constants {

    /**
     * Localhost.
     */
    public static final String LOCALHOST = "localhost";

    /**
     * Port par défaut pour les serveurs HTTP.
     */
    public static final int LOCALHOST_PORT = 8080;

    /**
     * Nom de la propriété par défaut dans le fichier de configuration utilisé pour certains modules du framework.
     */
    public static final String DEFAULT_PROPERTY_NAME = "microbean";

    /**
     * Packages où scanner les {@link EntryPointService} des modules MicroBean.
     */
    public static final String PACKAGE_ENTRYPOINTS = "com.jasonpercus.microbean.entrypoint";

    /**
     * Packages où scanner les {@link Service} des modules MicroBean.
     */
    public static final String PACKAGE_SERVICES = "com.jasonpercus.microbean.service";

    /**
     * Packages où scanner les {@link Adapter} des modules MicroBean.
     */
    public static final String PACKAGE_ADAPTERS = "com.jasonpercus.microbean.adapter";

    /**
     * Packages où scanner les {@link Service} ou {@link Adapter} des modules MicroBean.
     */
    public static final String PACKAGE_COMPONENTS = "com.jasonpercus.microbean.component";

    /**
     * Nom de la propriété système Java contenant le nom de l'OS.
     */
    public static final String PROPERTY_OS_NAME = "os.name";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant Linux.
     */
    public static final String FRAGMENT_NAME_LINUX_OS = "linux";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant un Unix de type Linux.
     */
    public static final String FRAGMENT_NAME_UNIX_OS = "nux";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant un Unix générique.
     */
    public static final String FRAGMENT_NAME_NIX_OS = "nix";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant AIX.
     */
    public static final String FRAGMENT_NAME_AIX_OS = "aix";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant Windows.
     */
    public static final String FRAGMENT_NAME_WINDOWS_OS = "win";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant macOS (variante 1).
     */
    public static final String FRAGMENT_NAME_1_MAC_OS = "mac";

    /**
     * Fragment d'identifiant dans le nom d'OS indiquant macOS (variante 2/ Darwin).
     */
    public static final String FRAGMENT_NAME_2_MAC_OS = "darwin";

    /**
     * Message formaté affiché pour indiquer le profil actif au démarrage de l'application.
     */
    public static final String ACTIVE_PROFILE = "\uD83C\uDFF7️ Active profile: %s";

    /**
     * Message de debug utilisé pour indiquer la découverte d'une classe annotée
     * comme composant. Format args : (abbreviatedClassName, annotationSimpleName)
     */
    public static final String DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND = "\uD83D\uDD0D Component annotated found : %s => @%s";

    /**
     * Message de debug utilisé pour indiquer la découverte d'une méthode annotée
     * comme bean. Format args : (methodName, annotationSimpleName)
     */
    public static final String DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND = "\uD83D\uDD0D Method annotated found : %s => @%s";

    /**
     * Modèle de message affiché en mode debug lors de la création d'un bean.
     */
    public static final String DEBUG_MESSAGE_CREATED_BEAN = "⚙️ Created bean: %s <= %s";

    /**
     * Message formaté indiquant qu'une méthode annotée (p.ex. {@code @Bean})
     * doit être publique.
     * <p>
     * Format args : (annotationSimpleName, methodName)
     */
    public static final String METHOD_MUST_BE_PUBLIC = "❌ @%s method must be public: %s";

    /**
     * Message formaté indiquant qu'une méthode annotée (p.ex. {@code @Bean})
     * doit retourner un type non-void.
     * <p>
     * Format args : (annotationSimpleName, methodName)
     */
    public static final String METHOD_MUST_HAVE_RETURN_TYPE = "❌ @%s method must have a return type: %s";

    /**
     * Message formaté indiquant qu'une méthode n'est pas annotée avec l'annotation
     * attendue.
     * <p>
     * Format args : (methodName, annotationSimpleName)
     */
    public static final String METHOD_IS_NOT_ANNOTATED = "❌ Method %s is not annotated with @%s";

    /**
     * Message formaté utilisé quand l'invocation d'une méthode annotée échoue.
     * <p>
     * Format args : (annotationSimpleName, methodName)
     */
    public static final String FAILED_TO_INVOKE_METHOD = "❌ Failed to invoke @%s method: %s";

    /**
     * Message utilisé pour signaler une évaluation de condition ayant levé une
     * exception. Format args : (evaluatorClass)
     */
    public static final String FAILED_TO_EVALUATE_CONDITION = "❌ Failed to evaluate condition: %s";

    /**
     * Message d'erreur formaté utilisé lorsqu'aucun bean n'est trouvé pour un type.
     */
    public static final String NO_BEAN_FOUND_FOR_TYPE = "❌ No bean found for type: %s";

    /**
     * Message d'erreur formaté utilisé lorsqu'aucun bean n'est trouvé pour un nom.
     */
    public static final String NO_BEAN_FOUND_FOR_NAME = "❌ No bean found for name: %s";

    /**
     * Message d'erreur formaté utilisé lorsqu'une dépendance cyclique est détectée.
     */
    public static final String CYCLIC_DEPENDENCY_DETECTED = "❌ Cyclic dependency detected: %s";

    /**
     * Message d'erreur formaté utilisé lorsqu'il y a plusieurs beans pour un même type.
     */
    public static final String MULTIPLE_BEANS_FOUND_FOR_TYPE = "❌ Multiple beans found for type: %s";

    /**
     * Message d'erreur utilisé lorsqu'un point d'injection ne peut pas être résolu au démarrage.
     */
    public static final String UNRESOLVABLE_INJECTION_POINT = "❌ Unresolvable injection at %s for dependency %s. Use an exposed type (e.g. @" + Bean.class.getSimpleName() + " return type/interface) or declare the missing bean.";

    /**
     * Message d'erreur utilisé lorsqu'un override d'OS MicroBean est invalide.
     */
    public static final String INVALID_MICROBEAN_OS_OVERRIDE = "❌ Invalid value for system property %s: %s";

    /**
     * Message d'erreur lorsqu'aucun bean n'est compatible avec l'OS courant.
     */
    public static final String NO_BEAN_MATCHING_CURRENT_OS = "❌ No bean matching current OS";

    /**
     * Message d'erreur formaté pour l'échec d'appel d'une méthode annotée @PostConstruct.
     */
    public static final String FAILED_TO_CALL_POSTCONSTRUCT_METHOD = "❌ Failed to call @%s: %s";

    /**
     * Message d'erreur formaté lors d'un échec de création d'instance d'un bean.
     */
    public static final String FAILED_TO_CREATE_BEAN = "❌ Failed to create bean: %s";

    /**
     * Message utilisé pour signaler un échec de traitement d'une classe de
     * configuration. Format args : (configurationClass)
     */
    public static final String FAILED_TO_PROCESS_CONFIGURATION = "❌ Failed to process configuration: %s";

    /**
     * Message formaté affiché lorsqu'aucune bannière n'est trouvée au path indiqué.
     */
    public static final String NO_BANNER_FOUND_AT_PATH = "❌ No banner found at path: %s";

    /**
     * Message d'erreur utilisé lorsqu'on tente d'accéder au contexte avant son initialisation.
     */
    public static final String CONTEXT_IS_NOT_INITIALIZED = "❌ " + Context.class.getSimpleName() + " is not initialized yet. Make sure to call " + MicroBean.class.getSimpleName() + ".run() first.";

    /**
     * Message d'erreur lorsque la classe principale n'est pas annotée {@link MicroBeanApplication}.
     */
    public static final String MISSING_MICRO_BEAN_APPLICATION_ON_CLASS = "❌ Missing @" + MicroBeanApplication.class.getSimpleName() + " on %s";

    /**
     * Message d'erreur lorsqu'une classe n'est pas annotée {@link EntryPointService}.
     */
    public static final String MISSING_ENTRY_POINT_SERVICE_ON_CLASS = "❌ Missing @" + EntryPointService.class.getSimpleName() + " on %s";

    /**
     * Message d'erreur si aucun {@link ApplicationEntryPoint} n'est fourni.
     */
    public static final String AT_LEAST_ONE_APPLICATION_ENTRY_POINT_CLASS_MUST_BE_PROVIDED = "❌ At least one " + ApplicationEntryPoint.class.getSimpleName() + " class must be provided";

    /**
     * Message d'erreur formaté utilisé lorsqu'une classe est annotée avec plusieurs annotations de composant.
     */
    public static final String CLASS_IS_ANNOTATED_WITH_MULTIPLE_COMPONENT_ANNOTATIONS = "❌ Class %s is annotated with multiple component annotations";

    /**
     * Message d'erreur formaté utilisé lorsqu'une classe n'est annotée avec aucune annotation de composant.
     */
    public static final String CLASS_IS_NOT_ANNOTATED_WITH_COMPONENT_ANNOTATION = "❌ Class %s is not annotated with any component annotation";

    /**
     * Message formaté utilisé pour indiquer qu'un composant est ignoré car sa condition n'est pas remplie (negate=true).
     */
    public static final String SKIPPING_COMPONENT_NEGATE_CONDITION_IS_NOT_MET = "⚠️ Skipping @%s %s because condition is not met (negate=true)";

    /**
     * Message formaté utilisé pour indiquer qu'un composant est ignoré car sa condition n'est pas remplie (negate=false).
     */
    public static final String SKIPPING_COMPONENT_CONDITION_IS_NOT_MET = "⚠️ Skipping @%s %s because condition is not met";

    /**
     * Message formaté utilisé pour indiquer qu'un composant est ignoré car sa condition de profil actif n'est pas remplie.
     */
    public static final String SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET = "⚠️ Skipping @%s %s because condition is not met for active profile [%s]";

    /**
     * Message formaté utilisé pour indiquer qu'un composant est ignoré car sa condition d'OS n'est pas remplie.
     */
    public static final String SKIPPING_ADAPTER_OS_CONDITION_IS_NOT_MET = "⚠️ Skipping @%s %s because condition is not met for current " + OS.class.getSimpleName() + " [%s]";

    /**
     * Message d'erreur indiquant qu'il ne peut y avoir qu'un seul entry point ONE_SHOT.
     */
    public static final String ONLY_ONE_APPLICATION_ENTRY_POINT_CAN_BE_ONE_SHOT = "\uD83D\uDEAB Only one " + ApplicationEntryPoint.class.getSimpleName() + " can be ONE_SHOT";

    /**
     * Message d'erreur indiquant qu'une classe ne doit pas être annotée avec {@link EntryPointService}.
     */
    public static final String CLASS_SHOULD_NOT_BE_ANNOTATED_WITH_ENTRY_POINT_SERVICE = "\uD83D\uDEAB \"%s\" class should not be annotated with @" + EntryPointService.class.getSimpleName() + " because it is not an " + ApplicationEntryPoint.class.getSimpleName();

    /**
     * Message d'erreur indiquant qu'un path de configuration est invalide.
     */
    public static final String INVALID_PATH_FOR_CONFIGURATION_PROPERTIES = "\uD83D\uDEAB Invalid path for properties file: %s";

    /**
     * Message d'erreur indiquant qu'un fichier de configuration est introuvable.
     */
    public static final String CONFIGURATION_PROPERTIES_FILE_NOT_FOUND = "\uD83D\uDEAB Configuration properties file not found: %s";

    /**
     * Message d'erreur indiquant qu'un fichier de configuration a une extension invalide.
     */
    public static final String INVALID_FILE_EXTENSION_FOR_CONFIGURATION_PROPERTIES = "\uD83D\uDEAB Invalid configuration properties file: %s";

    /**
     * Message d'erreur indiquant qu'un fichier de configuration n'a pas pu être chargé.
     */
    public static final String FAILED_TO_LOAD_CONFIGURATION_PROPERTIES = "\uD83D\uDEAB Failed to load configuration properties: %s";

    /**
     * Message d'erreur indiquant qu'un port est déjà utilisé par une autre instance de serveur HTTP.
     */
    public static final String PORT_IS_ALREADY_USED_BY_ANOTHER_HTTP_SERVER = "❌ Port %d is already used by another HTTP server instance. Please configure a different port for module '%s'.";

    /**
     * Message d'erreur indiquant qu'un serveur HTTP n'a pas pu être démarré.
     */
    public static final String ERROR_STARTING_HTTP_SERVER = "❌ Error starting HTTP server: %s";

    /**
     * Message indiquant qu'un serveur HTTP est en cours de démarrage pour un provider donné.
     */
    public static final String STARTING_HTTP_SERVER_FOR_PROVIDER = "⚙️ Starting %s HTTP server for provider '%s' on %s:%d with context path %s";

    /**
     * Message d'erreur indiquant qu'une classe de listener HTTP n'a pas pu être instanciée.
     */
    public static final String FAILED_TO_INSTANTIATE_HTTP_REQUESTS_LISTENER_CLASS = "⚠️ Failed to instantiate HttpRequestsListener class: %s";

    /**
     * Message d'erreur indiquant qu'une classe IModule n'a pas pu être instanciée.
     */
    public static final String ERROR_INSTANTIATING_IMODULE_INIT_CLASS = "❌ Error instantiating IModuleInit class: %s";
}
