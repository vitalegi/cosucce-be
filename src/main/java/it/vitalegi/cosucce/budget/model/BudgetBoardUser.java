package it.vitalegi.cosucce.budget.model;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetBoardUser {
    private UUID boardId;
    private UUID userId;
    private BudgetBoardRole role;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
}
