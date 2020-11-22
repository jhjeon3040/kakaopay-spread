package com.kakaopay.demo.spread.controller;


import com.kakaopay.demo.spread.vo.SpreadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_ROOM_ID;
import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_USER_ID;
import static org.hamcrest.Matchers.hasLength;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpreadControllerTest extends ControllerTest {

    @Test
    public void 뿌리기_컨트롤러_요청() throws Exception {
        SpreadRequest body = new SpreadRequest(195734, 3);

        mvc.perform(post("/v1/spread")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_USER_ID, "userA")
                .header(X_ROOM_ID, "roomA")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", hasLength(3)))
                .andDo(print());
    }

    @Test
    public void 뿌리기_인원수보다_적은_금액_요청() throws Exception {
        SpreadRequest body = new SpreadRequest(2, 3);

        mvc.perform(post("/v1/spread")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_USER_ID, "userA")
                .header(X_ROOM_ID, "roomA")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void 뿌리기_0명_요청() throws Exception {
        SpreadRequest body = new SpreadRequest(2, 0);
        mvc.perform(post("/v1/spread")
                .header(X_USER_ID, "userA")
                .header(X_ROOM_ID, "roomA")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }
}
