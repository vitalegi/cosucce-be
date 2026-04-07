package it.vitalegi.cosucce.security.resource;


import it.vitalegi.cosucce.configuration.OidcProperties;
import it.vitalegi.cosucce.security.exception.MissingCookieException;
import it.vitalegi.cosucce.security.model.OidcTokenRequest;
import it.vitalegi.cosucce.security.model.OidcTokenResponse;
import it.vitalegi.cosucce.security.service.OidcService;
import it.vitalegi.cosucce.util.StringUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("oidc")
@RequiredArgsConstructor
public class OidcResource {

    final OidcService oidcService;
    final OidcProperties oidcProperties;

    @PostMapping("/token")
    public OidcTokenResponse token(@RequestBody OidcTokenRequest request, HttpServletResponse response) {
        var token = oidcService.token(request.getCode(), request.getRedirectUrl());
        var cookie = cookie("refresh_token", token.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return token;
    }

    @GetMapping("/logout")
    public void token(HttpServletResponse response) {
        var cookie = cookie("refresh_token", "", -1);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/refresh")
    public OidcTokenResponse refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        if (StringUtil.isNullOrEmpty(refreshToken)) {
            throw new MissingCookieException("Missing cookie refresh_token");
        }
        return oidcService.refresh(refreshToken);
    }

    protected ResponseCookie cookie(String name, String value) {
        return cookie(name, value, oidcProperties.getCookie().getMaxAge());
    }

    protected ResponseCookie cookie(String name, String value, Integer maxAge) {
        var cookie = ResponseCookie.from(name, value).path("/");
        if (maxAge != null) {
            cookie.maxAge(maxAge);
        }
        if (oidcProperties.getCookie().getSecure()) {
            cookie.secure(true);
        }
        if (oidcProperties.getCookie().getHttpOnly()) {
            cookie.httpOnly(true);
        }
        var domain = oidcProperties.getCookie().getDomain();
        if (domain != null) {
            cookie.domain(domain);
            cookie.sameSite("None");
        }
        return cookie.build();
    }
}
