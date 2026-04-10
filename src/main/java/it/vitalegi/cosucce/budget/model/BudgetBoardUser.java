package it.vitalegi.cosucce.budget.model;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BudgetBoardUser {
    private UUID boardId;
    private UUID userId;
    private BudgetBoardRole role;
    private Instant creationDate;
    private Instant lastUpdate;
}
