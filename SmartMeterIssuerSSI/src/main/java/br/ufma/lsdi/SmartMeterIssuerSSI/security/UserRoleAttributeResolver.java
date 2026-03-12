package br.ufma.lsdi.SmartMeterIssuerSSI.security;

import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
public class UserRoleAttributeResolver {

    private final JpaUserDetailsService userDetailsService;

    public UserRoleAttributeResolver(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String resolve(String username) throws AccessDeniedException {

        UserPrincipal user = (UserPrincipal)
                userDetailsService.loadUserByUsername(username);

        if (user.hasRole(Role.EMPLOYEE)) {
            return Role.EMPLOYEE.asAuthority();
        }

        if (user.hasRole(Role.USER)) {
            return Role.USER.asAuthority();
        }

        throw new AccessDeniedException("User does not have required role");
    }
}
