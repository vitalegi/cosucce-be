package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "BudgetBoard")
@Table(name = "budget_board")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID boardId;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetBoardUserEntity> boardUsers;
    private String name;
    private Instant creationDate;
    private Instant lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BudgetBoardEntity that = (BudgetBoardEntity) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, name);
    }
}
