package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constant.BudgetBoardPermission;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddRequest;
import it.vitalegi.cosucce.budget.dto.BudgetBoardAddResponse;
import it.vitalegi.cosucce.budget.dto.BudgetBoardUpdateRequest;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.service.BudgetAuthenticationService;
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

import java.util.UUID;

@RestController
@RequestMapping("budget/board")
@RequiredArgsConstructor
public class BudgetBoardResource {

    final AuthenticationService authenticationService;
    final BudgetAuthenticationService budgetAuthenticationService;
    final BudgetBoardService budgetBoardService;

    @PostMapping
    public BudgetBoardAddResponse add(@RequestBody BudgetBoardAddRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        var id = budgetBoardService.addBoard(request.getName(), userId());
        return new BudgetBoardAddResponse(id);
    }

    @PutMapping
    public void update(@RequestBody BudgetBoardUpdateRequest request) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), request.getBoardId(), BudgetBoardPermission.ADMIN);
        budgetBoardService.updateBoard(request.getBoardId(), request.getName());
    }

    @GetMapping("/{boardId}")
    public BudgetBoard getById(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.VIEW);
        return budgetBoardService.getBudgetBoard(boardId);
    }

    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_VIEW);
        budgetAuthenticationService.checkPermission(userId(), boardId, BudgetBoardPermission.ADMIN);
        budgetBoardService.deleteBoard(boardId);
    }

    UUID userId() {
        return authenticationService.identity().getUserId();
    }
}
