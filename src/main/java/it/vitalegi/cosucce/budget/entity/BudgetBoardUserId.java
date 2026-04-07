package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BudgetBoardUserId implements Serializable {
    @Column(name = "board_id")
    UUID boardId;
    @Column(name = "user_id")
    UUID userId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BudgetBoardUserId that = (BudgetBoardUserId) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, userId);
    }
}
