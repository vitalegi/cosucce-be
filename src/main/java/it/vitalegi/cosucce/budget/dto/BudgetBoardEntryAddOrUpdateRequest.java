package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetBoardEntryAddOrUpdateRequest {
    private UUID entryId;
    private LocalDate date;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private String etag;
}
