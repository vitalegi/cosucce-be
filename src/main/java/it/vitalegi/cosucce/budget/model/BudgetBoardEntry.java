package it.vitalegi.cosucce.budget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetBoardEntry {
    private UUID entryId;
    private UUID boardId;
    private LocalDate date;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private String etag;
    private UUID lastUpdatedBy;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
}
