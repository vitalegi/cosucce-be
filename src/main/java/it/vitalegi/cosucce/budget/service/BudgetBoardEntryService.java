package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.exception.ETagNotMatchedException;
import it.vitalegi.cosucce.budget.model.BudgetBoardEntry;
import it.vitalegi.cosucce.budget.repository.BudgetBoardAccountRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardCategoryRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardEntryRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardEntryRecord;
import it.vitalegi.cosucce.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardEntryService {
    final BudgetBoardEntryRepository budgetBoardEntryRepository;
    final BudgetBoardAccountRepository budgetBoardAccountRepository;
    final BudgetBoardCategoryRepository budgetBoardCategoryRepository;

    public void addBoardEntry(UUID entryId, UUID boardId, LocalDate date, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID userId, String etag) {
        validateParams(entryId, boardId, date, accountId, categoryId, amount, userId);
        validateAccount(boardId, accountId);
        validateCategory(boardId, categoryId);
        if (StringUtil.isNullOrEmpty(etag)) {
            throw new IllegalArgumentException("ETag is missing");
        }

        budgetBoardEntryRepository.addEntity(entryId, boardId, date, accountId, categoryId, description, amount, userId, etag);
        log.info("Added Entry {} on board {}. Etag: {}", entryId, boardId, etag);
    }

    @Transactional
    public void updateBoardEntry(UUID entryId, UUID boardId, LocalDate date, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID userId, String newEtag, String oldEtag) {
        validateParams(entryId, boardId, date, accountId, categoryId, amount, userId);
        if (StringUtil.isNullOrEmpty(newEtag)) {
            throw new IllegalArgumentException("New ETag is missing");
        }
        if (StringUtil.isNullOrEmpty(oldEtag)) {
            throw new IllegalArgumentException("Old ETag is missing");
        }
        var existing = budgetBoardEntryRepository.getEntityById(entryId);
        if (existing == null) {
            throw new IllegalArgumentException("Entry doesn't exist");
        }
        if (!existing.getEtag().equals(oldEtag)) {
            throw new ETagNotMatchedException(existing.getEtag(), oldEtag, entryId, "BudgetEntry");
        }
        if (!existing.getBoardId().equals(boardId)) {
            throw new IllegalArgumentException("Invalid BoardId");
        }
        validateAccount(boardId, accountId);
        validateCategory(boardId, categoryId);
        budgetBoardEntryRepository.updateEntity(entryId, boardId, date, accountId, categoryId, description, amount, userId, newEtag);
        log.info("Updated Entry {} on board {}. ETag: {} => {}", entryId, boardId, oldEtag, newEtag);
    }

    public List<BudgetBoardEntry> getBoardEntries(UUID boardId) {
        return budgetBoardEntryRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardEntry).toList();
    }

    public void deleteBoardEntry(UUID entryId, UUID boardId) {
        budgetBoardEntryRepository.deleteEntityById(entryId);
        log.info("Deleted Entry {} on board {}", entryId, boardId);
    }

    protected BudgetBoardEntry mapBudgetBoardEntry(BudgetBoardEntryRecord entity) {
        var out = new BudgetBoardEntry();
        out.setEntryId(entity.getEntryId());
        out.setBoardId(entity.getBoardId());
        out.setDate(entity.getDate());
        out.setAccountId(entity.getAccountId());
        out.setCategoryId(entity.getCategoryId());
        out.setDescription(entity.getDescription());
        out.setAmount(entity.getAmount());
        out.setEtag(entity.getEtag());
        out.setLastUpdatedBy(entity.getLastUpdatedBy());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }

    protected void validateParams(UUID entryId, UUID boardId, LocalDate date, UUID accountId, UUID categoryId, BigDecimal amount, UUID userId) {
        if (entryId == null) {
            throw new IllegalArgumentException("EntryId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date is missing");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId is missing");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId is missing");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount is missing");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId is missing");
        }
    }

    protected void validateAccount(UUID boardId, UUID accountId) {
        var account = budgetBoardAccountRepository.getEntityById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Unknown Account");
        }
        if (!account.getBoardId().equals(boardId)) {
            throw new IllegalArgumentException("Invalid Account");
        }
    }

    protected void validateCategory(UUID boardId, UUID categoryId) {
        var category = budgetBoardCategoryRepository.getEntityById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Unknown Category");
        }
        if (!category.getBoardId().equals(boardId)) {
            throw new IllegalArgumentException("Invalid Category");
        }
    }

}
