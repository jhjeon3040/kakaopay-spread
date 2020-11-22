package com.kakaopay.demo.spread.controller;


import com.kakaopay.demo.spread.entity.SpreadEntity;
import com.kakaopay.demo.spread.repository.SpreadRepository;
import com.kakaopay.demo.spread.vo.ReceiveDetail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_ROOM_ID;
import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_USER_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReceiveControllerTest extends ControllerTest {

    @MockBean
    private SpreadRepository spreadRepository;

    private ResultActions call(String token, String user, String room) throws Exception{
        return mvc.perform(put(String.format("/v1/spread/%s", token))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_USER_ID, user)
                .header(X_ROOM_ID, room));
    }

    private SpreadEntity stub(){
        List<ReceiveDetail> list = IntStream.range(1, 4)
                .mapToObj(i -> ReceiveDetail.builder()
                        .amount(10 * i)
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
    public void 받기_요청() throws Exception {
        String user = "testUser";
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), user, ROOM_ID)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(10))
                .andExpect(jsonPath("$.userId").value(user));
    }

    @Test
    public void 받기_잘못된_토큰_요청() throws Exception {
        SpreadEntity entity = stub();
        when(spreadRepository.findById(entity.getToken())).thenReturn(Optional.of(entity));
        call("ABD", USER_ID, ROOM_ID)
                .andExpect(status().isNotFound());
    }

    @Test
    public void 받기_중복_요청() throws Exception {
        String user = "user";
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));

        // 첫번째 받기 요청
        call(entity.getToken(), user, ROOM_ID)
                .andExpect(status().isCreated());
        // 두번째 받기 요청
        call(entity.getToken(), user, ROOM_ID)
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 받기_자신이_뿌린_요청() throws Exception {
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), USER_ID, ROOM_ID)
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void 받기_동일하지_않은_대화방_요청() throws Exception {
        String room = "testRoom";
        SpreadEntity entity = stub();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), USER_ID, room)
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void 받기_10분_후_요청() throws Exception {
        List<ReceiveDetail> list = IntStream.range(1, 4)
                .mapToObj(i -> ReceiveDetail.builder()
                        .amount(10 * i)
                        .build())
                .collect(Collectors.toList());
        SpreadEntity entity = SpreadEntity.builder()
                .amount(123)
                .createTime(LocalDateTime.now().minusMinutes(11))
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .token("ABC")
                .receiveInformation(list)
                .build();
        when(spreadRepository.findById(anyString())).thenReturn(Optional.of(entity));
        call(entity.getToken(), "user", ROOM_ID)
                .andExpect(status().isRequestTimeout())
                .andDo(print());
    }

}
