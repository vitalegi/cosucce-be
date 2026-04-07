package it.vitalegi.cosucce.security.exception;

import it.vitalegi.cosucce.budget.constant.BudgetBoardPermission;
import lombok.Getter;

import java.util.List;

@Getter
public class UnauthorizedBoardAccessException extends RuntimeException {
    List<BudgetBoardPermission> userPermissions;
    BudgetBoardPermission missingPermission;

    public UnauthorizedBoardAccessException(List<BudgetBoardPermission> userPermissions, BudgetBoardPermission missingPermission) {
        super("Missing permission " + missingPermission + ". Available: " + userPermissions);
        this.userPermissions = userPermissions;
        this.missingPermission = missingPermission;
    }
}
