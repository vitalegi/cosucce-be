package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.db.tables.records.BudgetBoardAccountRecord;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_ACCOUNT;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_CATEGORY;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;

@Repository
@RequiredArgsConstructor
public class BudgetBoardAccountRepository {
    final DSLContext dsl;

    public void addEntity(UUID accountId, UUID boardId, String label, String icon, String color, boolean enabled, String etag) {
        var record = dsl.newRecord(BUDGET_BOARD_ACCOUNT);
        record.set(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID, accountId);
        record.set(BUDGET_BOARD_ACCOUNT.BOARD_ID, boardId);
        record.set(BUDGET_BOARD_ACCOUNT.LABEL, label);
        record.set(BUDGET_BOARD_ACCOUNT.ICON, icon);
        record.set(BUDGET_BOARD_ACCOUNT.COLOR, color);
        record.set(BUDGET_BOARD_ACCOUNT.ENABLED, enabled);
        record.set(BUDGET_BOARD_ACCOUNT.ETAG, etag);
        record.set(BUDGET_BOARD_ACCOUNT.CREATION_DATE, LocalDateTime.now());
        record.set(BUDGET_BOARD_ACCOUNT.LAST_UPDATE, LocalDateTime.now());
        record.store();
    }

    public void updateEntity(UUID categoryId, UUID boardId, String label, String icon, String color, boolean enabled, String etag) {
        dsl.update(BUDGET_BOARD_ACCOUNT) //
                .set(BUDGET_BOARD_ACCOUNT.BOARD_ID, boardId) //
                .set(BUDGET_BOARD_ACCOUNT.LABEL, label) //
                .set(BUDGET_BOARD_ACCOUNT.ICON, icon) //
                .set(BUDGET_BOARD_ACCOUNT.COLOR, color) //
                .set(BUDGET_BOARD_ACCOUNT.ENABLED, enabled) //
                .set(BUDGET_BOARD_ACCOUNT.ETAG, etag) //
                .set(BUDGET_BOARD_ACCOUNT.LAST_UPDATE, LocalDateTime.now()) //
                .where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(categoryId)) //
                .execute();
    }

    public Result<BudgetBoardAccountRecord> getEntitiesByBoardId(UUID boardId) {
        return dsl.selectFrom(BUDGET_BOARD_ACCOUNT.where(BUDGET_BOARD_ACCOUNT.BOARD_ID.eq(boardId))).fetch();
    }

    public Result<BudgetBoardAccountRecord> getVisibleEntities(UUID userId) {
        return dsl.select(BUDGET_BOARD_ACCOUNT.fields()) //
                .from(BUDGET_BOARD_ACCOUNT) //
                .join(BUDGET_BOARD_USER) //
                .on(BUDGET_BOARD_USER.BOARD_ID.eq(BUDGET_BOARD_ACCOUNT.BOARD_ID)) //
                .where(BUDGET_BOARD_USER.USER_ID.eq(userId)) //
                .fetchInto(BUDGET_BOARD_ACCOUNT);
    }

    public void deleteEntityById(UUID accountId) {
        dsl.deleteFrom(BUDGET_BOARD_ACCOUNT) //
                .where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId)) //
                .execute();
    }

    public BudgetBoardAccountRecord getEntityById(UUID accountId) {
        return dsl //
                .selectFrom(BUDGET_BOARD_ACCOUNT) //
                .where(BUDGET_BOARD_ACCOUNT.ACCOUNT_ID.eq(accountId)) //
                .fetchOne();
    }
}
