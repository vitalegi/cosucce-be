package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.exception.ETagNotMatchedException;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
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

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardServiceTests {

    @Autowired
    DSLContext dsl;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    UserDataUtil userDataUtil;

    @Nested
    class AddBoard {
        @Test
        void given_validData_then_entityIsInitialized() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);
            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId)).fetchOne();
            assertNotNull(board);
            assertEquals("Test", board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());

            var boardUsers = dsl.selectFrom(BUDGET_BOARD_USER).where(BUDGET_BOARD_USER.BOARD_ID.eq(boardId)).fetch();
            assertEquals(1, boardUsers.size());
            var boardUser = boardUsers.get(0);
            assertEquals(userId, boardUser.getUserId());
            assertEquals(BudgetBoardRole.OWNER, boardUser.getBudgetBoardRole());
            assertNotNull(boardUser.getCreationDate());
            assertNotNull(boardUser.getLastUpdate());
        }

        @Test
        void given_invalidData_then_exception() {
            var userId = userDataUtil.user();
            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard(null, "xxx", "etag", userId));
            assertEquals("BoardId is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard(UUID.randomUUID(), "", "etag", userId));
            assertEquals("Name is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard(UUID.randomUUID(), "xxx", "", userId));
            assertEquals("ETag is missing", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard(UUID.randomUUID(), "xxx", "etag", null));
            assertEquals("UserId is missing", e.getMessage());

            var boardId = UUID.randomUUID();
            assertThrows(DataIntegrityViolationException.class, () -> budgetBoardService.addBoard(boardId, "xxx", "xxx", UUID.randomUUID()));

            budgetBoardService.addBoard(boardId, "xxx", "xxx", userId);
            assertThrows(DataIntegrityViolationException.class, () -> budgetBoardService.addBoard(boardId, "xxx", "xxx", userId), "Duplicated boardId");
        }
    }

    @Nested
    class GetBoard {
        @Test
        void given_validData_then_boardIsRetrieved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test1", "etag", userId);

            var actual = budgetBoardService.getBoard(boardId);
            assertEquals("Test1", actual.getName());
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());

            assertEquals(1, actual.getUsers().size());
            var user = actual.getUsers().get(0);
            assertEquals(userId, user.getUserId());
            assertEquals(BudgetBoardRole.OWNER, user.getRole());
            assertNotNull(user.getCreationDate());
            assertNotNull(user.getLastUpdate());
        }
    }

    @Nested
    class UpdateBoard {
        @Test
        void given_validData_then_boardIsUpdated() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag1", userId);

            budgetBoardService.updateBoard(boardId, "Test2", "etag2", "etag1");

            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId)).fetchOne();
            assertNotNull(board);
            assertEquals("Test2", board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());
        }

        @Test
        void given_invalidEtag_then_fails() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag1", userId);

            var e = assertThrows(ETagNotMatchedException.class, () ->budgetBoardService.updateBoard(boardId, "Test2", "etag2", "xxx"));
            assertEquals("etag1", e.getExpectedEtag());
            assertEquals("xxx", e.getActualETag());
            assertEquals(boardId, e.getEntityId());
            assertEquals("BudgetBoard", e.getEntityClass());
        }

        @Test
        void given_multipleEntries_then_correctBoardIsUpdated() {
            var userId = userDataUtil.user();
            var boardId1 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId1, "Test1", "etag", userId);
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test2", "etag", userId);

            budgetBoardService.updateBoard(boardId1, "xxx", "etag2", "etag");

            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId2)).fetchOne();
            assertEquals("Test2", board.getName());
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void given_exists_then_entityIsDeleted() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test", "etag", userId);

            budgetBoardService.deleteBoard(boardId);

            assertNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId)).fetchOne());
        }

        @Test
        void given_multipleEntities_then_otherEntitiesArePreserved() {
            var userId = userDataUtil.user();
            var boardId1 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId1, "Test", "etag", userId);
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test", "etag", userId);

            budgetBoardService.deleteBoard(boardId1);

            assertNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId1)).fetchOne());
            assertNotNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(boardId2)).fetchOne());
        }
    }

    @Nested
    class GetBoardsVisibleByUser {
        @Test
        void given_hasBoard_then_dataIsRetrieved() {
            var userId = userDataUtil.user();
            var boardId = UUID.randomUUID();
            budgetBoardService.addBoard(boardId, "Test1", "etag", userId);

            var actual = budgetBoardService.getBoardsVisibleByUser(userId);
            assertEquals(1, actual.size());
            var board = actual.get(0);
            assertEquals("Test1", board.getName());
            assertEquals(boardId, board.getBoardId());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());

            assertEquals(1, board.getUsers().size());
            var user = board.getUsers().get(0);
            assertEquals(userId, user.getUserId());
            assertEquals(BudgetBoardRole.OWNER, user.getRole());
            assertNotNull(user.getCreationDate());
            assertNotNull(user.getLastUpdate());
        }

        @Test
        void visibilityRules() {
            var userId1 = userDataUtil.user();
            var userId2 = userDataUtil.user();
            var userId3 = userDataUtil.user();

            var boardId1 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId1, "Test1", "etag", userId1);
            var boardId2 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId2, "Test2", "etag", userId1);
            var boardId3 = UUID.randomUUID();
            budgetBoardService.addBoard(boardId3, "Test3", "etag", userId2);

            assertEquals(List.of("Test1", "Test2"), budgetBoardService.getBoardsVisibleByUser(userId1).stream().map(BudgetBoard::getName).sorted().toList());
            assertEquals(List.of("Test3"), budgetBoardService.getBoardsVisibleByUser(userId2).stream().map(BudgetBoard::getName).toList());
            assertTrue(budgetBoardService.getBoardsVisibleByUser(userId3).isEmpty());
        }
    }

}
