package com.chadtalty.demo.config;

import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Implement logic to return the current user
        return Optional.of("system"); // Replace "system" with the actual user identifier
    }
}
