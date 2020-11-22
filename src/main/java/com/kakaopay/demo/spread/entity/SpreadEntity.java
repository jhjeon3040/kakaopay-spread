package com.kakaopay.demo.spread.entity;

import com.kakaopay.demo.spread.vo.ReceiveDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
@RedisHash("spread")
public class SpreadEntity implements Serializable {

    @Id
    private String token;

    private long amount;

    private String roomId;

    private String userId;

    private LocalDateTime createTime;

    private List<ReceiveDetail> receiveInformation;

}
