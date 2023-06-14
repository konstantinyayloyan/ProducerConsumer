package com.vmware.consumer;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.vmware.utils.FileWriterUtils;

public class LargeStringConsumer implements Consumer {
    private static final String OUTPUT_FILE = "output.txt";

    private final Lock lock;
    private final Condition sizeLessThan80;
    private final Condition sizeIs0;
    private final Queue<String> queue;

    public LargeStringConsumer(final Lock lock, final Condition sizeLessThan80, final Condition sizeIs0, final Queue<String> queue) {
        this.lock = lock;
        this.sizeLessThan80 = sizeLessThan80;
        this.sizeIs0 = sizeIs0;
        this.queue = queue;
    }

    /**
     * Consumes an item from the queue and processes it.
     * <p>
     * The method first acquires the lock using the 'lock' object to ensure thread safety.
     * <p>
     * It enters a while loop that checks if the queue is empty.
     * If the condition is true, it waits for the 'sizeIs0' condition variable to be signaled,
     * indicating that there is an item available in the queue.
     * <p>
     * Once the while loop exits, it retrieves an item from the queue using the 'poll()' method,
     * which removes and returns the head of the queue.
     * <p>
     * If the retrieved content is not null, it processes the content by invoking the 'processContent()' method
     * and writes the processed content to an output file using the 'writeToFile()' method of the 'FileWriterUtils' class.
     * <p>
     * If the size of the queue becomes less than 80, it signals all waiting threads that are blocked on the 'sizeLessThan80' condition variable.
     * <p>
     * Finally, the lock is released using the 'unlock()' method within a 'finally' block to ensure proper cleanup.
     */
    @Override
    public void consume() {
        try {
            lock.lock();
            waitForItemInQueue();
            processContentAndWriteToFile();
            signalSizeLessThan80();
        } finally {
            lock.unlock();
        }
    }

    private void waitForItemInQueue() {
        while (queue.isEmpty()) {
            try {
                sizeIs0.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processContentAndWriteToFile() {
        final String content = queue.poll();
        if (content != null) {
            String processedContent = processContent(content);
            FileWriterUtils.writeToFile(OUTPUT_FILE, processedContent);
        }
    }

    private String processContent(String content) {
        // Process the content as required
        String firstFourChars = content.substring(0, 4);
        int contentLength = content.length();
        return "<" + firstFourChars + ", " + contentLength + ">";
    }

    private void signalSizeLessThan80() {
        if (queue.size() < 80) {
            sizeLessThan80.signalAll();
        }
    }
}
