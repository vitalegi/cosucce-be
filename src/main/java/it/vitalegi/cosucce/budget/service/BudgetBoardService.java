package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.entity.BudgetBoardEntity;
import it.vitalegi.cosucce.budget.entity.BudgetBoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BudgetBoardUserId;
import it.vitalegi.cosucce.budget.model.BudgetBoard;
import it.vitalegi.cosucce.budget.model.BudgetBoardUser;
import it.vitalegi.cosucce.budget.repository.BudgetBoardRepository;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import it.vitalegi.cosucce.security.entity.UserDataEntity;
import it.vitalegi.cosucce.security.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardService {
    final UserDataRepository userDataRepository;
    final BudgetBoardRepository budgetBoardRepository;
    final BudgetBoardUserRepository budgetBoardUserRepository;

    @Transactional
    public UUID addBoard(String name, UUID userId) {
        var board = addBoardEntity(name);
        addBudgetBoardUserEntity(board, getUserDataEntity(userId), BudgetBoardRole.OWNER);
        return board.getBoardId();
    }

    @Transactional
    public void updateBoard(UUID boardId, String name) {
        var entity = budgetBoardRepository.findById(boardId).orElseThrow();
        entity.setName(name);
        entity.setLastUpdate(Instant.now());
        budgetBoardRepository.save(entity);
    }

    public BudgetBoard getBudgetBoard(UUID boardId) {
        return mapBudgetBoard(budgetBoardRepository.findWithUsersByBoardId(boardId).orElseThrow());
    }

    public void deleteBoard(UUID boardId) {
        budgetBoardRepository.deleteById(boardId);
    }

    public void addBudgetBoardUser(UUID boardId, UUID userId, BudgetBoardRole role) {
        var board = budgetBoardRepository.findById(boardId).orElseThrow();
        var user = userDataRepository.findById(userId).orElseThrow();
        addBudgetBoardUserEntity(board, user, role);
    }

    public List<BudgetBoardUser> getBudgetBoardUsers(UUID boardId) {
        var entities = budgetBoardUserRepository.findAllByBoardId(boardId);
        return entities.stream().map(this::mapBudgetBoard).toList();
    }

    protected BudgetBoardEntity addBoardEntity(String name) {
        var entity = new BudgetBoardEntity();
        entity.setName(name);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdate(Instant.now());
        return budgetBoardRepository.save(entity);
    }

    protected BudgetBoardUserEntity addBudgetBoardUserEntity(BudgetBoardEntity boardEntity, UserDataEntity userDataEntity, BudgetBoardRole role) {
        var entity = new BudgetBoardUserEntity();
        entity.setBoard(boardEntity);
        entity.setUser(userDataEntity);
        entity.setId(new BudgetBoardUserId(boardEntity.getBoardId(), userDataEntity.getUserId()));
        entity.setRole(role);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdate(Instant.now());
        return budgetBoardUserRepository.save(entity);
    }

    protected UserDataEntity getUserDataEntity(UUID userId) {
        return userDataRepository.findById(userId).orElseThrow();
    }

    protected BudgetBoard mapBudgetBoard(BudgetBoardEntity entity) {
        var out = new BudgetBoard();
        out.setBoardId(entity.getBoardId());
        out.setName(entity.getName());
        out.setUsers(entity.getBoardUsers().stream().map(this::mapBudgetBoard).toList());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }

    protected BudgetBoardUser mapBudgetBoard(BudgetBoardUserEntity entity) {
        var out = new BudgetBoardUser();
        out.setBoardId(entity.getId().getBoardId());
        out.setUserId(entity.getId().getUserId());
        out.setRole(entity.getRole());
        out.setUsername("TODO");
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
