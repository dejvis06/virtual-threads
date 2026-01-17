package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MillionVirtualThreadsTest {

    // Regex pattern to extract the ForkJoinPool name from the thread description (e.g., ForkJoinPool-1)
    private static final Pattern THREAD_POOL_PATTERN = Pattern.compile("ForkJoinPool-\\d+");
    // // Regex pattern to extract the worker thread name (carrier thread) from the thread description(e.g., worker-1)
    private static final Pattern WORKER_PATTERN = Pattern.compile("worker-\\d+");

    /**
     * Virtual threads are very lightweight threads managed by the JVM.
     * Many virtual threads share a small number of OS threads.
     * This avoids creating many OS threads and keeps scheduling fast,
     * even when running millions of virtual threads.
     */
    @Test
    void millionVirtualThreads() throws Exception {
        // Thread-safe sets for unique pool and worker names
        Set<String> forkJoinPoolsUsed = ConcurrentHashMap.newKeySet();
        Set<String> platformWorkerThreadsUsed = ConcurrentHashMap.newKeySet();

        // Number of virtual threads to create
        final int NUMBER_OF_THREADS = 1_000_000;

        // Create 1 million virtual threads but don't start them yet
        var virtualThreads = IntStream.range(0, NUMBER_OF_THREADS)
                .mapToObj(i -> Thread.ofVirtual()
                        .unstarted(() -> {
                            // Virtual threads run on a single JVM-managed ForkJoinPool.
                            // This pool provides the carrier (OS) threads that execute virtual threads.
                            // All virtual threads are multiplexed onto this shared pool.
                            forkJoinPoolsUsed.add(getThreadPoolName());
                            // Get and store worker name
                            platformWorkerThreadsUsed.add(getWorkerName());
                        })
                )
                .toList();

        // Start timing
        Instant startTime = Instant.now();

        // Start all threads
        for (var thread : virtualThreads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (var thread : virtualThreads) {
            thread.join();
        }

        // Calculate time taken
        Instant endTime = Instant.now();
        long durationMs = Duration.between(startTime, endTime).toMillis();

        // Print results
        System.out.println("Virtual threads: " + NUMBER_OF_THREADS);

        int cpuCores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + cpuCores);

        System.out.println("Time taken: " + durationMs + "ms");
        System.out.println("### Thread Pools");
        forkJoinPoolsUsed.forEach(System.out::println);
        System.out.println("### Workers (platform threads) count : " + platformWorkerThreadsUsed.size());
        platformWorkerThreadsUsed.forEach(System.out::println);

        assertTrue(
                platformWorkerThreadsUsed.size() <= cpuCores,
                "Platform worker threads exceeded CPU cores"
        );
    }

    /**
     * Platform threads map 1:1 to native OS threads.
     * The operating system is responsible for creating, scheduling,
     * and context-switching these threads, which introduces significant
     * overhead at large thread counts.
     */
    @Test
    void millionPlatformThreads() throws Exception {
        // Thread-safe sets for unique pool and worker names
        Set<String> forkJoinPoolsUsed = ConcurrentHashMap.newKeySet();
        Set<String> platformWorkerThreadsUsed = ConcurrentHashMap.newKeySet();

        // Number of platform threads to create
        final int NUMBER_OF_THREADS = 1_000_000;

        // Create 1 million platform threads but don't start them yet
        var platformThreads = IntStream.range(0, NUMBER_OF_THREADS)
                .mapToObj(i -> Thread.ofPlatform()
                        .unstarted(() -> {
                            // Platform threads do not use a ForkJoin pool or carrier threads.
                            // Each platform thread maps directly to an OS thread.
                            // Thread pools are optional and only exist when using executors such as newFixedThreadPool
                            forkJoinPoolsUsed.add("N/A (platform thread)");
                            platformWorkerThreadsUsed.add(Thread.currentThread().getName());
                        })
                )
                .toList();

        // Start timing
        Instant startTime = Instant.now();

        // Start all threads
        for (var thread : platformThreads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (var thread : platformThreads) {
            thread.join();
        }

        // Calculate time taken
        Instant endTime = Instant.now();
        long durationMs = Duration.between(startTime, endTime).toMillis();

        // Print results
        System.out.println("Platform threads: " + NUMBER_OF_THREADS);

        int cpuCores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + cpuCores);

        System.out.println("Time taken: " + durationMs + "ms");
        System.out.println("### Thread Pools");
        forkJoinPoolsUsed.forEach(System.out::println);
        System.out.println("### Platform worker threads count : " + platformWorkerThreadsUsed.size());

        assertTrue(
                platformWorkerThreadsUsed.size() > cpuCores,
                "Expected platform threads to exceed CPU cores"
        );
    }

    // Get pool name from thread info (e.g., ForkJoinPool-1)
    private static String getThreadPoolName() {
        String threadInfo = Thread.currentThread().toString();
        Matcher poolMatcher = THREAD_POOL_PATTERN.matcher(threadInfo);
        return poolMatcher.find() ? poolMatcher.group() : "pool not found";
    }

    // Get worker name from thread info (e.g., worker-2)
    private static String getWorkerName() {
        String threadInfo = Thread.currentThread().toString();
        Matcher workerMatcher = WORKER_PATTERN.matcher(threadInfo);
        return workerMatcher.find() ? workerMatcher.group() : "worker not found";
    }
}
