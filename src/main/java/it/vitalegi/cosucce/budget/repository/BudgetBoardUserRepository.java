package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BudgetBoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BudgetBoardUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetBoardUserRepository extends JpaRepository<BudgetBoardUserEntity, BudgetBoardUserId> {

}
