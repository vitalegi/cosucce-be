package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constant.BudgetBoardPermission;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.model.BudgetBoardUser;
import it.vitalegi.cosucce.security.exception.UnauthorizedBoardAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetAuthenticationService {

    final BudgetBoardService budgetBoardService;

    public void checkPermission(UUID userId, UUID boardId, BudgetBoardPermission permission) {
        if (!hasPermission(userId, boardId, permission)) {
            var permissions = getPermissions(userId, boardId).toList();
            log.info("User {} doesn't have permission {} on board {}. Available. {}", userId, permission, userId, permissions);
            throw new UnauthorizedBoardAccessException(permissions, permission);
        }
    }

    public boolean hasPermission(UUID userId, UUID boardId, BudgetBoardPermission permission) {
        return getPermissions(userId, boardId).anyMatch(p -> p == permission);
    }

    protected Stream<BudgetBoardPermission> getPermissions(UUID userId, UUID boardId) {
        return budgetBoardService.getBoardUsers(boardId).stream() //
                .filter(e -> e.getUserId().equals(userId)) //
                .map(BudgetBoardUser::getRole) //
                .flatMap(this::getPermissions);
    }

    protected Stream<BudgetBoardPermission> getPermissions(BudgetBoardRole role) {
        return role.getPermissions().stream();
    }

}
