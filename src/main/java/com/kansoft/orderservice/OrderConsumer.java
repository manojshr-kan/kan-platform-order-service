package com.kansoft.orderservice;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes messages from the service's SQS queue — the same app produces and
 * processes, demonstrating the end-to-end queue loop.
 */
@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @SqsListener("${app.queue-name:order-service-queue}")
    public void handle(Map<String, Object> order) {
        log.info("Processed order from queue: {}", order);
    }
}
