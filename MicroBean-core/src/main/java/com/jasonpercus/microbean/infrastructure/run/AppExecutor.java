package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.api.LifecycleEntryPoint.ONE_SHOT;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.onlyOneApplicationEntryPointCanBeOneShot;
import static java.lang.Thread.MAX_PRIORITY;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.error;

/**
 * Orchestre le chargement et l'exécution des {@link ApplicationEntryPoint} de l'application.
 * <p>
 * Cette classe récupère chaque entry point depuis le {@link Context}, exécute éventuellement
 * un {@code Consumer<Context>} fourni par l'appelant, détermine le cycle de vie déclaré via
 * {@link EntryPointService}, puis lance l'exécution du point d'entrée.
 * </p>
 * <p>
 * Deux modes d'exécution sont pris en charge :
 * </p>
 * <ul>
 *   <li>{@link LifecycleEntryPoint#ONE_SHOT} : exécution immédiate sur le thread appelant ;</li>
 *   <li>{@link LifecycleEntryPoint#LONG_RUNNING} : exécution sur un thread dédié non daemon.</li>
 * </ul>
 */
public class AppExecutor {

    /**
     * Charge puis exécute l'ensemble des entry points fournis.
     * <p>
     * Chaque classe d'entry point est résolue depuis le {@link Context}, puis exécutée selon
     * son cycle de vie. Un compteur dédié garantit qu'au plus un seul entry point
     * {@link LifecycleEntryPoint#ONE_SHOT} est autorisé dans une même exécution.
     * </p>
     *
     * @param contextConsumer consommateur optionnel exécuté avant chaque lancement d'entry point
     * @param args            arguments applicatifs transmis aux entry points
     * @param appEntryPoint   classes des entry points à exécuter
     * @param context         contexte depuis lequel résoudre les entry points
     * @throws RuntimeException si plus d'un entry point est déclaré en {@code ONE_SHOT}
     */
    public static void loadAndExecuteEntryPointServices(Consumer<Context> contextConsumer, String[] args, Class<? extends ApplicationEntryPoint>[] appEntryPoint, Context context) {
        AtomicInteger countOneShot = new AtomicInteger();
        AtomicInteger countLongRunning = new AtomicInteger();

        Arrays.stream(appEntryPoint)
                .sorted(AppExecutor::compareEntryPointsByLifecycle)
                .forEach(aep -> prepareAndExecuteEntryPointService(
                        aep,
                        contextConsumer,
                        args,
                        context,
                        countOneShot,
                        countLongRunning)
                );
    }

    /**
     * Compare deux classes d'entry point en fonction de leur cycle de vie déclaré.
     * <p>
     * Les entry points {@code LONG_RUNNING} sont considérés comme prioritaires et seront
     * traités avant les {@code ONE_SHOT}. En l'absence d'annotation, le cycle de vie
     * par défaut est {@code ONE_SHOT}.
     * </p>
     *
     * @param entryPoint1 première classe d'entry point à comparer
     * @param entryPoint2 seconde classe d'entry point à comparer
     * @return un entier négatif, zéro ou positif selon que le premier entry point est
     *         respectivement prioritaire, équivalent ou moins prioritaire que le second
     */
    public static int compareEntryPointsByLifecycle(Class<? extends ApplicationEntryPoint> entryPoint1, Class<? extends ApplicationEntryPoint> entryPoint2) {
        EntryPointService aAnnotation = entryPoint1.getAnnotation(EntryPointService.class);
        EntryPointService bAnnotation = entryPoint2.getAnnotation(EntryPointService.class);

        LifecycleEntryPoint aLifecycle = getLifecycleEntryPoint(aAnnotation);
        LifecycleEntryPoint bLifecycle = getLifecycleEntryPoint(bAnnotation);

        return aLifecycle == bLifecycle ? 0 : (aLifecycle == ONE_SHOT ? 1 : -1);
    }

    /**
     * Exécute la logique principale d'un entry point et gère les exceptions éventuelles.
     *
     * @param mainService instance de l'entry point à exécuter
     * @param args        arguments applicatifs transmis au point d'entrée
     */
    @SuppressWarnings("all")
    static void getTask(ApplicationEntryPoint mainService, String[] args) {
        try {
            mainService.main(args);
        } catch (MicroBeanException e) {
            error("MicroBean initialization failed", e);
            throw e;
        } catch (Exception e) {
            error("MicroBean initialization failed", e);
            throw new MicroBeanException(e);
        }
    }

