package it.vitalegi.cosucce.security.service;

import it.vitalegi.cosucce.configuration.RbacProperties;
import it.vitalegi.cosucce.security.entity.UserDataEntity;
import it.vitalegi.cosucce.security.model.Permission;
import it.vitalegi.cosucce.security.model.UserIdentity;
import it.vitalegi.cosucce.security.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataService {

    final RbacProperties rbacProperties;
    final UserDataRepository userDataRepository;

    @Transactional
    public UserIdentity getUserIdentity(String issuer, String subject, List<String> roles) {
        var entity = userDataRepository.findByIssuerSubject(issuer, subject);
        if (entity == null) {
            var e = new UserDataEntity();
            e.setIssuer(issuer);
            e.setSubject(subject);
            e.setCreationDate(Instant.now());
            e.setLastUpdate(Instant.now());
            entity = userDataRepository.save(e);
        }
        return UserIdentity.builder().userId(entity.getUserId()).issuer(issuer).subject(subject).permissions(getPermissions(roles).toList()).build();
    }

    public Stream<Permission> getPermissions(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Stream.empty();
        }
        return roles.stream().flatMap(this::getPermissions).distinct();
    }

    protected Stream<Permission> getPermissions(String role) {
        var permissions = rbacProperties.getRbac().get(role);
        if (permissions == null) {
            return Stream.empty();
        }
        return permissions.stream();
    }
}
