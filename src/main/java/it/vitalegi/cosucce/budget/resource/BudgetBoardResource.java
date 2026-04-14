package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constant.BudgetBoardPermission;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAccountAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardCategoryAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardEntryAddOrUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
import it.vitalegi.cosucce.budget.model.BudgetBoardEntry;
import it.vitalegi.cosucce.budget.service.BudgetAuthenticationService;
import it.vitalegi.cosucce.budget.service.BudgetBoardAccountService;
import it.vitalegi.cosucce.budget.service.BudgetBoardCategoryService;
import it.vitalegi.cosucce.budget.service.BudgetBoardEntryService;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    final BudgetBoardEntryService budgetBoardEntryService;
    final BudgetBoardAccountService budgetBoardAccountService;
    final BudgetBoardCategoryService budgetBoardCategoryService;

    @PostMapping
    public void addBoard(@RequestBody BudgetBoardAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetBoardService.addBoard(request.getBoardId(), request.getName(), request.getEtag(), userId());
    }

    @PutMapping
    public void updateBoard(@RequestHeader("x-etag") String oldEtag, @RequestBody BudgetBoardAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), request.getBoardId(), BudgetBoardPermission.ADMIN);
        budgetBoardService.updateBoard(request.getBoardId(), request.getName(), request.getEtag(), oldEtag);
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

    @PostMapping("/{boardId}/entry")
    public void addEntry(@PathVariable("boardId") UUID boardId, @RequestBody BudgetBoardEntryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardEntryService.addBoardEntry(request.getEntryId(), boardId, request.getDate(), request.getAccountId(), request.getCategoryId(), request.getDescription(), request.getAmount(), userId(), request.getEtag());
    }

    @PutMapping("/{boardId}/entry")
    public void updateEntry(@PathVariable("boardId") UUID boardId, @RequestHeader("x-etag") String oldEtag, @RequestBody BudgetBoardEntryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardEntryService.updateBoardEntry(request.getEntryId(), boardId, request.getDate(), request.getAccountId(), request.getCategoryId(), request.getDescription(), request.getAmount(), userId(), request.getEtag(), oldEtag);
    }

    @GetMapping("/{boardId}/entry")
    public List<BudgetBoardEntry> getEntriesByBoardId(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.VIEW);
        return budgetBoardEntryService.getBoardEntries(boardId);
    }

    @DeleteMapping("/{boardId}/entry/{entryId}")
    public void deleteEntry(@PathVariable("boardId") UUID boardId, @PathVariable("entryId") UUID entryId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardEntryService.deleteBoardEntry(entryId, boardId);
    }


    @PostMapping("/{boardId}/category")
    public void addCategory(@PathVariable("boardId") UUID boardId, @RequestBody BudgetBoardCategoryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardCategoryService.addBoardCategory(request.getCategoryId(), boardId, request.getLabel(), request.getType(), request.getIcon(), request.getColor(), request.isEnabled(), request.getEtag());
    }

    @PutMapping("/{boardId}/category")
    public void updateCategory(@PathVariable("boardId") UUID boardId, @RequestHeader("x-etag") String oldEtag, @RequestBody BudgetBoardCategoryAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardCategoryService.updateBoardCategory(request.getCategoryId(), boardId, request.getLabel(), request.getType(), request.getIcon(), request.getColor(), request.isEnabled(), request.getEtag(), oldEtag);
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
        budgetBoardAccountService.addBoardAccount(request.getAccountId(), boardId, request.getLabel(), request.getIcon(), request.getColor(), request.isEnabled(), request.getEtag());
    }

    @PutMapping("/{boardId}/account")
    public void updateAccount(@PathVariable("boardId") UUID boardId, @RequestHeader("x-etag") String oldEtag, @RequestBody BudgetBoardAccountAddOrUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.EDIT);
        budgetBoardAccountService.updateBoardAccount(request.getAccountId(), boardId, request.getLabel(), request.getIcon(), request.getColor(), request.isEnabled(), request.getEtag(), oldEtag);
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
