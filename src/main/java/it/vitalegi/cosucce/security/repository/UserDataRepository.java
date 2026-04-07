package it.vitalegi.cosucce.security.repository;

import it.vitalegi.cosucce.security.entity.UserDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDataRepository extends JpaRepository<UserDataEntity, UUID> {

    @Query("SELECT ud FROM UserData ud WHERE ud.issuer = :issuer AND ud.subject = :subject")
    UserDataEntity findByIssuerSubject(@Param("issuer") String issuer, @Param("subject") String subject);

}
