package com.vmware.runnable;

import java.util.concurrent.ThreadLocalRandom;

import com.vmware.producer.Producer;

public class ProducerRunnable implements Runnable {
    private final Producer producer;

    public ProducerRunnable(final Producer producer) {
        this.producer = producer;
    }

    /**
     * The run method implementation for a thread that continuously produces items until interrupted.
     * <p>
     * The method runs in a loop as long as the current thread is not interrupted. Within each loop iteration,
     * it pauses for a random duration between 0 and 100 milliseconds using Thread.sleep() and then invokes
     * the 'produce()' method of the producer object.
     * <p>
     * If the thread is interrupted while sleeping, it sets the interrupted status again to maintain the interrupted state.
     * This ensures that the loop condition is checked properly in the next iteration.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(101));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            producer.produce();
        }
    }
}
