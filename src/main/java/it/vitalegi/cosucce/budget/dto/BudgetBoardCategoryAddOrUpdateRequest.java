package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetBoardCategoryAddOrUpdateRequest {
    String label;
    String icon;
    boolean enabled;
    String etag;
}
