package com.kakaopay.demo.spread.controller;


import com.kakaopay.demo.spread.entity.SpreadEntity;
import com.kakaopay.demo.spread.repository.SpreadRepository;
import com.kakaopay.demo.spread.vo.ReceiveDetail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_USER_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InquiryControllerTest extends ControllerTest {

    @MockBean
    SpreadRepository spreadRepository;

    private ResultActions call(String token, String user) throws Exception{
        return mvc.perform(get(String.format("/v1/spread/%s", token))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_USER_ID, user));
    }

    private SpreadEntity stub() {
        List<ReceiveDetail> list = IntStream.range(1, 4)
                .mapToObj(i -> ReceiveDetail.builder()
                        .amount(10 * i)
                        .userId(String.format("user%d", i))
                        .build())
                .collect(Collectors.toList());

        return SpreadEntity.builder()
                .amount(123)
                .createTime(LocalDateTime.now())
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .token("ABC")
                .receiveInformation(list)
                .build();
    }

    @Test
    public void 조회_요청() throws Exception {
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), USER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiveInformation", hasSize(3)))
                .andExpect(jsonPath("$.receiveInformation[0].userId", is("user1")))
                .andDo(print());
    }

    @Test
    public void 조회_잘못된_토큰_요청() throws Exception {
        SpreadEntity entity = stub();
        when(spreadRepository.findById("ABC")).thenReturn(Optional.of(entity));
        call("ABD", USER_ID)
                .andExpect(status().isNotFound());
    }

    @Test
    public void 조회_다른사람의_뿌리기_요청() throws Exception {
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), "testUser")
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 조회_7일이후_요청() throws Exception {
        SpreadEntity entity = SpreadEntity.builder()
                .amount(123)
                .createTime(LocalDateTime.now().minusDays(7))
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .token("ABC")
                .receiveInformation(new ArrayList<>())
                .build();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), entity.getUserId())
                .andExpect(status().isRequestTimeout());
    }
}
