package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.repository.BudgetBoardRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class BudgetBoardServiceTests {

    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    UserDataUtil userDataUtil;
    @Autowired
    BudgetBoardRepository budgetBoardRepository;

    @Nested
    class AddBoard {
        @Test
        void given_validData_then_boardIsInitialized() {
            var userId = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test", userId);

            var actual = budgetBoardRepository.findWithUsersByBoardId(id);
            assertTrue(actual.isPresent());
            var e = actual.get();
            assertEquals("Test", e.getName());
            assertNotNull(e.getCreationDate());
            assertNotNull(e.getLastUpdate());

            var users = e.getBoardUsers();
            assertEquals(1, users.size());
            var user = users.get(0);
            assertEquals(userId, user.getId().getUserId());
            assertEquals(BudgetBoardRole.OWNER, user.getRole());
            assertNotNull(user.getCreationDate());
            assertNotNull(user.getLastUpdate());
        }
    }

    @Nested
    class GetBoard {
        @Test
        void given_validData_then_boardIsRetrieved() {
            var userId = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test", userId);

            var actual = budgetBoardService.getBudgetBoard(id);
            assertEquals("Test", actual.getName());
            assertEquals(id, actual.getBoardId());
            assertEquals(1, actual.getUsers().size());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());

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
        void given_validData_then_boardIsInitialized() {
            var userId = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test", userId);

            budgetBoardService.updateBoard(id, "Test2");
            var actual = budgetBoardRepository.findWithUsersByBoardId(id);
            assertTrue(actual.isPresent());
            var e = actual.get();
            assertEquals("Test2", e.getName());
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void given_validData_then_boardIsInitialized() {
            var userId = userDataUtil.user();
            var id = budgetBoardService.addBoard("Test", userId);

            budgetBoardService.deleteBoard(id);
            var actual = budgetBoardRepository.findWithUsersByBoardId(id);
            assertFalse(actual.isPresent());
        }
    }
}
