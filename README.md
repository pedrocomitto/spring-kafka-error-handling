# Spring Kafka error handling

This is an approach of Spring Kafka error handling using blocking retries:

## Why blocking retries?

When you have a pretty straight forward actions like consumes and stores in a database; consumes, make a HTTP request and stores in a database; If discarding your message, or sending to another topic/queue to be processed later are't possible options and the only option is to really process that message, maybe a blocking retry could be a viable solution.

For instance, if you have only to consume and store data in a database, besides the broker (and it would be a completely different case that is not going to be covered on this project), your only single failure point is definitely the database. So, with your database down, if you keep polling messages from broker, you won't really have anything to do with them. The main point is: If the exception that prevented my first failed message of being consumed is an exception that is going to prevent the next ones too, why would I implement a non-blocking retry? With a non-blocking retry, the next messages will fail too, so there is no reason to poll more messages to end up failing in consuming them too.

![image](https://user-images.githubusercontent.com/13872621/120021417-6b04c480-bfc1-11eb-92b7-99bc5f5ac9d9.png)

## Lessons learned

- Set ```listenerContainer.containerProperties.ackMode = ContainerProperties.AckMode.RECORD``` to commit each record individually
- When using a ExponentialBackoOff, you must set the maxInterval less than the ```max.poll.interval.ms``` to avoid rebalancing.
- The answer from Gary Russel below:

Using stateful retry was specifically designed to be used with a STCEH to avoid a rebalance, before the STCEH supported back offs.
However, now that back off is supported in the STCEH, it is better to use that over a retry template.

Now that the SeekToCurrentErrorHandler can be configured with a BackOff and has the ability to retry only certain exceptions (since version 2.3), the use of stateful retry, via the listener adapter retry configuration, is no longer necessary. You can provide the same functionality with appropriate configuration of the error handler and remove all retry configuration from the listener adapter. See Seek To Current Container Error Handlers for more information.

The configuration is much simpler.
You don't need to use manual acks; the container will commit the offsets based on the AckMode BATCH (default) or RECORD. The latter is more costly but provides less chance of redelivery.

For infinite retries, use a FixedBackOff with UNLIMITED_ATTEMPTS (Long.MAX_VALUE) in the maxAttempts property.

The ExponentialBackOff will retry infinitely by default. You just need to be sure that the maxInterval is less than the max.poll.interval.ms to avoid a rebalance.

### Some considerations about Gary's answer

Since version ```2.6.0``` there is no longer necessary to keep the total aggregate retry time less than ```max.poll.interval.ms```. In fact, the interval between retries **must be less than** ```max.poll.interval.ms``` to avoid rebalancing.

