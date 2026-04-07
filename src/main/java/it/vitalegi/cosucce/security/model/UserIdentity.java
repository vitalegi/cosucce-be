package it.vitalegi.cosucce.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdentity {
    private UUID userId;
    private String issuer;
    private String subject;
    private List<Permission> permissions;
}
