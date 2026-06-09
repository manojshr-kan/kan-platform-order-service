package com.kansoft.orderservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Plain unit test — no Spring context, so CI does not require AWS credentials
 * or a live Cognito issuer to pass.
 */
class HealthControllerTest {

    @Test
    void healthReportsUp() {
        var body = new HealthController().health();
        assertEquals("UP", body.get("status"));
        assertEquals("order-service", body.get("service"));
    }
}
