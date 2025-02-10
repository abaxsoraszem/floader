package abax;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

// import com.google.cloud.spring.pubsub.core.PubSubTemplate;
// import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;

// @Configuration
public class GcpPublisher {
    Logger logger = LoggerFactory.getLogger(GcpPublisher.class);

    AtomicInteger counter = new AtomicInteger(0);

    // @Bean
    // @ServiceActivator(inputChannel = "jsonTransactionChannel")
    // public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
    //     PubSubMessageHandler adapter = new PubSubMessageHandler(pubsubTemplate, "transactions");

    //     adapter.setSuccessCallback
    //             ((ackId, message) -> {
    //                 int x = counter.incrementAndGet();
    //                 if (x % 100 == 0) {
    //                     logger.info("Message sent {}", message.getPayload());
    //                 }
    //                 logger.debug("Message sent {}", message.getPayload());
    //             });

    //     adapter.setFailureCallback(
    //             (cause, message) -> logger.info("Error sending " + message + " due to " + cause));

    //     return adapter;
    // }
}
