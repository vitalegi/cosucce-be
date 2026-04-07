package it.vitalegi.cosucce.budget.constant;

import lombok.Getter;

import java.util.List;

import static it.vitalegi.cosucce.budget.constant.BudgetBoardPermission.ADMIN;
import static it.vitalegi.cosucce.budget.constant.BudgetBoardPermission.EDIT;
import static it.vitalegi.cosucce.budget.constant.BudgetBoardPermission.VIEW;

public enum BudgetBoardRole {
    OWNER(List.of(VIEW, EDIT, ADMIN)), //
    MEMBER(List.of(VIEW, EDIT));

    @Getter
    private final List<BudgetBoardPermission> permissions;

    BudgetBoardRole(List<BudgetBoardPermission> permissions) {
        this.permissions = permissions;
    }
}
