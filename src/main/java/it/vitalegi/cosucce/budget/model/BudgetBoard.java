package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class BudgetBoard {
    private UUID boardId;
    private String name;
    private List<BudgetBoardUser> users;
    private Instant creationDate;
    private Instant lastUpdate;
}
