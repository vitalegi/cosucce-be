package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.exception.ETagNotMatchedException;
import it.vitalegi.cosucce.budget.model.BudgetBoardAccount;
import it.vitalegi.cosucce.budget.repository.BudgetBoardAccountRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardAccountRecord;
import it.vitalegi.cosucce.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardAccountService {
    final BudgetBoardAccountRepository budgetBoardAccountRepository;

    public void addBoardAccount(UUID accountId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (StringUtil.isNullOrEmpty(label)) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (StringUtil.isNullOrEmpty(icon)) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (StringUtil.isNullOrEmpty(etag)) {
            throw new IllegalArgumentException("ETag is missing");
        }

        budgetBoardAccountRepository.addEntity(accountId, boardId, label, icon, enabled, etag);
        log.info("Added Account {} on board {}. Etag: {}", accountId, boardId, etag);
    }

    @Transactional
    public void updateBoardAccount(UUID accountId, UUID boardId, String label, String icon, boolean enabled, String newEtag, String oldEtag) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (StringUtil.isNullOrEmpty(label)) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (StringUtil.isNullOrEmpty(icon)) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (StringUtil.isNullOrEmpty(newEtag)) {
            throw new IllegalArgumentException("New ETag is missing");
        }
        if (StringUtil.isNullOrEmpty(oldEtag)) {
            throw new IllegalArgumentException("Old ETag is missing");
        }
        var existing = budgetBoardAccountRepository.getEntityById(accountId);
        if (!existing.getEtag().equals(oldEtag)) {
            throw new ETagNotMatchedException(existing.getEtag(), oldEtag, accountId, "BudgetAccount");
        }
        budgetBoardAccountRepository.updateEntity(accountId, boardId, label, icon, enabled, newEtag);
        log.info("Updated Account {} on board {}. ETag: {} => {}", accountId, boardId, oldEtag, newEtag);
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
