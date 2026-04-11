package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.model.BudgetBoardCategory;
import it.vitalegi.cosucce.budget.repository.BudgetBoardCategoryRepository;
import it.vitalegi.cosucce.db.tables.records.BudgetBoardCategoryRecord;
import it.vitalegi.cosucce.util.StringUtil;
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

    public void addBoardCategory(UUID categoryId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (StringUtil.isNullOrEmpty(label)) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (StringUtil.isNullOrEmpty(icon)) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (StringUtil.isNullOrEmpty(etag)) {
            throw new IllegalArgumentException("ETag is missing");
        }
        budgetBoardCategoryRepository.addEntity(categoryId, boardId, label, icon, enabled, etag);
        log.info("Added Category {} on board {}: {}", categoryId, boardId, label);
    }

    public void updateBoardCategory(UUID categoryId, UUID boardId, String label, String icon, boolean enabled, String etag) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId is missing");
        }
        if (boardId == null) {
            throw new IllegalArgumentException("BoardId is missing");
        }
        if (StringUtil.isNullOrEmpty(label)) {
            throw new IllegalArgumentException("Label is missing");
        }
        if (StringUtil.isNullOrEmpty(icon)) {
            throw new IllegalArgumentException("Icon is missing");
        }
        if (StringUtil.isNullOrEmpty(etag)) {
            throw new IllegalArgumentException("ETag is missing");
        }
        budgetBoardCategoryRepository.updateEntity(categoryId, boardId, label, icon, enabled, etag);
        log.info("Updated Category {} on board {}: {}", categoryId, boardId, label);
    }

    public List<BudgetBoardCategory> getBoardCategories(UUID boardId) {
        return budgetBoardCategoryRepository.getEntitiesByBoardId(boardId).stream().map(this::mapBudgetBoardCategory).toList();
    }

    public void deleteBoardCategory(UUID categoryId, UUID boardId) {
        budgetBoardCategoryRepository.deleteEntityById(categoryId);
        log.info("Deleted Category {} on board {}", categoryId, boardId);
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
