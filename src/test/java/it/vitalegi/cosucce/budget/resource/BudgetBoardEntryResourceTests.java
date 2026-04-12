package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.budget.dto.BudgetBoardEntryAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoardEntry;
import it.vitalegi.cosucce.budget.service.BudgetBoardEntryService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
public class BudgetBoardEntryResourceTests extends AbstractBudgetResourceTests {

    @MockitoBean
    BudgetBoardEntryService budgetBoardEntryService;
    @Autowired
    BudgetBoardService budgetBoardService;

    final LocalDate DATE = LocalDate.now();
    final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    final String DESCRIPTION = "xxx";
    final String ETAG = "1";
    UUID userId;
    UUID accountId;
    UUID categoryId;
    UUID entryId;

    @BeforeEach
    void init() {
        super.init();
        userId = boardMemberId;
        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        entryId = UUID.randomUUID();
    }

    @Nested
    class Add {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
            verify(budgetBoardEntryService, times(1)).addBoardEntry(entryId, boardId, DATE, accountId, categoryId, DESCRIPTION, AMOUNT, userId, ETAG);
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
            return post("/budget/board/" + boardId + "/entry") //
                    .content(asString(BudgetBoardEntryAddOrUpdateRequest.builder().entryId(entryId).accountId(accountId).categoryId(categoryId).date(DATE).description(DESCRIPTION).amount(AMOUNT).etag(ETAG).build())) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Update {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
            verify(budgetBoardEntryService, times(1)).updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, DESCRIPTION, AMOUNT, userId, "2", ETAG);
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
            return put("/budget/board/" + boardId + "/entry") //
                    .content(asString(BudgetBoardEntryAddOrUpdateRequest.builder().entryId(entryId).accountId(accountId).categoryId(categoryId).date(DATE).description(DESCRIPTION).amount(AMOUNT).etag("2").build())) //
                    .header("x-etag", ETAG) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class GetById {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            when(budgetBoardEntryService.getBoardEntries(boardId)).thenReturn(List.of( //
                    BudgetBoardEntry.builder().entryId(entryId).boardId(boardId).accountId(accountId).categoryId(categoryId).date(DATE).amount(AMOUNT).etag(ETAG).lastUpdatedBy(userId).creationDate(LocalDateTime.now()).lastUpdate(LocalDateTime.now()).build() //
            ));
            mockMvc.perform(request().with(boardMember)) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$[0].date").value("2026-04-12")) //
                    .andExpect(jsonPath("$[0].amount").value(AMOUNT.toPlainString())) //
                    .andExpect(jsonPath("$[0].etag").value(ETAG)) //
                    .andExpect(jsonPath("$[0].lastUpdatedBy").value(userId.toString())) //
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
        void when_notPartOfTheBoard_then_403() throws Exception {
            mockMvc.perform(request().with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        @Test
        void when_unknownBoard_then_403() throws Exception {
            mockMvc.perform(request().with(MockAuth.member())) //
                    .andExpect(status().is(403)) //
                    .andExpect(jsonPath("$.error").value(UnauthorizedBoardAccessException.class.getSimpleName()));
        }

        MockHttpServletRequestBuilder request() {
            return get("/budget/board/" + boardId + "/entry") //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

    @Nested
    class Delete {
        @Test
        void when_authenticated_then_ok() throws Exception {
            mockMvc.perform(request(boardId, entryId).with(boardOwner)) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().string(""));
            verify(budgetBoardEntryService, times(1)).deleteBoardEntry(entryId, boardId);
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
            return delete("/budget/board/" + boardId + "/entry/" + accountId) //
                    .contentType(MediaType.APPLICATION_JSON).with(csrf());
        }
    }

}
