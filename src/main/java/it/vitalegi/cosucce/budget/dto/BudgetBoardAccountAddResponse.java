package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBoardAccountAddResponse {
    UUID accountId;
}
