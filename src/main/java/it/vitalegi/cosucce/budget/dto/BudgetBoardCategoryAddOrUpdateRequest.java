package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetBoardCategoryAddOrUpdateRequest {
    UUID categoryId;
    String label;
    String icon;
    String color;
    boolean enabled;
    String etag;
}
