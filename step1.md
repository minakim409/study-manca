# Step 1: Spring Boot REST API 기초 실습

## 학습 목표
- REST API 개념 이해
- HTTP 메서드와 CRUD 연산 매핑
- 멱등성(Idempotent) 개념 이해
- HTTP 상태 코드 활용
- RESTful URL 설계 원칙
- Spring Boot 프로젝트 구조 이해

---

## 프로젝트 배경: 만화카페 관리 시스템

이 프로젝트는 만화카페를 운영하는 시나리오를 기반으로 합니다.

**주요 기능:**
- 회원 관리 (Member)
- 만화책 관리 (Book)
- 좌석 관리 (Seat)
- 대여 관리 (Rental)
- 주문 관리 (Order)

**Step 1에서는 회원 관리 기능을 중심으로 REST API를 학습합니다.**

---

## 1. REST API 개념

REST(Representational State Transfer)는 HTTP를 사용해 데이터를 주고받는 웹 API 설계 방식입니다.

### 핵심 원칙:
1. **자원(Resource)을 URL로 표현**
   - 예: `/api/members`, `/api/books`

2. **행위(Action)를 HTTP 메서드로 표현**
   - GET, POST, PUT, PATCH, DELETE

3. **무상태성(Stateless)**
   - 각 요청은 독립적이며 서버는 클라이언트의 상태를 저장하지 않음

---

## 2. HTTP 메서드와 CRUD 매핑

| HTTP 메서드 | CRUD | 설명 | 멱등성 | 예시 |
|------------|------|------|--------|------|
| GET | Read | 리소스 조회 | O | 회원 목록 조회 |
| POST | Create | 리소스 생성 | X | 신규 회원 등록 |
| PUT | Update | 리소스 전체 수정 | O | 회원 정보 전체 수정 |
| PATCH | Update | 리소스 부분 수정 | X | 회원 전화번호만 수정 |
| DELETE | Delete | 리소스 삭제 | O | 회원 삭제 |

### 멱등성(Idempotent)이란?
- 같은 요청을 여러 번 실행해도 결과가 동일한 성질
- GET: 여러 번 조회해도 데이터는 변하지 않음 (O)
- POST: 여러 번 실행하면 여러 개의 리소스가 생성됨 (X)
- PUT: 여러 번 실행해도 최종 상태는 동일함 (O)
- DELETE: 이미 삭제된 리소스를 다시 삭제해도 결과는 동일함 (O)

---

## 3. RESTful URL 설계 예시

```
GET    /api/members          # 전체 회원 목록 조회
GET    /api/members/{id}     # 특정 회원 조회
POST   /api/members          # 회원 생성
PUT    /api/members/{id}     # 회원 정보 전체 수정
PATCH  /api/members/{id}     # 회원 정보 부분 수정
DELETE /api/members/{id}     # 회원 삭제
```

### URL 설계 원칙:
1. 명사를 사용 (동사 X)
   - Good: `/api/members`
   - Bad: `/api/getMembers`, `/api/createMember`

2. 복수형 사용
   - Good: `/api/members`
   - Bad: `/api/member`

3. 계층 구조 표현
   - `/api/members/{id}/rentals` - 특정 회원의 대여 내역

---

## 4. HTTP 상태 코드

### 2xx - 성공
- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공 (POST)
- `204 No Content`: 성공했지만 반환 데이터 없음 (DELETE)

### 4xx - 클라이언트 오류
- `400 Bad Request`: 잘못된 요청 (validation 실패)
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 리소스 충돌 (중복 이메일)

### 5xx - 서버 오류
- `500 Internal Server Error`: 서버 내부 오류

---

## 5. 프로젝트 구조

```
manca/
├── src/
│   ├── main/
│   │   ├── java/com/study/manca/
│   │   │   ├── MancaApplication.java          # 메인 애플리케이션
│   │   │   ├── controller/                    # REST 컨트롤러
│   │   │   │   └── MemberController.java
│   │   │   ├── service/                       # 비즈니스 로직
│   │   │   │   └── MemberService.java
│   │   │   ├── repository/                    # 데이터 접근 계층
│   │   │   │   └── MemberRepository.java
│   │   │   ├── entity/                        # JPA 엔티티
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── Member.java
│   │   │   │   ├── Book.java
│   │   │   │   ├── Seat.java
│   │   │   │   ├── Rental.java
│   │   │   │   └── Order.java
│   │   │   └── dto/                           # 데이터 전송 객체
│   │   │       ├── MemberRequest.java
│   │   │       └── MemberResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── build.gradle
├── docker-compose.yml                         # PostgreSQL 설정
└── init.sql                                   # 초기 데이터
```

