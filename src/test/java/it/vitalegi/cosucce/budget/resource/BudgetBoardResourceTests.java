package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.security.exception.UnauthorizedAccessException;
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

import static it.vitalegi.cosucce.MockAuth.guest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class BudgetBoardResourceTests extends AbstractBudgetResourceTests {

    @Nested
    class Add {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var auth = MockAuth.member("user1");
            mockMvc.perform(request().with(auth)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        MockHttpServletRequestBuilder request() {
            return post("/budget/board") //
                    .content(asString(BudgetBoardAddOrUpdateRequest.builder().boardId(UUID.randomUUID()).name("SAMPLE").etag("aaa").build())) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Update {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request().with(boardOwner)) //
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

        @Test
        void when_memberOfTheBoardWithoutPermission_then_403() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request() {
            return put("/budget/board") //
                    .content(asString(BudgetBoardAddOrUpdateRequest.builder().boardId(boardId).name("SAMPLE2").etag("bbb").build())) //
                    .header("x-etag", "aaa") //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class GetById {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var response = payload(mockMvc.perform(request(boardId).with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.name").value("SAMPLE")) //
                    .andExpect(jsonPath("$.creationDate").exists()) //
                    .andExpect(jsonPath("$.lastUpdate").exists()) //
                    .andReturn(), BudgetBoard.class);

            assertEquals(2, response.getUsers().size());
            var owner = response.getUsers().stream().filter(e -> e.getUserId().equals(boardOwnerId)).findFirst().orElseThrow();
            assertEquals(boardOwnerId, owner.getUserId());
            assertEquals(boardId, owner.getBoardId());
            assertEquals(BudgetBoardRole.OWNER, owner.getRole());
            assertNotNull(owner.getCreationDate());
            assertNotNull(owner.getLastUpdate());
            var member = response.getUsers().stream().filter(e -> e.getUserId().equals(boardMemberId)).findFirst().orElseThrow();
            assertEquals(boardMemberId, member.getUserId());
            assertEquals(boardId, member.getBoardId());
            assertEquals(BudgetBoardRole.MEMBER, member.getRole());
            assertNotNull(member.getCreationDate());
            assertNotNull(member.getLastUpdate());
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
            return get("/budget/board/" + boardId) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Delete {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request(boardId).with(boardOwner)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
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
        void when_memberOfTheBoardWithoutPermission_then_403() throws Exception {
            mockMvc.perform(request(boardId).with(boardMember)) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request(UUID boardId) {
            return delete("/budget/board/" + boardId) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class GetBoards {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").exists()) //
                    .andExpect(jsonPath("$[0].name").exists()) //
                    .andExpect(jsonPath("$[0].users").exists()) //
                    .andExpect(jsonPath("$[0].creationDate").exists()) //
                    .andExpect(jsonPath("$[0].lastUpdate").exists());
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string(""));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            mockMvc.perform(request().with(guest())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedAccessException.class.getSimpleName()))
            ;
        }

        MockHttpServletRequestBuilder request() {
            return get("/budget/board") //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

}
