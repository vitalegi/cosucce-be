package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.db.tables.records.BudgetBoardCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_CATEGORY;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;

@Repository
@RequiredArgsConstructor
public class BudgetBoardCategoryRepository {
    final DSLContext dsl;

    public void addEntity(UUID categoryId, UUID boardId, String label, String icon, String color, boolean enabled, String etag) {
        var record = dsl.newRecord(BUDGET_BOARD_CATEGORY);
        record.set(BUDGET_BOARD_CATEGORY.CATEGORY_ID, categoryId);
        record.set(BUDGET_BOARD_CATEGORY.BOARD_ID, boardId);
        record.set(BUDGET_BOARD_CATEGORY.LABEL, label);
        record.set(BUDGET_BOARD_CATEGORY.ICON, icon);
        record.set(BUDGET_BOARD_CATEGORY.COLOR, color);
        record.set(BUDGET_BOARD_CATEGORY.ENABLED, enabled);
        record.set(BUDGET_BOARD_CATEGORY.ETAG, etag);
        record.set(BUDGET_BOARD_CATEGORY.CREATION_DATE, LocalDateTime.now());
        record.set(BUDGET_BOARD_CATEGORY.LAST_UPDATE, LocalDateTime.now());
        record.store();
    }

    public void updateEntity(UUID categoryId, UUID boardId, String label, String icon, String color, boolean enabled, String etag) {
        dsl.update(BUDGET_BOARD_CATEGORY) //
                .set(BUDGET_BOARD_CATEGORY.BOARD_ID, boardId) //
                .set(BUDGET_BOARD_CATEGORY.LABEL, label) //
                .set(BUDGET_BOARD_CATEGORY.ICON, icon) //
                .set(BUDGET_BOARD_CATEGORY.COLOR, color) //
                .set(BUDGET_BOARD_CATEGORY.ENABLED, enabled) //
                .set(BUDGET_BOARD_CATEGORY.ETAG, etag) //
                .set(BUDGET_BOARD_CATEGORY.LAST_UPDATE, LocalDateTime.now()) //
                .where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)) //
                .execute();
    }

    public Result<BudgetBoardCategoryRecord> getEntitiesByBoardId(UUID boardId) {
        return dsl.selectFrom(BUDGET_BOARD_CATEGORY.where(BUDGET_BOARD_CATEGORY.BOARD_ID.eq(boardId))).fetch();
    }

    public Result<BudgetBoardCategoryRecord> getVisibleEntities(UUID userId) {
        return dsl.select(BUDGET_BOARD_CATEGORY.fields()) //
                .from(BUDGET_BOARD_CATEGORY) //
                .join(BUDGET_BOARD_USER) //
                .on(BUDGET_BOARD_USER.BOARD_ID.eq(BUDGET_BOARD_CATEGORY.BOARD_ID)) //
                .where(BUDGET_BOARD_USER.USER_ID.eq(userId)) //
                .fetchInto(BUDGET_BOARD_CATEGORY);
    }

    public void deleteEntityById(UUID categoryId) {
        dsl.deleteFrom(BUDGET_BOARD_CATEGORY) //
                .where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)) //
                .execute();
    }

    public BudgetBoardCategoryRecord getEntityById(UUID categoryId) {
        return dsl //
                .selectFrom(BUDGET_BOARD_CATEGORY) //
                .where(BUDGET_BOARD_CATEGORY.CATEGORY_ID.eq(categoryId)) //
                .fetchOne();
    }
}
