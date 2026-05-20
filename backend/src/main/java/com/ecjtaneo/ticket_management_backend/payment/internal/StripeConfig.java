package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.stripe.StripeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    //TODO: Load API key from configuration file
    @Bean
    public StripeClient stripeClient() {
        return new StripeClient("API_KEY");
    }

}
