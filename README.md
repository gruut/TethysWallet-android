# TethysWallet-android
TethysWallet android native application



## Development Environment

#### Kotlin
이 프로젝트는 모두 `Kotlin`으로 작성되었습니다.



#### RxJava2
Merger와 데이터 핑퐁할때 아주 편한 rx



#### Dagger2
module 추가하는 것을 잊지 마세요!



#### Serializer Libraries
- moshi: `retrofit` 내장 라이브러리 사용. `authority`와 REST API 통신할 때 사용
- jackson: `CBOR`와 `JSON` 모두 파싱이 가능함. Merger와 통신할 때 사용



#### Grpc

| RPC   Service | MSG NAME         | FROM   | TO     | DESCRIPTION                                                |
| ------------- | ---------------- | ------ | ------ | :--------------------------------------------------------- |
| UserService   | MSG_TX           | user   | merger |                                                            |
| PushService   | MSG_REQ_SSIG     | merger | signer | Stream으로 연결함                                          |
| SignerService | MSG_SSIG         | signer | merger |                                                            |
| KeyExService  | MSG_JOIN         | user   | merger | Signer/User의 네트워크 참여 요청                           |
| KeyExService  | MSG_CHALLENGE    | merger | user   | Merger가 Signer/User에게 보내는 신원 검증 요청             |
| KeyExService  | MSG_RESPONSE_1   | user   | merger | Signer/User가 Merger에게 보내는 신원 검증 요청에 대한 응답 |
| KeyExService  | MSG_RESPONSE_2   | merger | user   | Merger가 Signer/User에게 보내는 신원 검증 요청에 대한 응답 |
| KeyExService  | MSG_SUCCESS      | user   | merger | HMAC KEY가 정상적으로 생성되었음을 알림                    |
| KeyExService  | MSG_ACCEPT       | merger | user   | Merger가 Signer/User의 가입을 승인                         |
| UserService   | MSG_REQ_TX_CHECK | user   | merger | TX 조회 및 체크 메시지                                     |
| UserService   | MSG_RES_TX_CHECK | merger | user   | MSG_REQ_TX_CHECK의 응답                                    |
| UserService   | MSG_QUERY        | user   | merger | 스토리지 조회 메시지                                       |
| UserService   | MSG_RESULT       | merger | user   | MSG_QUERY의 응답                                           |
| UserService   | MSG_SETUP_MERGER | user   | merger |                                                            |
