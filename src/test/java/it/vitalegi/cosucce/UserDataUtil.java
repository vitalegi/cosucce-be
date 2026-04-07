package it.vitalegi.cosucce;

import it.vitalegi.cosucce.security.service.UserDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDataUtil {
    final UserDataService userDataService;

    public UUID admin() {
        return user(List.of("ADMIN"));
    }

    public UUID user() {
        return user(List.of());
    }

    public UUID user(List<String> roles) {
        var user = userDataService.getUserIdentity(MockAuth.ISSUER, UUID.randomUUID().toString(), roles);
        return user.getUserId();
    }
}
