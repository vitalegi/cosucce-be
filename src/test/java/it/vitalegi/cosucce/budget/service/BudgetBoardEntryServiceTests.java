package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetCategoryType;
import it.vitalegi.cosucce.budget.exception.ETagNotMatchedException;
import it.vitalegi.cosucce.budget.model.BudgetBoardEntry;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_ENTRY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardEntryServiceTests {

    @Autowired
    DSLContext dsl;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    BudgetBoardEntryService budgetBoardEntryService;
    @Autowired
    BudgetBoardAccountService budgetBoardAccountService;
    @Autowired
    BudgetBoardCategoryService budgetBoardCategoryService;
    @Autowired
    UserDataUtil userDataUtil;

    UUID userId;
    UUID boardId;
    UUID accountId;
    UUID categoryId;
    UUID entryId;
    final BigDecimal AMOUNT = new BigDecimal("1000.55");
    final LocalDate DATE = LocalDate.of(2026, 1, 2);

    @BeforeEach
    void init() {
        userId = userDataUtil.user();
        boardId = UUID.randomUUID();
        budgetBoardService.addBoard(boardId, "Test", "etag", userId);
        accountId = UUID.randomUUID();
        budgetBoardAccountService.addBoardAccount(accountId, boardId, "account", "aaa", "red", true, "1");
        categoryId = UUID.randomUUID();
        budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");
        entryId = UUID.randomUUID();
    }

    void addEntry() {
        budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "1");
    }

    @Nested
    class AddBoardEntry {
        @Test
        void given_validData_then_entityIsInitialized() {
            budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "1");
            var actual = dsl.selectFrom(BUDGET_BOARD_ENTRY).where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)).fetchOne();
            assertNotNull(actual);
            assertEquals(entryId, actual.getEntryId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(DATE, actual.getDate());
            assertEquals(accountId, actual.getAccountId());
            assertEquals(categoryId, actual.getCategoryId());
            assertEquals("123", actual.getDescription());
            assertThat(actual.getAmount()).isEqualByComparingTo(AMOUNT);
            assertEquals("1", actual.getEtag());
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_invalidData_then_exception() {
            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(null, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("EntryId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, null, DATE, accountId, categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("BoardId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, null, accountId, categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("Date is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, null, categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("AccountId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, null, "123", AMOUNT, userId, "1"));
            assertEquals("CategoryId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", null, userId, "1"));
            assertEquals("Amount is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, null));
            assertEquals("ETag is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, UUID.randomUUID(), DATE, accountId, categoryId, "123", AMOUNT, userId, "1"));

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, UUID.randomUUID(), categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("Unknown Account", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, UUID.randomUUID(), "123", AMOUNT, userId, "1"));
            assertEquals("Unknown Category", e.getMessage());

            budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "1");
            assertThrows(RuntimeException.class, () -> budgetBoardEntryService.addBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "1"), "Duplicated entry");
        }

        @Test
        void given_accountOfDifferentBoard_then_exception() {
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var accountId2 = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId2, boardId2, "account", "aaa", "red", true, "1");

            var e = assertThrows(RuntimeException.class, () -> budgetBoardEntryService.addBoardEntry(UUID.randomUUID(), boardId, DATE, accountId2, categoryId, "123", AMOUNT, userId, "1"));
            assertEquals("Invalid Account", e.getMessage());
        }

        @Test
        void given_categoryOfDifferentBoard_then_exception() {
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var categoryId2 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId2, boardId2, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");

            var e = assertThrows(RuntimeException.class, () -> budgetBoardEntryService.addBoardEntry(UUID.randomUUID(), boardId, DATE, accountId, categoryId2, "123", AMOUNT, userId, "1"));
            assertEquals("Invalid Category", e.getMessage());
        }
    }

    @Nested
    class GetBoardEntries {
        @Test
        void given_validData_then_allDataAreRetrieved() {
            addEntry();
            var actual = budgetBoardEntryService.getBoardEntries(boardId);
            assertEquals(1, actual.size());
            var entry = actual.get(0);
            assertEquals(entryId, entry.getEntryId());
            assertEquals(boardId, entry.getBoardId());
            assertEquals(DATE, entry.getDate());
            assertEquals(accountId, entry.getAccountId());
            assertEquals(categoryId, entry.getCategoryId());
            assertEquals("123", entry.getDescription());
            assertThat(entry.getAmount()).isEqualByComparingTo(AMOUNT);
            assertEquals("1", entry.getEtag());
            assertNotNull(entry.getCreationDate());
            assertNotNull(entry.getLastUpdate());
        }

        @Test
        void given_validData_then_dataIsRetrieved() {
            addEntry();

            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var accountId2 = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId2, boardId2, "account", "aaa", "red", true, "1");
            var categoryId2 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId2, boardId2, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");

            var boardId3 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId3, "Test", "etag", userId);
            var accountId3 = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId3, boardId3, "account", "aaa", "red", true, "1");
            var categoryId3 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId3, boardId3, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");

            var entryId2 = UUID.randomUUID();
            budgetBoardEntryService.addBoardEntry(entryId2, boardId, DATE, accountId, categoryId, "456", AMOUNT, userId, "1");
            var entryId3 = UUID.randomUUID();
            budgetBoardEntryService.addBoardEntry(entryId3, boardId2, DATE, accountId2, categoryId2, "789", AMOUNT, userId, "1");

            assertEquals(Set.of("123", "456"), budgetBoardEntryService.getBoardEntries(boardId).stream().map(BudgetBoardEntry::getDescription).collect(Collectors.toSet()));
            assertEquals(Set.of("789"), budgetBoardEntryService.getBoardEntries(boardId2).stream().map(BudgetBoardEntry::getDescription).collect(Collectors.toSet()));
            assertEquals(List.of(), budgetBoardEntryService.getBoardEntries(boardId3));
        }
    }

    @Nested
    class UpdateBoardEntry {
        @Test
        void given_validData_then_dataIsUpdated() {
            addEntry();
            var accountId2 = UUID.randomUUID();
            var categoryId2 = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId2, boardId, "account", "aaa", "red", true, "1");
            budgetBoardCategoryService.addBoardCategory(categoryId2, boardId, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");

            budgetBoardEntryService.updateBoardEntry(entryId, boardId, LocalDate.of(1990, 10, 11), accountId2, categoryId2, "xxx", BigDecimal.valueOf(100), userId, "2", "1");

            var actual = budgetBoardEntryService.getBoardEntries(boardId);
            var entry = actual.get(0);
            assertEquals(entryId, entry.getEntryId());
            assertEquals(boardId, entry.getBoardId());
            assertEquals(LocalDate.of(1990, 10, 11), entry.getDate());
            assertEquals(accountId2, entry.getAccountId());
            assertEquals(categoryId2, entry.getCategoryId());
            assertEquals("xxx", entry.getDescription());
            assertThat(entry.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertEquals("2", entry.getEtag());
            assertEquals(userId, entry.getLastUpdatedBy());
            assertNotNull(entry.getCreationDate());
            assertNotNull(entry.getLastUpdate());
        }

        @Test
        void given_invalidData_then_exception() {
            addEntry();
            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(null, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("EntryId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, null, DATE, accountId, categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("BoardId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, null, accountId, categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("Date is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, null, categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("AccountId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, null, "123", AMOUNT, userId, "2", "1"));
            assertEquals("CategoryId is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", null, userId, "2", "1"));
            assertEquals("Amount is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, null, "1"));
            assertEquals("New ETag is missing", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "2", null));
            assertEquals("Old ETag is missing", e.getMessage());

            assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, UUID.randomUUID(), DATE, accountId, categoryId, "123", AMOUNT, userId, "2", "1"));
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, UUID.randomUUID(), categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("Unknown Account", e.getMessage());
            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, UUID.randomUUID(), "123", AMOUNT, userId, "2", "1"));
            assertEquals("Unknown Category", e.getMessage());
        }

        @Test
        void given_invalidEtag_then_fails() {
            addEntry();
            var e = assertThrows(ETagNotMatchedException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, "123", AMOUNT, userId, "2", "x"));
            assertEquals("1", e.getExpectedEtag());
            assertEquals("x", e.getActualETag());
            assertEquals(entryId, e.getEntityId());
            assertEquals("BudgetEntry", e.getEntityClass());
        }

        @Test
        void given_accountOfDifferentBoard_then_exception() {
            addEntry();
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var accountId2 = UUID.randomUUID();
            budgetBoardAccountService.addBoardAccount(accountId2, boardId2, "account", "aaa", "red", true, "1");

            var e = assertThrows(RuntimeException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId2, categoryId, "123", AMOUNT, userId, "2", "1"));
            assertEquals("Invalid Account", e.getMessage());
        }

        @Test
        void given_categoryOfDifferentBoard_then_exception() {
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);
            var categoryId2 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId2, boardId2, "category", BudgetCategoryType.CREDIT, "aaa", "red", true, "1");

            addEntry();
            var e = assertThrows(RuntimeException.class, () -> budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId2, "123", AMOUNT, userId, "2", "1"));
            assertEquals("Invalid Category", e.getMessage());
        }

        @Test
        void given_multipleEntries_then_correctDataIsUpdated() {
            addEntry();
            budgetBoardEntryService.addBoardEntry(UUID.randomUUID(), boardId, DATE, accountId, categoryId, "456", AMOUNT, userId, "2");

            budgetBoardEntryService.updateBoardEntry(entryId, boardId, DATE, accountId, categoryId, "xxx", AMOUNT, userId, "2", "1");

            assertEquals(Set.of("456", "xxx"), budgetBoardEntryService.getBoardEntries(boardId).stream().map(BudgetBoardEntry::getDescription).collect(Collectors.toSet()));
        }
    }

    @Nested
    class DeleteBoardEntry {
        @Test
        void given_exists_then_entityIsDeleted() {
            addEntry();
            budgetBoardEntryService.deleteBoardEntry(entryId, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_ENTRY).where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)).fetchOne());
        }

        @Test
        void given_multipleEntities_then_otherEntitiesArePreserved() {
            addEntry();
            var entryId2 = UUID.randomUUID();
            budgetBoardEntryService.addBoardEntry(entryId2, boardId, DATE, accountId, categoryId, "456", AMOUNT, userId, "2");

            budgetBoardEntryService.deleteBoardEntry(entryId, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_ENTRY).where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)).fetchOne());
            assertNotNull(dsl.selectFrom(BUDGET_BOARD_ENTRY).where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId2)).fetchOne());
        }
    }
}
