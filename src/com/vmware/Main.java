package com.vmware;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vmware.consumer.LargeStringConsumer;
import com.vmware.producer.LargeStringProducer;
import com.vmware.random.ConcurrentRandomStringGenerator;
import com.vmware.random.RandomStringGenerator;
import com.vmware.runnable.ConsumerRunnable;
import com.vmware.runnable.MonitoringRunnable;
import com.vmware.runnable.ProducerRunnable;

public class Main {

    private static final int NUMBER_OF_THREADS_FOR_STRING_GENERATION = 8;

    public static void main(String[] args) {
        final int numProducers = readUserInput("Enter the number of producers (1-10): ", 1, 10);
        final int numConsumers = readUserInput("Enter the number of consumers (1-10): ", 1, 10);

        final Queue<String> queue = new LinkedList<>();

        final Lock lock = new ReentrantLock();
        final Condition sizeLessThan80 = lock.newCondition();
        final Condition sizeIs0 = lock.newCondition();

        final ExecutorService producerExecutor = Executors.newFixedThreadPool(numProducers);
        final ExecutorService consumerExecutor = Executors.newFixedThreadPool(numConsumers);
        final ExecutorService monitoringExecutor = Executors.newSingleThreadExecutor();

        final RandomStringGenerator randomGenerator = new ConcurrentRandomStringGenerator(NUMBER_OF_THREADS_FOR_STRING_GENERATION);

        startProducers(numProducers, queue, lock, sizeLessThan80, sizeIs0, producerExecutor, randomGenerator);
        startConsumers(numConsumers, queue, lock, sizeLessThan80, sizeIs0, consumerExecutor);
        startMonitoring(monitoringExecutor, queue);
    }

    private static void startMonitoring(ExecutorService monitoringExecutor, Queue<String> queue) {
        monitoringExecutor.submit(new MonitoringRunnable(queue));
    }

    private static void startConsumers(int numConsumers, Queue<String> queue, Lock lock, Condition sizeLessThan80, Condition sizeIs0, ExecutorService consumerExecutor) {
        for (int i = 0; i < numConsumers; ++i) {
            consumerExecutor.submit(new ConsumerRunnable(new LargeStringConsumer(lock, sizeLessThan80, sizeIs0, queue)));
        }
    }

    private static void startProducers(int numProducers, Queue<String> queue, Lock lock, Condition sizeLessThan80, Condition sizeIs0, ExecutorService producerExecutor, RandomStringGenerator randomGenerator) {
        for (int i = 0; i < numProducers; ++i) {
            producerExecutor.submit(new ProducerRunnable(new LargeStringProducer(lock, sizeLessThan80, sizeIs0, queue, randomGenerator)));
        }
    }

    private static int readUserInput(String message, int minValue, int maxValue) {
        int value = 0;
        boolean validInput = false;
        Scanner scanner = new Scanner(System.in);
        while (!validInput) {
            try {
                System.out.print(message);
                value = scanner.nextInt();
                if (value >= minValue && value <= maxValue) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a value between " + minValue + " and " + maxValue + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer value.");
            }
        }
        return value;
    }
}
