package it.vitalegi.cosucce.security.model;

import lombok.Data;

@Data
public class OidcTokenRequest {
    String code;
    String redirectUrl;
}
