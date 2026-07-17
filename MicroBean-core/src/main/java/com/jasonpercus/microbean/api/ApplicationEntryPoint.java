package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Point d'entrée principal d'une application MicroBean.
 * <p>
 * Cette interface doit être implémentée par toute classe souhaitant définir le démarrage
 * de l'application. Elle permet au conteneur ou au framework d'invoquer la méthode
 * {@code main(String[] args)} pour lancer l'exécution d'un service de l'application.
 * <p>
 * <b>Exemple d'implémentation :</b>
 * <pre>{@code
 * @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
 * public class MonApplication implements ApplicationEntryPoint {
 * 
 *     @Override
 *     public void main(String[] args) throws Exception {
 *         // Logique de démarrage de l'application
 *     }
 *     
 * }
 * }
 * </pre>
 */
public interface ApplicationEntryPoint {

    /**
     * Méthode principale appelée pour démarrer un service de l'application.
     * <p>
     * Cette méthode est invoquée par le conteneur ou le framework lors du lancement
     * de l'application. Elle doit contenir la logique d'initialisation et de démarrage.
     *
     * @param args les arguments de la ligne de commande
     * @throws Exception si une exception est levée lors de l'exécution du point d'entrée
     */
    void main(String[] args) throws Exception;
}
