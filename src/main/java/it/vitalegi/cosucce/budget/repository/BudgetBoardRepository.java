package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.db.tables.records.BudgetBoardRecord;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardUserRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;

@Repository
@RequiredArgsConstructor
public class BudgetBoardRepository {
    final DSLContext dsl;


    public UUID add(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        var id = UUID.randomUUID();
        var record = dsl.newRecord(BUDGET_BOARD);
        record.set(BUDGET_BOARD.BOARD_ID, id);
        record.set(BUDGET_BOARD.NAME, name);
        record.set(BUDGET_BOARD.CREATION_DATE, Instant.now());
        record.set(BUDGET_BOARD.LAST_UPDATE, Instant.now());
        record.store();
        return record.getBoardId();
    }

    public void update(UUID boardId, String name) {
        dsl.update(BUDGET_BOARD) //
                .set(BUDGET_BOARD.NAME, name) //
                .set(BUDGET_BOARD.LAST_UPDATE, Instant.now()) //
                .where(BUDGET_BOARD.BOARD_ID.eq(boardId)) //
                .execute();
    }

    public Map.Entry<BudgetBoardRecord, Result<BudgetBoardUserRecord>> getById(UUID boardId) {
        var results = dsl //
                .select(BUDGET_BOARD.fields()) //
                .select(BUDGET_BOARD_USER.fields()) //
                .from(BUDGET_BOARD) //
                .leftJoin(BUDGET_BOARD_USER) //
                .on(BUDGET_BOARD_USER.BOARD_ID.eq(BUDGET_BOARD.BOARD_ID)) //
                .where(BUDGET_BOARD.BOARD_ID.eq(boardId)) //
                .fetch();
        var map = results.intoGroups(BUDGET_BOARD, BUDGET_BOARD_USER);

        if (map.isEmpty()) {
            throw new IllegalArgumentException("Board " + boardId + " not found");
        }
        return map.entrySet().iterator().next();
    }

    public void deleteById(UUID boardId) {
        dsl.deleteFrom(BUDGET_BOARD) //
                .where(BUDGET_BOARD.BOARD_ID.eq(boardId)) //
                .execute();
    }
}
