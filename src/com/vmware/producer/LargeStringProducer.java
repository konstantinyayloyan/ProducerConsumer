package com.vmware.producer;

import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.vmware.random.RandomStringGenerator;

public class LargeStringProducer implements Producer {

    private static final int QUEUE_MAX_SIZE = 100;

    private final Lock lock;
    private final Condition sizeLessThan80;
    private final Condition sizeIs0;
    private final Queue<String> queue;
    private final RandomStringGenerator randomStringGenerator;

    public LargeStringProducer(final Lock lock, final Condition sizeLessThan80, final Condition sizeIs0, final Queue<String> queue, RandomStringGenerator randomStringGenerator) {
        this.lock = lock;
        this.sizeLessThan80 = sizeLessThan80;
        this.sizeIs0 = sizeIs0;
        this.queue = queue;
        this.randomStringGenerator = randomStringGenerator;
    }

    /**
     * Produces an item and adds it to the queue.
     * <p>
     * The method first acquires the lock using the 'lock' object to ensure thread safety.
     * <p>
     * It enters a while loop that checks if the size of the queue has reached the maximum size (QUEUE_MAX_SIZE).
     * If the condition is true, it waits for the 'sizeLessThan80' condition variable to be signaled,
     * indicating that the queue size has decreased below the maximum size.
     * <p>
     * Once the while loop exits, it adds a random string to the queue by invoking the 'addRandomStringToQueue()' method.
     * After adding the item, it signals all waiting threads that are blocked on the 'sizeIs0' condition variable,
     * indicating that the queue is no longer empty.
     * <p>
     * Finally, the lock is released using the 'unlock()' method within a 'finally' block to ensure proper cleanup.
     */
    @Override
    public void produce() {
        try {
            lock.lock();

            while (queue.size() >= QUEUE_MAX_SIZE) {
                try {
                    sizeLessThan80.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            addRandomStringToQueue();
            sizeIs0.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void addRandomStringToQueue() {
        int totalSize = ThreadLocalRandom.current().nextInt(18_000_001) + 2_000_000; //for generating number between 2_000_000 and 20_000_000
        queue.add(randomStringGenerator.generateRandomStringOfGivenSize(totalSize));
    }
}
