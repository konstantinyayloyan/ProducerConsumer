package com.vmware.runnable;

import java.util.concurrent.ThreadLocalRandom;

import com.vmware.consumer.Consumer;

public class ConsumerRunnable implements Runnable {
    private final Consumer consumer;

    public ConsumerRunnable(Consumer consumer) {
        this.consumer = consumer;
    }

    /**
     * The run method implementation for a thread that continuously consumes items until interrupted.
     * <p>
     * The method runs in a loop as long as the current thread is not interrupted. Within each loop iteration,
     * it pauses for a random duration between 0 and 100 milliseconds using Thread.sleep() and then invokes
     * the 'consume()' method of the consumer object.
     * <p>
     * If the thread is interrupted while sleeping, it sets the interrupted status again to maintain the interrupted state.
     * This ensures that the loop condition is checked properly in the next iteration.
     * The purpose of this method is to continuously consume items from a source, providing periodic execution with random delays.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(101));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            consumer.consume();
        }
    }
}
