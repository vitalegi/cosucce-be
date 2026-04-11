package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import it.vitalegi.cosucce.budget.repository.BudgetBoardAccountRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardAccountRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardAccountService {
    final BudgetBoardAccountRepository budgetBoardAccountRepository;

    public UUID addBoardAccount(UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (label == null) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (icon == null) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (etag == null) {
            throw new IllegalArgumentException("ETag is missing");
        }

        return budgetBoardAccountRepository.addEntity(boardId, label, icon, enabled, etag);
    }

    public void updateBoardAccount(UUID accountId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (label == null) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (icon == null) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (etag == null) {
            throw new IllegalArgumentException("ETag is missing");
        }
        budgetBoardAccountRepository.updateEntity(accountId, boardId, label, icon, enabled, etag);
    }

    public List<BudgetBoardAccount> getBoardAccounts(UUID boardId) {
        return budgetBoardAccountRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardAccount).toList();
    }

    public void deleteBoardAccount(UUID accountId) {
        budgetBoardAccountRepository.deleteEntityById(accountId);
    }

    protected BudgetBoardAccount mapBudgetBoardAccount(BudgetBoardAccountRecord entity) {
        var out = new BudgetBoardAccount();
        out.setAccountId(entity.getAccountId());
        out.setBoardId(entity.getBoardId());
        out.setLabel(entity.getLabel());
        out.setIcon(entity.getIcon());
        out.setEnabled(entity.getEnabled());
        out.setEtag(entity.getEtag());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
