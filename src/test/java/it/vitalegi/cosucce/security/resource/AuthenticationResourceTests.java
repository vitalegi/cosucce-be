package it.vitalegi.cosucce.security.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.security.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class AuthenticationResourceTests {
    @Autowired
    MockMvc mockMvc;

    @Nested
    class Identity {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var auth = MockAuth.admin("user1");
            mockMvc.perform(get("/auth/identity").contentType(MediaType.APPLICATION_JSON).with(csrf()).with(auth)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.userId").isString()) //
                    .andExpect(jsonPath("$.issuer").value(MockAuth.ISSUER)) //
                    .andExpect(jsonPath("$.subject").value("user1")) //
                    .andExpect(jsonPath("$.permissions", hasItem(Permission.BUDGET_VIEW.name())));
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(get("/auth/identity").contentType(MediaType.APPLICATION_JSON)) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }
    }
}
