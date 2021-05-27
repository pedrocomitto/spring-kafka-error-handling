# Spring Kafka error handling

This is an approach of Spring Kafka error handling using blocking retries:

## Why blocking retries?

When you have a pretty straight forward actions like consumes and stores in a database; consumes, make a HTTP request and stores in a database; If discarding your message, or sending to another topic/queue to be processed later are't possible options and the only option is to really process that message, maybe a blocking retry could be a viable solution.

For instance, if you have only to consume and store data in a database, besides the broker (and this it would be a completely different case that is not going to be covered on this project), your only single failure point is definely the database. So, with your database down, if you keep polling messages from broker, you won't really have anything to do with them. The main point is: If the exception that prevented my first failed message of being consumed is an exception that is going to prevent the next ones too, why would I implement a non-blocking retry? With a non-blocking retry, the next messages will fail too, so there is no reason to poll more messages to end up failing in consuming them too.

## Lessons learned

- Set ```listenerContainer.containerProperties.ackMode = ContainerProperties.AckMode.RECORD``` to commit each record individually


![image](https://user-images.githubusercontent.com/13872621/119908508-1f9ed780-bf29-11eb-9501-61c8428ef321.png)
