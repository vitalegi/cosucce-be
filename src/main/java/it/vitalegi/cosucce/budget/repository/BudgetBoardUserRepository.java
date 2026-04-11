package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardUserRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;

@Repository
@RequiredArgsConstructor
public class BudgetBoardUserRepository {
    final DSLContext dsl;

    public void add(UUID boardId, UUID userId, BudgetBoardRole role) {
        var record = dsl.newRecord(BUDGET_BOARD_USER);
        record.set(BUDGET_BOARD_USER.BOARD_ID, boardId);
        record.set(BUDGET_BOARD_USER.USER_ID, userId);
        record.set(BUDGET_BOARD_USER.BUDGET_BOARD_ROLE, role);
        record.set(BUDGET_BOARD_USER.CREATION_DATE, LocalDateTime.now());
        record.set(BUDGET_BOARD_USER.LAST_UPDATE, LocalDateTime.now());
        record.store();
    }

    public Result<BudgetBoardUserRecord> getAllByBoardId(UUID boardId) {
        return dsl.selectFrom(BUDGET_BOARD_USER.where(BUDGET_BOARD_USER.BOARD_ID.eq(boardId))).fetch();
    }
}
