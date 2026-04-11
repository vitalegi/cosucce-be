package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
import it.vitalegi.cosucce.budget.repository.BudgetBoardCategoryRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardCategoryRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetBoardCategoryService {
    final BudgetBoardCategoryRepository budgetBoardCategoryRepository;

    public UUID addBoardCategory(UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (label == null) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (icon == null) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (etag == null) {
            throw new IllegalArgumentException("ETag is missing");
        }

        return budgetBoardCategoryRepository.addEntity(boardId, label, icon, enabled, etag);
    }

    public void updateBoardCategory(UUID categoryId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (label == null) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (icon == null) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (etag == null) {
            throw new IllegalArgumentException("ETag is missing");
        }
        budgetBoardCategoryRepository.updateEntity(categoryId, boardId, label, icon, enabled, etag);
    }

    public List<BudgetBoardCategory> getBoardCategories(UUID boardId) {
        return budgetBoardCategoryRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardCategory).toList();
    }

    public void deleteBoardCategory(UUID categoryId) {
        budgetBoardCategoryRepository.deleteEntityById(categoryId);
    }

    protected BudgetBoardCategory mapBudgetBoardCategory(BudgetBoardCategoryRecord entity) {
        var out = new BudgetBoardCategory();
        out.setCategoryId(entity.getCategoryId());
        out.setBoardId(entity.getBoardId());
        out.setLabel(entity.getLabel());
        out.setIcon(entity.getIcon());
        out.setEnabled(entity.getEnabled());
        out.setEtag(entity.getEtag());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