---

## 6. 기술 스택

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA** - 데이터베이스 연동
- **PostgreSQL** - 관계형 데이터베이스
- **Lombok** - 보일러플레이트 코드 감소

---

## 7. 실습 과정

### Step 1-1: 환경 설정

**데이터베이스 실행:**
```bash
docker-compose up -d
```

**애플리케이션 실행:**
```bash
./gradlew bootRun
```

### Step 1-2: 코드 분석

#### Entity (Member.java)
```java
@Entity
@Table(name = "members")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
}
```
- `@Entity`: JPA 엔티티 클래스
- `@Table`: 데이터베이스 테이블 매핑
- `@Id`: 기본키
- `@GeneratedValue`: 자동 증가

#### BaseEntity.java
```java
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```
- 생성/수정 시간 자동 관리
- 다른 엔티티에서 상속받아 사용

#### DTO (MemberRequest.java / MemberResponse.java)
```java
// 요청 DTO
public class MemberRequest {
    private String name;
    private String email;
    private String phone;

    public Member toEntity() { ... }
}

// 응답 DTO
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponse from(Member member) { ... }
}
```
- Entity를 직접 노출하지 않고 DTO 사용
- 요청/응답 데이터 구조 분리

#### Repository (MemberRepository.java)
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
```
- Spring Data JPA 인터페이스
- 기본 CRUD 메서드 자동 제공
- 커스텀 쿼리 메서드 정의 가능

#### Service (MemberService.java)
```java
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public List<MemberResponse> findAll() { ... }

    @Transactional
    public MemberResponse create(MemberRequest request) { ... }
}
```
- 비즈니스 로직 계층
- `@Transactional`: 트랜잭션 관리

#### Controller (MemberController.java)
```java
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() { ... }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) { ... }
}
```
- `@RestController`: REST API 컨트롤러
- `@RequestMapping`: URL 매핑
- `@GetMapping`, `@PostMapping`: HTTP 메서드 매핑

### Step 1-3: API 테스트

#### 1. 전체 회원 조회
```bash
GET http://localhost:8080/api/members
```

#### 2. 특정 회원 조회
```bash
GET http://localhost:8080/api/members/1
```

#### 3. 회원 생성
```bash
POST http://localhost:8080/api/members
Content-Type: application/json

{
  "name": "홍길동",
  "email": "hong@example.com",
  "phone": "010-9999-8888"
}
```

#### 4. 회원 수정 (PUT - 전체)
```bash
PUT http://localhost:8080/api/members/1
Content-Type: application/json

{
  "name": "홍길동2",
  "email": "hong2@example.com",
  "phone": "010-9999-7777"
}
```

#### 5. 회원 수정 (PATCH - 부분)
```bash
PATCH http://localhost:8080/api/members/1
Content-Type: application/json

{
  "phone": "010-9999-6666"
}
```

#### 6. 회원 삭제
```bash
DELETE http://localhost:8080/api/members/1
```

---

## 8. 학습 포인트

### Java 기초 복습 (Ch1-6)
1. **변수와 자료형**: Entity 필드 (Long id, String name)
2. **클래스**: Entity, DTO, Service, Controller
3. **메서드**: Service의 비즈니스 로직 메서드
4. **조건문**: Service에서 validation 로직
5. **null 처리**: Optional 사용

### Spring Boot 개념
1. **의존성 주입(DI)**: `@RequiredArgsConstructor`로 생성자 주입
2. **계층 구조**: Controller → Service → Repository
3. **어노테이션**: `@RestController`, `@Service`, `@Entity` 등

---

## 9. 실습 과제

1. **Member API 테스트**
   - 모든 CRUD 엔드포인트 테스트
   - 각 상태 코드 확인 (200, 201, 204, 404)

2. **코드 이해**
   - 각 계층의 역할 파악
   - Entity와 DTO의 차이점 이해
   - PUT vs PATCH 차이 이해

3. **질문 생각해보기**
   - Entity를 직접 반환하지 않고 DTO를 사용하는 이유는?
   - Service에서 `@Transactional`을 사용하는 이유는?
   - Repository 인터페이스만 선언했는데 구현체는 어디에?

---

## 다음 단계 (Step 2)

- 객체지향 개념 (상속, 다형성, 인터페이스)
- 계층 구조와 의존성 주입
- Book 대여 기능 구현
- Entity 간 연관관계 매핑
