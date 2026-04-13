package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAccountAddOrUpdateRequest;
import it.vitalegi.cosucce.security.exception.UnauthorizedBoardAccessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardAccountResourceTests extends AbstractBudgetResourceTests {

    @Nested
    class Add {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request().with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request() {
            return post("/budget/board/" + boardId + "/account") //
                    .content(asString(BudgetBoardAccountAddOrUpdateRequest.builder().accountId(UUID.randomUUID()).label("aaa").icon("bbb").enabled(true).etag("ccc").build())) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Update {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", "red", true, "aaa");
            mockMvc.perform(request(accountId).with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
            var actual = budgetBoardAccountService.getBoardAccounts(boardId).get(0);
            assertEquals(accountId, actual.getAccountId());
            assertEquals("xxx", actual.getLabel());
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request().with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID());
        }

        MockHttpServletRequestBuilder request(UUID accountId) {
            return put("/budget/board/" + boardId + "/account") //
                    .content(asString(BudgetBoardAccountAddOrUpdateRequest.builder().accountId(accountId).label("xxx").icon("xxx").enabled(true).etag("bbb").build())) //
                    .header("x-etag", "aaa") //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class GetById {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", "red", true, "ccc");
            mockMvc.perform(request(boardId).with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[0].icon").value("bbb")) //
                    .andExpect(jsonPath("$[0].color").value("red")) //
                    .andExpect(jsonPath("$[0].creationDate").exists()) //
                    .andExpect(jsonPath("$[0].lastUpdate").exists());
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request(boardId)) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request(boardId).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        @Test
        void when_unknownBoard_then_403() throws Exception {
            mockMvc.perform(request(UUID.randomUUID()).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request(UUID boardId) {
            return get("/budget/board/" + boardId + "/account") //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Delete {
        @Test
        void when_authenticated_then_ok() throws Exception {
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", "red", true, "ccc");
            mockMvc.perform(request(boardId, accountId).with(boardOwner)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
            assertTrue(budgetBoardAccountService.getBoardAccounts(boardId).isEmpty());
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request(boardId, UUID.randomUUID())) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request(boardId, UUID.randomUUID()).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        @Test
        void when_memberOfTheBoardWithoutPermission_then_403() throws Exception {
            mockMvc.perform(request(boardId, UUID.randomUUID()).with(boardMember)) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request(UUID boardId, UUID accountId) {
            return delete("/budget/board/" + boardId + "/account/" + accountId) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

}
