package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.db.tables.records.BudgetBoardCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_CATEGORY;

@Repository
@RequiredArgsConstructor
public class BudgetBoardCategoryRepository {
    final DSLContext dsl;

    public UUID addEntity(UUID boardId, String label, String icon, boolean enabled, String etag) {
        var id = UUID.randomUUID();
        var record = dsl.newRecord(BUDGET_BOARD_CATEGORY);
        record.set(BUDGET_BOARD_CATEGORY.CATEGORY_ID, id);
        record.set(BUDGET_BOARD_CATEGORY.BOARD_ID, boardId);
        record.set(BUDGET_BOARD_CATEGORY.LABEL, label);
        record.set(BUDGET_BOARD_CATEGORY.ICON, icon);
        record.set(BUDGET_BOARD_CATEGORY.ENABLED, enabled);
        record.set(BUDGET_BOARD_CATEGORY.ETAG, etag);
        record.set(BUDGET_BOARD_CATEGORY.CREATION_DATE, LocalDateTime.now());
        record.set(BUDGET_BOARD_CATEGORY.LAST_UPDATE, LocalDateTime.now());
        record.store();
        return id;
    }

    public void updateEntity(UUID categoryId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        dsl.update(BUDGET_BOARD_CATEGORY) //
                .set(BUDGET_BOARD_CATEGORY.BOARD_ID, boardId) //
                .set(BUDGET_BOARD_CATEGORY.LABEL, label) //
                .set(BUDGET_BOARD_CATEGORY.ICON, icon) //
                .set(BUDGET_BOARD_CATEGORY.ENABLED, enabled) //
                .set(BUDGET_BOARD_CATEGORY.ETAG, etag) //
                .set(BUDGET_BOARD_CATEGORY.LAST_UPDATE, LocalDateTime.now()) //
                .where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)) //
                .execute();
    }

    public Result<BudgetBoardCategoryRecord> getEntitiesByBoardId(UUID boardId) {
        return dsl.selectFrom(BUDGET_BOARD_CATEGORY.where(BUDGET_BOARD_CATEGORY.BOARD_ID.eq(boardId))).fetch();
    }

    public void deleteEntityById(UUID categoryId) {
        dsl.deleteFrom(BUDGET_BOARD_CATEGORY) //
                .where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)) //
                .execute();
    }
}
