package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.MockAuth;
import it.vitalegi.cosucce.UserDataUtil;
import it.vitalegi.cosucce.budget.constant.BudgetBoardRole;
import it.vitalegi.cosucce.budget.repository.BudgetBoardUserRepository;
import it.vitalegi.cosucce.budget.service.BudgetBoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Slf4j
public class AbstractBudgetResourceTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BudgetBoardUserRepository budgetBoardUserRepository;
    @Autowired
    BudgetBoardService budgetBoardService;
    @Autowired
    UserDataUtil userDataUtil;
    @Autowired
    ObjectMapper objectMapper;

    UUID boardOwnerId;
    RequestPostProcessor boardOwner;
    UUID boardMemberId;
    RequestPostProcessor boardMember;
    UUID boardId;

    @BeforeEach
    void init() {
        var subject = UUID.randomUUID().toString();
        boardOwnerId = userDataUtil.user(subject, List.of("MEMBER"));
        boardOwner = MockAuth.member(subject);
        boardId = budgetBoardService.addBoard("SAMPLE", boardOwnerId);

        subject = UUID.randomUUID().toString();
        boardMemberId = userDataUtil.user(subject, List.of("MEMBER"));
        boardMember = MockAuth.member(subject);
        budgetBoardUserRepository.addEntity(boardId, boardMemberId, BudgetBoardRole.MEMBER);
        log.info("BoardId: {}", boardId);
        log.info("OwnerId: {}", boardOwnerId);
        log.info("MemberId: {}", boardMemberId);
    }

    String asString(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    <E> E payload(MvcResult result, Class<E> clazz) {
        try {
            return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
