package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BudgetBoard {
    private UUID boardId;
    private String name;
    private List<BudgetBoardUser> users;
    private String etag;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
}
