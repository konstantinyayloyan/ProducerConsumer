package com.vmware.random;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConcurrentRandomStringGenerator implements RandomStringGenerator {
    private final int numberOfThreads;

    public ConcurrentRandomStringGenerator(final int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * Generates a random string of a given size using multiple threads.
     *
     * @param totalSize The desired total size of the generated random string.
     * @return The generated random string.
     * The method creates an ExecutorService with a fixed thread pool of size 'numberOfThreads'.
     * It initializes an empty StringBuilder 'sb' to store the generated random string.
     * 'addTasks' method is called to add tasks to the 'tasks' list, where each task generates a batch of random characters.
     * 'mergeResultsOfAllTasks' method is called to execute the tasks using the executorService, retrieve the results,
     * and append them to the 'sb' StringBuilder.
     * <p>
     * Finally, the method returns the generated random string by converting the StringBuilder to a String.
     */
    @Override
    public String generateRandomStringOfGivenSize(int totalSize) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<String>> tasks = new ArrayList<>();

        addTasks(totalSize, tasks);

        final StringBuilder sb = new StringBuilder(totalSize);
        mergeResultsOfAllTasks(executorService, tasks, sb);

        return sb.toString();
    }

    /**
     * Adds tasks to the given list of Callable<String> to generate random strings
     * based on the total size and the number of threads.
     *
     * @param totalSize The desired total size of the generated random strings.
     * @param tasks     The list of Callable<String> to which the tasks will be added.
     *                  The method calculates the batchSize for each thread based on the totalSize and
     *                  the number of threads. It also calculates the remainingSize, which represents
     *                  the additional characters that need to be distributed among the threads when
     *                  the totalSize is not evenly divisible by the number of threads.
     *                  It iterates over the number of threads and adds a task for each thread to the
     *                  tasks list. The batchSizeForThread is calculated by adding 1 to batchSize for
     *                  the threads with indices less than the remainingSize, to ensure the remaining
     *                  characters are evenly distributed among the threads.
     */
    private void addTasks(int totalSize, List<Callable<String>> tasks) {
        int batchSize = totalSize / numberOfThreads;
        int remainingSize = totalSize % numberOfThreads;

        for (int i = 0; i < numberOfThreads; ++i) {
            int batchSizeForThread = batchSize + (i < remainingSize ? 1 : 0);
            tasks.add(() -> generateRandomStringBatch(batchSizeForThread));
        }
    }

    private static void mergeResultsOfAllTasks(ExecutorService executorService, List<Callable<String>> tasks, StringBuilder sb) {
        try {
            final List<Future<String>> futures = executorService.invokeAll(tasks);
            for (Future<String> future : futures) {
                sb.append(future.get(5, TimeUnit.SECONDS));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println("Execution exception occurred");
        } catch (TimeoutException e) {
            System.out.println("Timeout exception occurred, please check generateRandomStringBatch() method because it took >5 seconds");
        } finally {
            executorService.shutdown();
        }
    }

    private static String generateRandomStringBatch(final int size) {
        final StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; ++i) {
            char randomChar = (char) (ThreadLocalRandom.current().nextInt(26) + 'A');
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
