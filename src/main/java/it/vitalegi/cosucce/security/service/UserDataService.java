package it.vitalegi.cosucce.security.service;

import it.vitalegi.cosucce.configuration.RbacProperties;
import it.vitalegi.cosucce.security.model.Permission;
import it.vitalegi.cosucce.security.model.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static it.vitalegi.cosucce.db.Tables.USER_DATA;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataService {

    final RbacProperties rbacProperties;
    final DSLContext dsl;

    @Transactional
    public UserIdentity getUserIdentity(String issuer, String subject, List<String> roles) {
        dsl.insertInto(USER_DATA) //
                .set(USER_DATA.USER_ID, UUID.randomUUID()) //
                .set(USER_DATA.ISSUER, issuer) //
                .set(USER_DATA.SUBJECT, subject) //
                .set(USER_DATA.CREATION_DATE, LocalDateTime.now()) //
                .set(USER_DATA.LAST_UPDATE, LocalDateTime.now()) //
                .onDuplicateKeyIgnore() //
                .execute();

        var record = dsl.selectFrom(USER_DATA) //
                .where(USER_DATA.ISSUER.eq(issuer)) //
                .and(USER_DATA.SUBJECT.eq(subject)) //
                .fetchOne();
        if (record == null) {
            throw new RuntimeException("User data " + issuer + "/" + subject + " not found");
        }
        return UserIdentity.builder().userId(record.getUserId()).issuer(issuer).subject(subject).permissions(getPermissions(roles).toList()).build();
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
