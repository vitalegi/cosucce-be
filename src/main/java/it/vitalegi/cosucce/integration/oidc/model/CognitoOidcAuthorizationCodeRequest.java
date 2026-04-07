package it.vitalegi.cosucce.integration.oidc.model;

import lombok.Data;

@Data
public class CognitoOidcAuthorizationCodeRequest {
    String code;
    String redirectUrl;
}
