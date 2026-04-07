package it.vitalegi.cosucce.integration.oidc;

import it.vitalegi.cosucce.configuration.OidcProperties;
import it.vitalegi.cosucce.integration.oidc.model.CognitoOidcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class CognitoService {

    RestClient restClient;
    String authorizationUrl;
    String clientId;

    public CognitoService(OidcProperties oidcProperties) {
        this.authorizationUrl = oidcProperties.getAuthorizationUrl();
        this.clientId = oidcProperties.getClientId();

        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectionRequestTimeout(5000);

        restClient = RestClient.builder() //
                .requestFactory(factory) //
                .baseUrl(authorizationUrl) //
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) //
                .build();
    }

    public CognitoOidcResponse token(String code, String redirectUrl) {
        var formData = new LinkedMultiValueMap<String, String>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUrl);
        formData.add("code", code);

        return restClient.post() //
                .uri(builder -> builder.pathSegment("oauth2", "token").build()) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) //
                .body(formData) //
                .retrieve() //
                .body(CognitoOidcResponse.class);
    }

    public CognitoOidcResponse refresh(String refreshToken) {
        var formData = new LinkedMultiValueMap<String, String>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("refresh_token", refreshToken);

        return restClient.post() //
                .uri(builder -> builder.pathSegment("oauth2", "token").build()) //
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) //
                .body(formData) //
                .retrieve() //
                .body(CognitoOidcResponse.class);
    }

}
