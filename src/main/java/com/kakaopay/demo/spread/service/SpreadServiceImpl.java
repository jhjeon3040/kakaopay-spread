package com.kakaopay.demo.spread.service;

import com.kakaopay.demo.spread.entity.SpreadEntity;
import com.kakaopay.demo.spread.except.SpreadExceptEnum;
import com.kakaopay.demo.spread.except.SpreadException;
import com.kakaopay.demo.spread.repository.SpreadRepository;
import com.kakaopay.demo.spread.vo.InquiryResult;
import com.kakaopay.demo.spread.vo.ReceiveDetail;
import com.kakaopay.demo.spread.vo.SpreadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Service
public class SpreadServiceImpl implements SpreadService {

    @Autowired
    private TokenGenService tokenGenService;

    @Autowired
    private SpreadRepository spreadRepository;

//    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SpreadResult spread(String userId, String roomId, long amount, int number) {
        // 1. 뿌릴 금액 분배
        long[] n = partition(amount, number);
        // 2. 뿌리기 토큰 생성
        final String token = tokenGenService.generate();

        List<ReceiveDetail> list = LongStream.of(n)
                .boxed()
                .map(l -> ReceiveDetail
                        .builder()
                        .amount(l)
                        .build())
                .collect(Collectors.toList());

        SpreadEntity entity = SpreadEntity.builder()
                .userId(userId)
                .roomId(roomId)
                .createTime(LocalDateTime.now())
                .token(token)
                .amount(amount)
                .receiveInformation(list)
                .build();

        Optional<SpreadEntity> optional = spreadRepository.findById(token);
        if(optional.isPresent())
            throw new SpreadException(SpreadExceptEnum.INVALID_TOKEN);
        spreadRepository.save(entity);
        log.debug("spread entity stored : {}", entity);
        return new SpreadResult(token);
    }

//    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReceiveDetail receive(String userId, String roomId, String token) {
        Optional<SpreadEntity> optional = spreadRepository.findById(token);
        // 1. 존재하지 않는 토큰
        if(!optional.isPresent())
            throw new SpreadException(SpreadExceptEnum.INVALID_TOKEN);
        SpreadEntity entity = optional.get();

        // 2. 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 유효
        if(!entity.getRoomId().equals(roomId))
            throw new SpreadException(SpreadExceptEnum.RECEIVE_ROOM_NOT_EQUAL);

        // 3. 자신이 뿌리기한 건은 자신이 받을 수 없습니다
        if(entity.getUserId().equals(userId))
            throw new SpreadException(SpreadExceptEnum.RECEIVE_SELF);

        // 4. 뿌리기는 한번만 받기 가능
        if(entity.getReceiveInformation().stream().anyMatch(info -> userId.equals(info.getUserId())))
            throw new SpreadException(SpreadExceptEnum.RECEIVE_ALREADY);

        // 5. 뿌리기 요청은 10분간만 유효
        LocalDateTime create = entity.getCreateTime();
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(create.plusMinutes(10)))
            throw new SpreadException(SpreadExceptEnum.RECEIVE_TIME_EXCEED);

        // 6. 뿌리기가 모두 할당되었을 경우 무효
        Optional<ReceiveDetail> first = entity.getReceiveInformation().stream()
                .filter(info -> info.getUserId() == null)
                .findFirst();
        if(!first.isPresent())
            throw new SpreadException(SpreadExceptEnum.RECEIVE_ALL_ASSIGN);

        ReceiveDetail firstElement = first.get();
        firstElement.setUserId(userId);
        spreadRepository.save(entity);
        log.debug("spread entity updated : {}", entity);
        return firstElement;
    }

    public InquiryResult inquiry(String userId, String token) {
        Optional<SpreadEntity> optional = spreadRepository.findById(token);
        // 1. 유효하지 않은 토큰
        if(!optional.isPresent())
            throw new SpreadException(SpreadExceptEnum.INVALID_TOKEN);

        // 2. 뿌린 사람만 조회 가능
        SpreadEntity entity = optional.get();
        if(!userId.equals(entity.getUserId()))
            throw new SpreadException(SpreadExceptEnum.INQUIRY_ONLY_SPREAD_USER);

        // 3. 뿌린 건에 대한 조회는 7일동안 유효
        LocalDateTime create = entity.getCreateTime();
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(create.plusDays(7)))
            throw new SpreadException(SpreadExceptEnum.INQUIRY_TIME_EXCEED);

        List<ReceiveDetail> list = entity.getReceiveInformation().stream()
                .filter(info -> info.getUserId() != null)
                .collect(Collectors.toList());

        long remain = entity.getAmount() - list.stream().mapToLong(r -> r.getAmount()).sum();

        InquiryResult result = InquiryResult.builder()
                .createTime(entity.getCreateTime())
                .amount(entity.getAmount())
                .receiveAmount(remain)
                .receiveInformation(list)
                .build();
        log.debug("spread inquiry : {}", result);
        return result;
    }

    private long[] partition(long amount, int n){
        if(amount < n || n <= 0)
            throw new SpreadException(SpreadExceptEnum.SPREAD_AMOUNT_LITTLE);

        long[] part = new long[n];
        long total = amount;
        for (int i = 0; i < n; i++) {
            if(total <= 1) {
                part[i] += total;
                break;
            }

            long cur = ThreadLocalRandom.current().nextLong(1, total);
            part[i] += cur;
            total -= cur;

            if(i == part.length-1 && total > 0){
                part[ThreadLocalRandom.current().nextInt(0, n)] += total;
            }
        }
        return part;
    }

}
