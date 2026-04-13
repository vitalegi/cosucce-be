package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.db.tables.records.BudgetBoardEntryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_ENTRY;
import static it.vitalegi.cosucce.db.Tables.BUDGET_BOARD_USER;

@Repository
@RequiredArgsConstructor
public class BudgetBoardEntryRepository {
    final DSLContext dsl;

    public void addEntity(UUID entryId, UUID boardId, LocalDate date, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID lastUpdatedBy, String etag) {
        var record = dsl.newRecord(BUDGET_BOARD_ENTRY);
        record.set(BUDGET_BOARD_ENTRY.ENTRY_ID, entryId);
        record.set(BUDGET_BOARD_ENTRY.BOARD_ID, boardId);
        record.set(BUDGET_BOARD_ENTRY.DATE, date);
        record.set(BUDGET_BOARD_ENTRY.ACCOUNT_ID, accountId);
        record.set(BUDGET_BOARD_ENTRY.CATEGORY_ID, categoryId);
        record.set(BUDGET_BOARD_ENTRY.DESCRIPTION, description);
        record.set(BUDGET_BOARD_ENTRY.AMOUNT, amount);
        record.set(BUDGET_BOARD_ENTRY.LAST_UPDATED_BY, lastUpdatedBy);
        record.set(BUDGET_BOARD_ENTRY.ETAG, etag);
        record.set(BUDGET_BOARD_ENTRY.CREATION_DATE, LocalDateTime.now());
        record.set(BUDGET_BOARD_ENTRY.LAST_UPDATE, LocalDateTime.now());
        record.store();
    }

    public void updateEntity(UUID entryId, UUID boardId, LocalDate date, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID lastUpdatedBy, String etag) {
        dsl.update(BUDGET_BOARD_ENTRY) //
                .set(BUDGET_BOARD_ENTRY.BOARD_ID, boardId) //
                .set(BUDGET_BOARD_ENTRY.DATE, date) //
                .set(BUDGET_BOARD_ENTRY.ACCOUNT_ID, accountId) //
                .set(BUDGET_BOARD_ENTRY.CATEGORY_ID, categoryId) //
                .set(BUDGET_BOARD_ENTRY.DESCRIPTION, description) //
                .set(BUDGET_BOARD_ENTRY.AMOUNT, amount) //
                .set(BUDGET_BOARD_ENTRY.LAST_UPDATED_BY, lastUpdatedBy) //
                .set(BUDGET_BOARD_ENTRY.ETAG, etag) //
                .set(BUDGET_BOARD_ENTRY.LAST_UPDATE, LocalDateTime.now()) //
                .where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)) //
                .execute();
    }

    public Result<BudgetBoardEntryRecord> getEntitiesByBoardId(UUID boardId) {
        return dsl.selectFrom(BUDGET_BOARD_ENTRY.where(BUDGET_BOARD_ENTRY.BOARD_ID.eq(boardId))).fetch();
    }

    public Result<BudgetBoardEntryRecord> getVisibleEntities(UUID userId) {
        return dsl.select(BUDGET_BOARD_ENTRY.fields()) //
                .from(BUDGET_BOARD_ENTRY) //
                .join(BUDGET_BOARD_USER) //
                .on(BUDGET_BOARD_USER.BOARD_ID.eq(BUDGET_BOARD_ENTRY.BOARD_ID)) //
                .where(BUDGET_BOARD_USER.USER_ID.eq(userId)) //
                .fetchInto(BUDGET_BOARD_ENTRY);
    }

    public void deleteEntityById(UUID entryId) {
        dsl.deleteFrom(BUDGET_BOARD_ENTRY) //
                .where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)) //
                .execute();
    }

    public BudgetBoardEntryRecord getEntityById(UUID entryId) {
        return dsl //
                .selectFrom(BUDGET_BOARD_ENTRY) //
                .where(BUDGET_BOARD_ENTRY.ENTRY_ID.eq(entryId)) //
                .fetchOne();
    }
}
