package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
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

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_CATEGORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardCategoryServiceTests {

    @Autowired
    DSLContext dsl;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    BudgetBoardCategoryService budgetBoardCategoryService;
    @Autowired
    UserDataUtil userDataUtil;

    @Nested
    class AddBoardCategory {
        @Test
        void given_validData_then_entityIsInitialized() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc");
            var actual = dsl.selectFrom(BUDGET_BOARD_CATEGORY).where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)).fetchOne();
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
            var categoryId = UUID.randomUUID();
            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardCategoryService.addBoardCategory(null, boardId, "aaa", "bbb", true, "ccc"));
            assertEquals("CategoryId is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, null, "aaa", "bbb", true, "ccc"));
            assertEquals("BoardId is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, boardId, null, "bbb", true, "ccc"));
            assertEquals("Label is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", null, true, "ccc"));
            assertEquals("Icon is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, null));
            assertEquals("ETag is missing", e.getMessage());

            assertThrows(DataIntegrityViolationException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, UUID.randomUUID(), "aaa", "bbb", true, "ccc"));

            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc");
            assertThrows(RuntimeException.class, () -> budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc"));
        }
    }

    @Nested
    class GetBoardCategory {
        @Test
        void given_validData_then_allDataAreRetrieved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc");
            var actual = budgetBoardCategoryService.getBoardCategories(boardId);
            assertEquals(1, actual.size());
            var entry = actual.get(0);
            assertEquals(categoryId, entry.getCategoryId());
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
            budgetBoardCategoryService.addBoardCategory(UUID.randomUUID(), boardId1, "aaa1", "bbb", true, "ccc");
            budgetBoardCategoryService.addBoardCategory(UUID.randomUUID(), boardId1, "aaa2", "bbb", true, "ccc");
            budgetBoardCategoryService.addBoardCategory(UUID.randomUUID(), boardId2, "aaa3", "bbb", true, "ccc");
            assertEquals(List.of("aaa1", "aaa2"), budgetBoardCategoryService.getBoardCategories(boardId1).stream().map(BudgetBoardCategory::getLabel).sorted().toList());
            assertEquals(List.of("aaa3"), budgetBoardCategoryService.getBoardCategories(boardId2).stream().map(BudgetBoardCategory::getLabel).sorted().toList());
            assertEquals(List.of(), budgetBoardCategoryService.getBoardCategories(boardId3));
        }
    }

    @Nested
    class UpdateBoardCategory {
        @Test
        void given_validData_then_dataIsUpdated() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc");

            budgetBoardCategoryService.updateBoardCategory(categoryId, boardId, "1", "2", false, "3");

            var actual = budgetBoardCategoryService.getBoardCategories(boardId).get(0);
            assertEquals(categoryId, actual.getCategoryId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals("1", actual.getLabel());
            assertEquals("2", actual.getIcon());
            assertFalse(actual.isEnabled());
            assertEquals("3", actual.getEtag());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_multipleEntries_then_correctDataIsUpdated() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa1", "bbb", true, "ccc");
            budgetBoardCategoryService.addBoardCategory(UUID.randomUUID(), boardId, "aaa2", "bbb", true, "ccc");

            budgetBoardCategoryService.updateBoardCategory(categoryId, boardId, "xxx", "2", false, "3");

            assertEquals(List.of("aaa2", "xxx"), budgetBoardCategoryService.getBoardCategories(boardId).stream().map(BudgetBoardCategory::getLabel).sorted().toList());
        }
    }

    @Nested
    class DeleteBoardCategory {
        @Test
        void given_exists_then_entityIsDeleted() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId, boardId, "aaa", "bbb", true, "ccc");

            budgetBoardCategoryService.deleteBoardCategory(categoryId, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_CATEGORY).where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)).fetchOne());
        }

        @Test
        void given_multipleEntities_then_otherEntitiesArePreserved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var categoryId1 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId1, boardId, "aaa1", "bbb", true, "ccc");
            var categoryId2 = UUID.randomUUID();
            budgetBoardCategoryService.addBoardCategory(categoryId2, boardId, "aaa2", "bbb", true, "ccc");

            budgetBoardCategoryService.deleteBoardCategory(categoryId1, boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD_CATEGORY).where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId1)).fetchOne());
            assertNotNull(dsl.selectFrom(BUDGET_BOARD_CATEGORY).where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId2)).fetchOne());
        }
    }
}
