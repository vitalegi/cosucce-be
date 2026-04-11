package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardUser;
import it.vitalegi.cosucce.budget.repository.BudgetBoardCategoryRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardRecord;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardUserRecord;
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
public class BudgetBoardService {
    final BudgetBoardRepository budgetBoardRepository;
    final BudgetBoardUserRepository budgetBoardUserRepository;
    final BudgetBoardCategoryRepository budgetBoardCategoryRepository;

    @Transactional
    public void addBoard(UUID boardId, String name, String etag, UUID userId) {
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (StringUtil.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name is missing");
        }
        if (StringUtil.isNullOrEmpty(etag)) {
            throw new IllegalArgumentException("ETag is missing");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId is missing");
        }
        budgetBoardRepository.addEntity(boardId, name, etag);
        budgetBoardUserRepository.addEntity(boardId, userId, BudgetBoardRole.OWNER);
        log.info("Added board {}: {}", boardId, name);
    }

    public void updateBoard(UUID boardId, String name) {
        budgetBoardRepository.updateEntity(boardId, name);
        log.info("Updated board {}: {}", boardId, name);
    }

    public BudgetBoard getBoard(UUID boardId) {
        var record = budgetBoardRepository.getEntityById(boardId);
        return mapBudgetBoard(record.getKey(), record.getValue());
    }

    public List<BudgetBoard> getBoardsVisibleByUser(UUID userId) {
        return budgetBoardRepository.getEntitiesByUserId(userId) //
                .stream() //
                .map(e -> mapBudgetBoard(e.getKey(), e.getValue())) //
                .toList();
    }

    public void deleteBoard(UUID boardId) {
        budgetBoardRepository.deleteEntityById(boardId);
        log.info("Deleted board {}", boardId);
    }

    public List<BudgetBoardUser> getBoardUsers(UUID boardId) {
        return budgetBoardUserRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardUser).toList();
    }

    protected BudgetBoard mapBudgetBoard(BudgetBoardRecord budgetBoardRecord, List<BudgetBoardUserRecord> budgetBoardUserRecords) {
        var out = new BudgetBoard();
        out.setBoardId(budgetBoardRecord.getBoardId());
        out.setName(budgetBoardRecord.getName());
        out.setCreationDate(budgetBoardRecord.getCreationDate());
        out.setLastUpdate(budgetBoardRecord.getLastUpdate());
        if (budgetBoardUserRecords != null) {
            out.setUsers(budgetBoardUserRecords.stream().map(this::mapBudgetBoardUser).toList());
        }
        return out;
    }

    protected BudgetBoardUser mapBudgetBoardUser(BudgetBoardUserRecord entity) {
        var out = new BudgetBoardUser();
        out.setBoardId(entity.getBoardId());
        out.setUserId(entity.getUserId());
        out.setRole(entity.getBudgetBoardRole());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
