package it.vitalegi.cosucce.security.service;

import it.vitalegi.cosucce.security.exception.UnauthorizedAccessException;
import it.vitalegi.cosucce.security.model.Permission;
import it.vitalegi.cosucce.security.model.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    final UserDataService userDataService;

    public UserIdentity identity() {
        var principal = getPrincipal();
        return userDataService.getUserIdentity(getIssuer(principal), getSubject(principal), getGroups(principal));
    }

    public void checkPermission(Permission permission) {
        if (!hasPermission(permission)) {
            var principal = getPrincipal();
            throw new UnauthorizedAccessException(userDataService.getPermissions(getGroups(principal)).toList(), permission);
        }
    }

    public boolean hasPermission(Permission permission) {
        var principal = getPrincipal();
        return userDataService.getPermissions(getGroups(principal)).anyMatch(p -> p == permission);
    }

    protected String getIssuer(Jwt jwt) {
        return jwt.getIssuer().toString();
    }

    protected String getSubject(Jwt jwt) {
        return jwt.getSubject();
    }

    protected List<String> getGroups(Jwt jwt) {
        if (jwt == null) {
            return Collections.emptyList();
        }
        return jwt.getClaimAsStringList("cognito:groups");
    }

    protected Jwt getPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }
        return null;
    }


}
