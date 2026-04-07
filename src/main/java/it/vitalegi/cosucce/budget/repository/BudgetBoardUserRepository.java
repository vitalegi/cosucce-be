package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BudgetBoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BudgetBoardUserId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetBoardUserRepository extends JpaRepository<BudgetBoardUserEntity, BudgetBoardUserId> {

    @EntityGraph(attributePaths = {"board", "user"})
    @Query("SELECT bbu FROM BudgetBoardUser bbu WHERE bbu.board.boardId = :boardId")
    List<BudgetBoardUserEntity> findAllByBoardId(@Param("boardId") UUID boardId);
}
