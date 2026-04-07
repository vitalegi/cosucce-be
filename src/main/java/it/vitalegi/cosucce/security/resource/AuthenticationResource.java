package it.vitalegi.cosucce.security.resource;

import it.vitalegi.cosucce.security.model.UserIdentity;
import it.vitalegi.cosucce.security.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticationResource {

    AuthenticationService authenticationService;

    @GetMapping("/identity")
    public UserIdentity identity() {
        return authenticationService.identity();
    }
}
