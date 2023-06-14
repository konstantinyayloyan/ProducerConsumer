<h2>General Description</h2>

The main classes are `LargeStringConsumer` and `LargeStringProducer`. They share a `Queue<String>` object for the purpose of producing and consuming strings. Additionally, they share a lock object (`lock.lock()` and `lock.unlock()`) to handle locking. Two conditions, `sizeLessThan80` and `sizeIs0`, are created using this lock.

The `sizeLessThan80` condition is used in the consumer to signal the producer that the size is already less than 80, allowing the producer to continue its job. The same logic applies to the `sizeIs0` condition.

Another interesting component is the `RandomStringGenerator`. Since the strings in our case are very large (ranging from 2,000,000 to 20,000,000), I have created a `ConcurrentRandomStringGenerator` that generates strings concurrently using multiple threads to avoid performance issues.

The `ConsumerRunnable` receives a `Consumer` object and calls `consumer.consume()` in its `run()` method. The same applies to the `ProducerRunnable`.

Furthermore, there is a `MonitoringRunnable` that simply prints the current size of the queue.

<h2> How to Run </h2>

Just run the main() method of Main class, input 2 numbers from 1 to 10 (for producer and consumer count) and it will start to print the current queue size.
