package br.ufma.lsdi.SmartMeterIssuerSSI.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String getUsername() {
        return Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();
    }
}
