package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetBoardAccount {
    private UUID accountId;
    private UUID boardId;
    private String label;
    private String icon;
    private boolean enabled;
    private String etag;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
}
