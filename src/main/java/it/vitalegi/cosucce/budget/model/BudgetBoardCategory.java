package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetBoardCategory {
    private UUID categoryId;
    private UUID boardId;
    private String label;
    private String icon;
    private String color;
    private boolean enabled;
    private String etag;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
}
