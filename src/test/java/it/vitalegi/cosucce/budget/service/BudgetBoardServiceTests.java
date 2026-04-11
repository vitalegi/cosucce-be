package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
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
            var id = budgetBoardService.addBoard("Test", userId);
            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id)).fetchOne();
            assertNotNull(board);
            assertEquals("Test", board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());

            var boardUsers = dsl.selectFrom(BUDGET_BOARD_USER).where(BUDGET_BOARD_USER.BOARD_ID.eq(id)).fetch();
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
            var e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard(null, userId));
            assertEquals("Name is null", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () -> budgetBoardService.addBoard("xxx", null));
            assertEquals("UserId is null", e.getMessage());

            assertThrows(DataIntegrityViolationException.class, () -> budgetBoardService.addBoard("xxx", UUID.randomUUID()));
        }
    }

    @Nested
    class GetBoard {
        @Test
        void given_validData_then_boardIsRetrieved() {
            var userId1 = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test1", userId1);

            var actual = budgetBoardService.getBoard(id);
            assertEquals("Test1", actual.getName());
            assertEquals(id, actual.getBoardId());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());

            assertEquals(1, actual.getUsers().size());
            var user = actual.getUsers().get(0);
            assertEquals(userId1, user.getUserId());
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
            var id = budgetBoardService.addBoard("Test", userId);

            budgetBoardService.updateBoard(id, "Test2");

            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id)).fetchOne();
            assertNotNull(board);
            assertEquals("Test2", board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());
        }

        @Test
        void given_multipleEntries_then_correctBoardIsUpdated() {
            var userId = userDataUtil.user();
            var id1 = budgetBoardService.addBoard("Test1", userId);
            var id2 = budgetBoardService.addBoard("Test2", userId);

            budgetBoardService.updateBoard(id1, "xxx");

            var board = dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id2)).fetchOne();
            assertEquals("Test2", board.getName());
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void given_exists_then_entityIsDeleted() {
            var userId = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test", userId);

            budgetBoardService.deleteBoard(id);

            assertNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id)).fetchOne());
        }

        @Test
        void given_multipleEntities_then_otherEntitiesArePreserved() {
            var userId = userDataUtil.user();
            var id1 = budgetBoardService.addBoard("Test1", userId);
            var id2 = budgetBoardService.addBoard("Test2", userId);

            budgetBoardService.deleteBoard(id1);

            assertNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id1)).fetchOne());
            assertNotNull(dsl.selectFrom(BUDGET_BOARD).where(BUDGET_BOARD.BOARD_ID.eq(id2)).fetchOne());
        }
    }

    @Nested
    class GetBoardsVisibleByUser {
        @Test
        void given_hasBoard_then_dataIsRetrieved() {
            var userId1 = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test1", userId1);

            var actual = budgetBoardService.getBoardsVisibleByUser(userId1);
            assertEquals(1, actual.size());
            var board = actual.get(0);
            assertEquals("Test1", board.getName());
            assertEquals(id, board.getBoardId());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());

            assertEquals(1, board.getUsers().size());
            var user = board.getUsers().get(0);
            assertEquals(userId1, user.getUserId());
            assertEquals(BudgetBoardRole.OWNER, user.getRole());
            assertNotNull(user.getCreationDate());
            assertNotNull(user.getLastUpdate());
        }
        
        @Test
        void visibilityRules() {
            var userId1 = userDataUtil.user();
            var userId2 = userDataUtil.user();
            var userId3 = userDataUtil.user();

            budgetBoardService.addBoard("Test1", userId1);
            budgetBoardService.addBoard("Test2", userId1);
            budgetBoardService.addBoard("Test3", userId2);

            assertEquals(List.of("Test1", "Test2"), budgetBoardService.getBoardsVisibleByUser(userId1).stream().map(BudgetBoard::getName).sorted().toList());
            assertEquals(List.of("Test3"), budgetBoardService.getBoardsVisibleByUser(userId2).stream().map(BudgetBoard::getName).toList());
            assertTrue(budgetBoardService.getBoardsVisibleByUser(userId3).isEmpty());
        }
    }

}
