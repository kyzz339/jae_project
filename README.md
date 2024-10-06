# jae_project(rest-api)
## 주요 기능

- **인증 및 사용자 관리**:
  - 로그인과 회원가입 기능이 포함되어 있으며 JWT를 사용하여 보안 인증을 구현하였습니다.

- **상품 관리**:
  - 상품 관련 데이터는 MySQL을 사용하여 저장하고 상품 이미지를 업로드할 수 있는 기능이 있습니다.
  - 상품 생성 시 관련된 채팅방이 자동으로 생성됩니다.
  - 상품 관리 API는 RESTful 방식으로 구현되었으며 주요 기능은 다음과 같습니다:
    - **상품 리스트 조회** (`GET /api/product/list`): 사용자는 페이지네이션된 상품 리스트를 조회할 수 있습니다.
    - **상품 등록** (`POST /api/product/create`): 상품 정보를 등록하고 이미지를 업로드할 수 있습니다.
    - **상품 상세 조회** (`GET /api/product/find/{id}`): 특정 상품의 상세 정보를 조회합니다.
    - **상품 삭제** (`POST /api/product/delete/{id}`): 상품의 소유자만 해당 상품을 삭제할 수 있습니다.
    - **상품 수정** (`POST /api/product/update`): 상품 정보를 수정할 수 있으며 이미지를 변경할 수 있습니다.

- **채팅 기능**:
  - 채팅 기능은 MongoDB에 저장되며 STOMP를 사용하여 실시간 메시징을 구현하였습니다.
  - 채팅 관련 API도 RESTful 방식으로 구성되어 있으며 주요 기능은 다음과 같습니다:
    - **채팅방 리스트 조회** (`GET /api/chat/rooms`): 사용자가 참여한 채팅방의 리스트를 조회합니다.
    - **채팅방 생성** (`POST /api/chat/rooms/create`): 새로운 채팅방을 생성합니다.
    - **채팅방 삭제** (`POST /api/chat/rooms/delete`): 호스트만 채팅방을 삭제할 수 있습니다.
    - **채팅방 입장** (`GET /api/chat/rooms/{roomId}`): 사용자가 채팅방에 입장하고 기존 채팅 내용을 불러옵니다.
    - **채팅방 나가기** (`POST /api/chat/rooms/{roomId}/exit`): 사용자가 채팅방에서 나가며 구독을 취소합니다.
    - **메시지 전송** (`POST /api/chat/rooms/{roomId}/messages`): 사용자가 채팅방에 메시지를 전송하고 메시지를 저장합니다.
    - **파일 업로드** (`POST /api/chat/upload/{roomId}/file`): 채팅방에 파일을 업로드합니다.
    - **파일 다운로드** (`GET /api/chat/{id}/download/{filename}`): 업로드된 파일을 다운로드합니다.
    - **채팅 메시지 삭제(취소)** (`POST /api/chat/delete/{id}`): 메시지를 삭제하지 않고 "삭제된 메시지입니다"로 수정합니다.

## 메시지 전송 방식

이 프로젝트에서는 REST API와 STOMP를 사용하여 채팅 메시지를 전송하고 있습니다. 주요 메시지 전송 방식으로 `@PostMapping`을 사용하였습니다. 그 이유와 장단점은 다음과 같습니다.

### @PostMapping을 이용한 메시지 전송

`@PostMapping("/rooms/{roomId}/messages")`는 HTTP POST 요청을 통해 메시지를 서버에 전송하고 이를 STOMP 프로토콜을 사용하여 구독 중인 사용자들에게 메시지를 전달하는 방식입니다.

**장점**:
- **RESTful API 통합**: 기존 RESTful 아키텍처와 일관성을 유지할 수 있습니다. 다른 CRUD 작업과 같은 방식으로 메시지를 처리하여 API 관리가 일관적입니다.
- **보안 처리 용이**: HTTP 프로토콜의 보안 기능(JWT 토큰 검증 등)을 그대로 사용할 수 있어 인증 및 권한 관리를 REST API 수준에서 손쉽게 처리할 수 있습니다.
- **로깅과 검증**: 메시지 전송 전후에 다양한 비즈니스 로직(예: 로깅, 데이터 검증 등)을 추가하기 용이합니다.

**단점**:
- **실시간 성능**: HTTP 요청의 특성상 약간의 지연 시간이 발생할 수 있습니다. 실시간성이 중요한 채팅에서 메시지 전송과 수신 간의 지연이 문제가 될 수 있습니다.
- **추가적인 트래픽**: HTTP 요청-응답으로 인해 네트워크 트래픽이 증가하며 지속적인 메시지 전송이 많은 경우 서버에 부하가 발생할 수 있습니다.

### @MessageMapping을 이용한 메시지 전송

`@MessageMapping`을 사용하는 방식은 WebSocket을 통해 메시지를 실시간으로 처리하는 방식입니다.

**장점**:
- **실시간 전송**: HTTP 요청 없이도 WebSocket을 통해 클라이언트와 서버 간의 양방향 통신을 즉시 수행할 수 있어 실시간 전송에 적합합니다.
- **효율성**: WebSocket 연결이 유지되는 동안에는 지속적인 핸드셰이크 과정이 필요 없으므로 메시지 전송의 효율성이 높아집니다.

**단점**:
- **복잡한 인증 처리**: WebSocket은 RESTful한 인증 절차와 다르게 토큰 등을 별도로 관리해야 하며 인증 절차가 비교적 복잡해질 수 있습니다.
- **호환성**: HTTP와 다르게 일부 구형 클라이언트나 네트워크에서는 WebSocket이 지원되지 않을 수 있으며 이를 위한 대체 메커니즘을 마련해야 합니다.

### 프로젝트에서의 선택

이 프로젝트에서는 REST API 기반의 `@PostMapping`을 통해 메시지를 전송하고 서버 측에서는 STOMP를 활용해 구독자들에게 메시지를 전달하는 방식을 선택했습니다. 이는 기존의 RESTful API와의 통합과 보안 관리가 용이하다는 점에서 장점이 있었기 때문입니다.

추후 성능 향상과 실시간 처리가 중요한 경우, `@MessageMapping` 방식으로 전환하거나 보완하여 WebSocket을 더 적극적으로 활용할 계획입니다.


Jenkin를 활용한 github 연동과 자동빌드화 예정 : https://www.notion.so/MAC-ecefde9037fa42acad8bed04e2722486

## 기술 스택

- **백엔드**: Java 1.8, Spring Boot, JPA
- **데이터베이스**: MySQL, MongoDB
- **인증**: JWT (JSON Web Token)
- **실시간 통신**: STOMP
- **API**: REST API
