package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constant.BudgetBoardPermission;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAccountAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddResponse;
import it.vitalegi.cosucce.budget.dto.BudgetBoardCategoryAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
import it.vitalegi.cosucce.budget.service.BudgetAuthenticationService;
import it.vitalegi.cosucce.budget.service.BudgetBoardAccountService;
import it.vitalegi.cosucce.budget.service.BudgetBoardCategoryService;
import it.vitalegi.cosucce.budget.service.BudgetBoardService;
import it.vitalegi.cosucce.security.model.Permission;
import it.vitalegi.cosucce.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("budget/board")
@RequiredArgsConstructor
public class BudgetBoardResource {

    final AuthenticationService authenticationService;
    final BudgetAuthenticationService budgetAuthenticationService;
    final BudgetBoardService budgetBoardService;
    final BudgetBoardAccountService budgetBoardAccountService;
    final BudgetBoardCategoryService budgetBoardCategoryService;

    @PostMapping
    public void addBoard(@RequestBody BudgetBoardAddRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetBoardService.addBoard(request.getBoardId(), request.getName(), request.getEtag(), userId());
    }

    @PutMapping("/{boardId}")
    public void updateBoard(@PathVariable("boardId") UUID boardId, @RequestBody BudgetBoardUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardService.updateBoard(boardId, request.getName());
    }

    @GetMapping("/{boardId}")
    public BudgetBoard getBoardById(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.VIEW);
        return budgetBoardService.getBoard(boardId);
    }

    @GetMapping
    public List<BudgetBoard> getBoards() {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        return budgetBoardService.getBoardsVisibleByUser(userId());
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardService.deleteBoard(boardId);
    }

    @PostMapping("/{boardId}/category")
    public void addCategory(@PathVariable("boardId") UUID boardId, @RequestBody BudgetBoardCategoryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardCategoryService.addBoardCategory(request.getCategoryId(), boardId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getEtag());
    }

    @PutMapping("/{boardId}/category/{categoryId}")
    public void updateCategory(@PathVariable("boardId") UUID boardId, @PathVariable("categoryId") UUID categoryId, @RequestBody BudgetBoardCategoryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardCategoryService.updateBoardCategory(categoryId, boardId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getEtag());
    }

    @GetMapping("/{boardId}/category")
    public List<BudgetBoardCategory> getCategoriesByBoardId(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.VIEW);
        return budgetBoardCategoryService.getBoardCategories(boardId);
    }

    @DeleteMapping("/{boardId}/category/{categoryId}")
    public void deleteCategory(@PathVariable("boardId") UUID boardId, @PathVariable("categoryId") UUID categoryId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardCategoryService.deleteBoardCategory(categoryId, boardId);
    }

    @PostMapping("/{boardId}/account")
    public void addAccount(@PathVariable("boardId") UUID boardId, @RequestBody BudgetBoardAccountAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardAccountService.addBoardAccount(request.getAccountId(), boardId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getEtag());
    }

    @PutMapping("/{boardId}/account/{accountId}")
    public void updateAccount(@PathVariable("boardId") UUID boardId, @PathVariable("accountId") UUID accountId, @RequestBody BudgetBoardAccountAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardAccountService.updateBoardAccount(accountId, boardId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getEtag());
    }

    @GetMapping("/{boardId}/account")
    public List<BudgetBoardAccount> getAccountsByBoardId(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.VIEW);
        return budgetBoardAccountService.getBoardAccounts(boardId);
    }

    @DeleteMapping("/{boardId}/account/{accountId}")
    public void deleteAccount(@PathVariable("boardId") UUID boardId, @PathVariable("accountId") UUID accountId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardAccountService.deleteBoardAccount(accountId, boardId);
    }

    UUID userId() {
        return authenticationService.identity().getUserId();
    }
}
