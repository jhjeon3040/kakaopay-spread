# kakaopay 뿌리기

> 요구사항

- 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.

- 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록 설계되어야 합니다.

- 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

  

> API 명세

1. 뿌리기


    POST /v1/spread

| header    | value  |
| --------- | ------ |
| X-USER-ID | 유저id |
| X-ROOM-ID | 방id   |

```
Response status 201 Created

{
  "token": "abc"
}
```



2. 줍기


    PUT /v1/spread/{token}

| header    | value  |
| --------- | ------ |
| X-USER-ID | 유저id |
| X-ROOM-ID | 방id   |

```
Response status 201 Created

{
  "userId": "유저id",
  "amount": 11000
}
```



3. 조회하기


    GET /v1/spread/{token}

| header    | value  |
| --------- | ------ |
| X-USER-ID | 유저id |

```
Response status 200 OK

{
   "createTime":"2020-11-22T18:53:57.17",
   "amount":123,
   "receiveAmount":63,
   "receiveInformation":[
      {
         "amount":10,
         "userId":"user1"
      },
      {
         "amount":20,
         "userId":"user2"
      },
      {
         "amount":30,
         "userId":"user3"
      }
   ]
}
```





> 핵심 문제 해결 전략

- 3자리의 고유한 토큰 생성 시 [DB 조회 -> DB 존재하지 않을 경우 삽입] 과정을 빠르게 하기위해 redis 사용
- 멀티 인스턴스에서 동시성을 위해 Transaction 기능을 사용해야 하지만 실패

