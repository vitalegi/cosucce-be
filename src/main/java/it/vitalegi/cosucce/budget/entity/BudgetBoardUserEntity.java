package it.vitalegi.cosucce.budget.entity;

import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.security.entity.UserDataEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "BudgetBoardUser")
@Table(name = "budget_board_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBoardUserEntity {
    @EmbeddedId
    private BudgetBoardUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardId")
    @JoinColumn(name = "board_id")
    private BudgetBoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserDataEntity user;
    @Enumerated(EnumType.STRING)
    private BudgetBoardRole role;
    private Instant creationDate;
    private Instant lastUpdate;
}