    /**
     * Prépare puis exécute un entry point unique.
     * <p>
     * La méthode résout d'abord le bean correspondant, exécute le consommateur de contexte si
     * nécessaire, détermine le cycle de vie, met à jour les compteurs internes, vérifie la
     * contrainte d'unicité sur {@code ONE_SHOT}, puis déclenche l'exécution effective.
     * </p>
     *
     * @param aep              classe de l'entry point à traiter
     * @param contextConsumer  consommateur optionnel du contexte
     * @param args             arguments applicatifs
     * @param context          contexte d'injection
     * @param countOneShot     compteur des entry points {@code ONE_SHOT}
     * @param countLongRunning compteur des entry points {@code LONG_RUNNING}
     * @throws RuntimeException si plus d'un entry point est déclaré en {@code ONE_SHOT}
     */
    private static void prepareAndExecuteEntryPointService(Class<? extends ApplicationEntryPoint> aep, Consumer<Context> contextConsumer, String[] args, Context context, AtomicInteger countOneShot, AtomicInteger countLongRunning) {

        ApplicationEntryPoint mainService = context.getBean(aep);

        if (contextConsumer != null)
            contextConsumer.accept(context);

        EntryPointService entryPointAnnotation = aep.getAnnotation(EntryPointService.class);

        LifecycleEntryPoint lifecycle = getLifecycleEntryPoint(entryPointAnnotation);

        updateAtomicCounters(lifecycle, countOneShot, countLongRunning);

        if (oneShotCounterIsTooHigh(countOneShot))
            throw onlyOneApplicationEntryPointCanBeOneShot();

        executeEntryPointService(args, lifecycle, mainService);
    }

    /**
     * Exécute effectivement l'entry point selon son cycle de vie.
     * <p>
     * En mode {@code ONE_SHOT}, l'exécution est synchrone sur le thread courant.
     * En mode {@code LONG_RUNNING}, un thread dédié non daemon est créé avec la priorité maximale.
     * </p>
     *
     * @param args        arguments applicatifs transmis au point d'entrée
     * @param lifecycle   cycle de vie à appliquer
     * @param mainService instance de l'entry point à exécuter
     */
    private static void executeEntryPointService(String[] args, LifecycleEntryPoint lifecycle, ApplicationEntryPoint mainService) {

        Runnable task = () -> getTask(mainService, args);

        if (lifecycle == ONE_SHOT) {
            task.run();
        } else {
            Thread thread = new Thread(task);
            thread.setName(mainService.getClass().getSimpleName());
            thread.setDaemon(false);
            thread.setPriority(MAX_PRIORITY);
            thread.start();
        }
    }

    /**
     * Détermine le cycle de vie d'un entry point.
     *
     * @param entryPointAnnotation annotation portée par la classe d'entry point, peut être {@code null}
     * @return le cycle de vie déclaré, ou {@link LifecycleEntryPoint#ONE_SHOT} par défaut
     */
    private static LifecycleEntryPoint getLifecycleEntryPoint(EntryPointService entryPointAnnotation) {
        return entryPointAnnotation == null ? ONE_SHOT : entryPointAnnotation.lifecycle();
    }

    /**
     * Met à jour les compteurs de cycles de vie rencontrés.
     *
     * @param lifecycle        cycle de vie de l'entry point traité
     * @param countOneShot     compteur des entry points {@code ONE_SHOT}
     * @param countLongRunning compteur des entry points {@code LONG_RUNNING}
     */
    private static void updateAtomicCounters(LifecycleEntryPoint lifecycle, AtomicInteger countOneShot, AtomicInteger countLongRunning) {
        if (lifecycle == ONE_SHOT)
            countOneShot.incrementAndGet();
        else
            countLongRunning.incrementAndGet();
    }

    /**
     * Indique si le nombre d'entry points {@code ONE_SHOT} dépasse la limite autorisée.
     *
     * @param countOneShot compteur des entry points {@code ONE_SHOT}
     * @return {@code true} si plus d'un entry point {@code ONE_SHOT} a été déclaré, sinon {@code false}
     */
    private static boolean oneShotCounterIsTooHigh(AtomicInteger countOneShot) {
        return countOneShot.get() > 1;
    }
}
