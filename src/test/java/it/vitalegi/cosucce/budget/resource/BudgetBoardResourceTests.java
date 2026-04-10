package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import it.vitalegi.cosucce.budget.service.BudgetBoardService;
import it.vitalegi.cosucce.security.exception.UnauthorizedBoardAccessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

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
public class BudgetBoardResourceTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BudgetBoardUserRepository budgetBoardUserRepository;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    UserDataUtil userDataUtil;
    @Autowired
    ObjectMapper objectMapper;

    UUID boardOwnerId;
    RequestPostProcessor boardOwner;
    UUID boardMemberId;
    RequestPostProcessor boardMember;
    UUID boardId;

    @BeforeEach
    void init() {
        var subject = UUID.randomUUID().toString();
        boardOwnerId = userDataUtil.user(subject, List.of("MEMBER"));
        boardOwner = MockAuth.member(subject);
        boardId = budgetBoardService.addBoard("SAMPLE", boardOwnerId);

        subject = UUID.randomUUID().toString();
        boardMemberId = userDataUtil.user(subject, List.of("MEMBER"));
        boardMember = MockAuth.member(subject);
        budgetBoardUserRepository.add(boardId, boardMemberId, BudgetBoardRole.MEMBER);
        log.info("BoardId: {}", boardId);
        log.info("OwnerId: {}", boardOwnerId);
        log.info("MemberId: {}", boardMemberId);
    }

    @Nested
    class Add {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var auth = MockAuth.member("user1");
            mockMvc.perform(request().with(auth)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").isString());
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(request()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string("")) //
                    .andReturn();
        }

        MockHttpServletRequestBuilder request() {
            return post("/budget/board") //
                    .content(asString(BudgetBoardAddRequest.builder().name("SAMPLE").build())) //
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
                    .andExpect(content().string("")) //
                    .andReturn();
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request().with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
        }

        @Test
        void when_memberOfTheBoardWithoutPermission_then_403() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
        }

        MockHttpServletRequestBuilder request() {
            return put("/budget/board") //
                    .content(asString(BudgetBoardUpdateRequest.builder().boardId(boardId).name("SAMPLE2").build())) //
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
                    .andExpect(content().string("")) //
                    .andReturn();
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request(boardId).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
        }

        @Test
        void when_unknownBoard_then_403() throws Exception {
            mockMvc.perform(request(UUID.randomUUID()).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
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
                    .andExpect(content().string("")) //
                    .andReturn();
        }

        @Test
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request(boardId).with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
        }

        @Test
        void when_memberOfTheBoardWithoutPermission_then_403() throws Exception {
            mockMvc.perform(request(boardId).with(boardMember)) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName())) //
                    .andReturn();
        }

        MockHttpServletRequestBuilder request(UUID boardId) {
            return delete("/budget/board/" + boardId) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    String asString(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    <E> E payload(MvcResult result, Class<E> clazz) {
        try {
            return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
