package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
import it.vitalegi.cosucce.budget.model.BudgetBoardEntry;
import it.vitalegi.cosucce.budget.service.BudgetBoardAccountService;
import it.vitalegi.cosucce.budget.service.BudgetBoardCategoryService;
import it.vitalegi.cosucce.budget.service.BudgetBoardEntryService;
import it.vitalegi.cosucce.budget.service.BudgetBoardService;
import it.vitalegi.cosucce.security.model.Permission;
import it.vitalegi.cosucce.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("budget/sync")
@RequiredArgsConstructor
public class BudgetSyncResource {

    final AuthenticationService authenticationService;
    final BudgetBoardService budgetBoardService;
    final BudgetBoardEntryService budgetBoardEntryService;
    final BudgetBoardAccountService budgetBoardAccountService;
    final BudgetBoardCategoryService budgetBoardCategoryService;

    @GetMapping("/board")
    public List<BudgetBoard> getBoards() {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        return budgetBoardService.getBoardsVisibleByUser(userId());
    }

    @GetMapping("/entry")
    public List<BudgetBoardEntry> getEntries() {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        return budgetBoardEntryService.getVisibleEntries(userId());
    }
    @GetMapping("/category")
    public List<BudgetBoardCategory> getCategories() {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        return budgetBoardCategoryService.getVisibleCategories(userId());
    }

    @GetMapping("/account")
    public List<BudgetBoardAccount> getAccounts() {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        return budgetBoardAccountService.getVisibleAccounts(userId());
    }

    UUID userId() {
        return authenticationService.identity().getUserId();
    }
}
