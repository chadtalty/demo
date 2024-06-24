package com.chadtalty.demo.config;

import com.chadtalty.demo.exception.UnexpectedErrorException;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Configuration class for enabling JPA auditing.
 * <p>
 * This class implements the {@link AuditorAware} interface to provide the
 * current auditor (user) for auditing purposes. It retrieves the current
 * auditor's email from the security context.
 * </p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "jpaAuditConfig")
public class JpaAuditConfig implements AuditorAware<String> {

    /**
     * Returns the current auditor's email.
     * <p>
     * This method retrieves the current authentication token from the security
     * context and extracts the email claim if the authentication token is an
     * instance of {@link JwtAuthenticationToken}.
     * </p>
     *
     * @return an {@link Optional} containing the current auditor's email if
     *         available, otherwise an empty {@link Optional}.
     * @throws UnexpectedErrorException if the authentication token is not an
     *                                  instance of {@link JwtAuthenticationToken}.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken) {
                        return ((JwtAuthenticationToken) authentication)
                                .getToken()
                                .getClaim("email");
                    }
                    throw new UnexpectedErrorException(String.format(
                            "Unexpected security context authentication type: %s",
                            authentication.getClass().getName()));
                });
    }
}
