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

        var id = budgetBoardAccountRepository.addEntity(boardId, label, icon, enabled, etag);
        log.info("Added Account {} on board {}: {}", id, boardId, label);
        return id;
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
        log.info("Updated Account {} on board {}: {}", accountId, boardId, label);
    }

    public List<BudgetBoardAccount> getBoardAccounts(UUID boardId) {
        return budgetBoardAccountRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardAccount).toList();
    }

    public void deleteBoardAccount(UUID accountId, UUID boardId) {
        budgetBoardAccountRepository.deleteEntityById(accountId);
        log.info("Deleted Account {} on board {}", accountId, boardId);
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
