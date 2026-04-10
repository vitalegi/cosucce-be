package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardUser;
import it.vitalegi.cosucce.budget.repository.BudgetBoardRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardRecord;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardService {
    final DSLContext dsl;
    final BudgetBoardRepository budgetBoardRepository;
    final BudgetBoardUserRepository budgetBoardUserRepository;

    @Transactional
    public UUID addBoard(String name, UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId is null");
        }
        var boardId = budgetBoardRepository.add(name);
        budgetBoardUserRepository.add(boardId, userId, BudgetBoardRole.OWNER);
        return boardId;
    }

    public void updateBoard(UUID boardId, String name) {
        budgetBoardRepository.update(boardId, name);
    }

    public BudgetBoard getBudgetBoard(UUID boardId) {
        var record = budgetBoardRepository.getById(boardId);
        return mapBudgetBoard(record.getKey(), record.getValue());
    }

    public void deleteBoard(UUID boardId) {
        budgetBoardRepository.deleteById(boardId);
    }

    public List<BudgetBoardUser> getBudgetBoardUsers(UUID boardId) {
        return budgetBoardUserRepository.getAllByBoardId(boardId).stream().map(this::mapBudgetBoardUser).toList();
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
