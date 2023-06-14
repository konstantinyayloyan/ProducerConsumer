package com.vmware.runnable;

import java.util.Queue;

public class MonitoringRunnable implements Runnable {

    private final Queue<String> queue;

    public MonitoringRunnable(Queue<String> queue) {
        this.queue = queue;
    }

    /**
     * The run method implementation for a thread that continuously monitors the size of a queue until interrupted.
     * <p>
     * The method runs in a loop as long as the current thread is not interrupted. Within each loop iteration,
     * it pauses for 100 milliseconds using Thread.sleep() and then prints the current size of the queue.
     * <p>
     * If the thread is interrupted while sleeping, it sets the interrupted status again to maintain the interrupted state.
     * This ensures that the loop condition is checked properly in the next iteration.
     * The purpose of this method is to monitor the size of the queue and provide periodic updates through console logging.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Queue size is " + queue.size());
        }
    }
}
