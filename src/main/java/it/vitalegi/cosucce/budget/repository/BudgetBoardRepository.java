package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BudgetBoardEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetBoardRepository extends JpaRepository<BudgetBoardEntity, UUID> {

    @EntityGraph(attributePaths = {"boardUsers", "boardUsers.user"})
    Optional<BudgetBoardEntity> findWithUsersByBoardId(UUID boardId);
}
