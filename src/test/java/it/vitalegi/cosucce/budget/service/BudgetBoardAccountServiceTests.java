package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.exception.ETagNotMatchedException;
import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_ACCOUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardAccountServiceTests {

    @Autowired
    DSLContext dsl;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    BudgetBoardAccountService budgetBoardAccountService;
    @Autowired
    UserDataUtil userDataUtil;

    @Nested
    class AddBoardAccount {
        @Test
        void given_validData_then_entityIsInitialized() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc");
            var actual = dsl.selectFrom(BUDGET_BOARD_ACCOUNT).where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId)).fetchOne();
            assertNotNull(actual);
            assertEquals(boardId, actual.getBoardId());
            assertEquals("aaa", actual.getLabel());
            assertEquals("bbb", actual.getIcon());
            assertEquals(true, actual.getEnabled());
            assertEquals("ccc", actual.getEtag());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_invalidData_then_exception() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();

            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardAccountService.addBoardAccount(null, boardId, "aaa", "bbb", true, "ccc"));
            assertEquals("AccountId is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, null, "aaa", "bbb", true, "ccc"));
            assertEquals("BoardId is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, boardId, null, "bbb", true, "ccc"));
            assertEquals("Label is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", null, true, "ccc"));
            assertEquals("Icon is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, null));
            assertEquals("ETag is missing", e.getMessage());

            assertThrows(DataIntegrityViolationException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, UUID.randomUUID(), "aaa", "bbb", true, "ccc"));

            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc");
            assertThrows(RuntimeException.class, () -> budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc"), "Duplicated entry");
        }
    }

    @Nested
    class GetBoardAccount {
        @Test
        void given_validData_then_allDataAreRetrieved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc");
            var actual = budgetBoardAccountService.getBoardAccounts(boardId);
            assertEquals(1, actual.size());
            var entry = actual.get(0);
            assertEquals(accountId, entry.getAccountId());
            assertEquals(boardId, entry.getBoardId());
            assertEquals("aaa", entry.getLabel());
            assertEquals("bbb", entry.getIcon());
            assertTrue(entry.isEnabled());
            assertEquals("ccc", entry.getEtag());
            assertNotNull(entry.getCreationDate());
            assertNotNull(entry.getLastUpdate());
        }

        @Test
        void given_validData_then_dataIsRetrieved() {
            var userId = userDataUtil.user();
            var boardId1 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId1, "Test", "etag", userId);
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var boardId3 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId3, "Test", "etag", userId);

            budgetBoardAccountService.addBoardAccount(UUID.randomUUID(), boardId1, "aaa1", "bbb", true, "ccc");
            budgetBoardAccountService.addBoardAccount(UUID.randomUUID(), boardId1, "aaa2", "bbb", true, "ccc");
            budgetBoardAccountService.addBoardAccount(UUID.randomUUID(), boardId2, "aaa3", "bbb", true, "ccc");
            assertEquals(List.of("aaa1", "aaa2"), budgetBoardAccountService.getBoardAccounts(boardId1).stream().map(BudgetBoardAccount::getLabel).sorted().toList());
            assertEquals(List.of("aaa3"), budgetBoardAccountService.getBoardAccounts(boardId2).stream().map(BudgetBoardAccount::getLabel).sorted().toList());
            assertEquals(List.of(), budgetBoardAccountService.getBoardAccounts(boardId3));
        }
    }

    @Nested
    class UpdateBoardAccount {
        @Test
        void given_validData_then_dataIsUpdated() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc");

            budgetBoardAccountService.updateBoardAccount(accountId, boardId, "1", "2", false, "3", "ccc");

            var actual = budgetBoardAccountService.getBoardAccounts(boardId).get(0);
            assertEquals(accountId, actual.getAccountId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals("1", actual.getLabel());
            assertEquals("2", actual.getIcon());
            assertFalse(actual.isEnabled());
            assertEquals("3", actual.getEtag());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_invalidEtag_then_fails() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag1", userId);
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "etag");

            var e = assertThrows(ETagNotMatchedException.class, () -> budgetBoardAccountService.updateBoardAccount(accountId, boardId, "aaa", "bbb", true, "yyy", "xxx"));
            assertEquals("etag", e.getExpectedEtag());
            assertEquals("xxx", e.getActualETag());
            assertEquals(accountId, e.getEntityId());
            assertEquals("BudgetAccount", e.getEntityClass());
        }

        @Test
        void given_multipleEntries_then_correctDataIsUpdated() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();

            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa1", "bbb", true, "ccc");
            budgetBoardAccountService.addBoardAccount(UUID.randomUUID(), boardId, "aaa2", "bbb", true, "ccc");

            budgetBoardAccountService.updateBoardAccount(accountId, boardId, "xxx", "2", false, "3", "ccc");

            assertEquals(List.of("aaa2", "xxx"), budgetBoardAccountService.getBoardAccounts(boardId).stream().map(BudgetBoardAccount::getLabel).sorted().toList());
        }
    }

    @Nested
    class DeleteBoardAccount {
        @Test
        void given_exists_then_entityIsDeleted() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId, boardId, "aaa", "bbb", true, "ccc");

            budgetBoardAccountService.deleteBoardAccount(accountId, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_ACCOUNT).where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId)).fetchOne());
        }

        @Test
        void given_multipleEntities_then_otherEntitiesArePreserved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var accountId1 = UUID.randomUUID();
            var accountId2 = UUID.randomUUID();

            budgetBoardAccountService.addBoardAccount(accountId1, boardId, "aaa1", "bbb", true, "ccc");
            budgetBoardAccountService.addBoardAccount(accountId2, boardId, "aaa2", "bbb", true, "ccc");

            budgetBoardAccountService.deleteBoardAccount(accountId1, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_ACCOUNT).where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId1)).fetchOne());
            assertNotNull(dsl.selectFrom(BUDGET_BOARD_ACCOUNT).where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId2)).fetchOne());
        }
    }
}
