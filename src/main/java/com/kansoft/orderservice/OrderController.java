package com.kansoft.orderservice;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Demonstrates the "developer adds infra alongside code" loop:
 * a request produces a message onto the service's own SQS queue.
 */
@RestController
public class OrderController {

    private final SqsTemplate sqsTemplate;
    private final String queueName;

    public OrderController(SqsTemplate sqsTemplate,
                           @Value("${app.queue-name:order-service-queue}") String queueName) {
        this.sqsTemplate = sqsTemplate;
        this.queueName = queueName;
    }

    @PostMapping("/orders")
    public Map<String, String> createOrder(@RequestBody Map<String, Object> order) {
        sqsTemplate.send(queueName, order);
        return Map.of("status", "accepted", "queue", queueName);
    }
}
