# Step 2: 객체지향과 계층 구조

## 학습 목표
- 객체지향 프로그래밍 핵심 개념 이해 (상속, 다형성, 추상 클래스, 인터페이스)
- Spring의 계층 구조 (Controller-Service-Repository) 이해
- 의존성 주입(DI)과 IoC 컨테이너 개념
- Repository Pattern을 통한 결합도 감소
- Entity 간 연관관계 매핑

---

## 1. 자바 객체지향 개념 (자바의 정석 Ch6-7)

### 1-1. 상속 (Inheritance)
기존 클래스를 재사용하여 새로운 클래스를 작성하는 것

**프로젝트 예시:** `BaseEntity`를 상속받는 모든 Entity (createdAt, updatedAt 공통 관리)

### 1-2. 다형성 (Polymorphism)
하나의 참조변수로 여러 타입의 객체를 참조하는 것

**프로젝트 예시:** `JpaRepository` 인터페이스 - Spring이 런타임에 구현체를 자동 생성

### 1-3. 추상 클래스 (Abstract Class)
인스턴스를 생성할 수 없고, 상속을 통해서만 사용되는 클래스

**프로젝트 예시:** `BaseEntity`는 추상 클래스 - 직접 생성 불가, 반드시 상속받아 사용

**인터페이스와의 차이:**
| 구분 | 추상 클래스 | 인터페이스 |
|------|------------|-----------|
| 목적 | 공통 기능 상속 (is-a) | 구현 강제 (can-do) |
| 상속 | 단일 상속만 가능 | 다중 구현 가능 |
| 필드 | 인스턴스 변수 가능 | 상수만 가능 |
| 메서드 | 일반 메서드 가능 | default 메서드만 가능 |

### 1-4. 인터페이스 (Interface)
추상 메서드의 집합, 구현을 강제

**프로젝트 예시:** `MemberRepository extends JpaRepository` - 인터페이스만 정의하면 Spring이 구현체 생성

**장점:** 테스트 용이 (Mock 객체 사용), 구현체 교체 용이, 결합도 감소

### 1-5. 접근 제어자

| 접근 제어자 | 클래스 내부 | 같은 패키지 | 자식 클래스 | 전체 |
|------------|-----------|-----------|-----------|------|
| private    | O         | X         | X         | X    |
| default    | O         | O         | X         | X    |
| protected  | O         | O         | O         | X    |
| public     | O         | O         | O         | O    |

---

## 2. Spring Boot 계층 구조

```
┌─────────────────────────────────────┐
│   Controller (Presentation Layer)   │  ← HTTP 요청/응답 처리
├─────────────────────────────────────┤
│     Service (Business Layer)        │  ← 비즈니스 로직
├─────────────────────────────────────┤
│  Repository (Data Access Layer)     │  ← 데이터 접근
├─────────────────────────────────────┤
│         Database (PostgreSQL)       │
└─────────────────────────────────────┘
```

### 각 계층의 역할

| 계층 | 역할 | 어노테이션 |
|------|------|-----------|
| Controller | HTTP 요청 받기, 응답 반환 | `@RestController` |
| Service | 비즈니스 로직, 트랜잭션 관리 | `@Service` |
| Repository | 데이터베이스 접근, CRUD | `@Repository` |

### 계층 간 데이터 흐름

```
Request DTO → Controller → Service → Repository → Entity
                  ↓          ↓          ↓           ↓
Response DTO ← Controller ← Service ← Repository ← Entity
```

---

## 3. 의존성 주입(DI)과 IoC

### 의존성 주입 (Dependency Injection)
외부에서 의존 객체를 주입받는 것 (생성자 주입 권장)

### IoC 컨테이너 (Inversion of Control)
Spring이 객체 생성과 생명주기를 관리

---

## 4. Entity 연관관계

### 다대일 (Many-to-One)
- 여러 대여(Rental) → 한 명의 회원(Member)
- 여러 대여(Rental) → 한 권의 책(Book)

### 지연 로딩 vs 즉시 로딩
- **LAZY (권장):** 연관된 엔티티를 실제 사용할 때 조회
- **EAGER:** 엔티티 조회 시 연관된 엔티티도 함께 조회

---

## 5. 실습 과제

### 사전 조건
- Step 1에서 Member CRUD API가 완성되어 있어야 함
- Entity (Book, Rental)가 정의되어 있어야 함

---

### 과제 1: Book CRUD API 구현

**목표:** Member API를 참고하여 Book API 구현

**구현할 파일:**
1. `BookRepository.java` - JpaRepository 상속
2. `BookRequest.java` - 요청 DTO
3. `BookResponse.java` - 응답 DTO
4. `BookService.java` - 비즈니스 로직
5. `BookController.java` - REST API

**API 명세:**

| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/books | 전체 도서 조회 |
| GET | /api/books/{id} | 도서 상세 조회 |
| GET | /api/books/available | 대여 가능 도서 조회 |
| POST | /api/books | 도서 등록 |
| POST | /api/books/{id}/update | 도서 수정 |
| POST | /api/books/{id}/delete | 도서 삭제 |

---

### 과제 2: Rental API 구현 (대여/반납)

**목표:** 연관관계가 있는 Entity를 다루는 API 구현

**대여 프로세스:**
1. 회원 존재 확인
2. 도서 존재 확인
3. 도서 대여 가능 여부 확인 (status = AVAILABLE)
4. 대여 기록 생성
5. 도서 상태 변경 (AVAILABLE → RENTED)

**반납 프로세스:**
1. 대여 기록 조회
2. 반납 처리 (returnDateTime 설정, status 변경)
3. 도서 상태 변경 (RENTED → AVAILABLE)

**구현할 파일:**
1. `RentalRepository.java`
2. `RentalRequest.java`
3. `RentalResponse.java`
4. `RentalService.java`
5. `RentalController.java`

**API 명세:**

| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/rentals | 전체 대여 조회 |
| GET | /api/rentals/{id} | 대여 상세 조회 |
| GET | /api/rentals/member/{memberId} | 회원별 대여 조회 |
| POST | /api/rentals | 도서 대여 |
| POST | /api/rentals/{id}/return | 도서 반납 |

---

### 과제 3: 비즈니스 규칙 추가

**목표:** 실무에서 필요한 검증 로직 추가

**구현할 규칙:**
1. 한 회원이 최대 3권까지만 대여 가능
2. 연체 중인 회원은 새로운 대여 불가
3. 이미 반납된 도서는 재반납 불가

**힌트:**
- `RentalRepository`에 `countByMemberIdAndStatus(Long memberId, RentalStatus status)` 추가
- `RentalRepository`에 `findByMemberIdAndStatus(Long memberId, RentalStatus status)` 추가

---

## 6. 학습 체크리스트

### 자바 객체지향
- [ ] BaseEntity 상속 구조 이해
- [ ] 추상 클래스와 인터페이스 차이 이해
- [ ] JpaRepository 다형성 이해
- [ ] private 필드와 public 메서드 사용 이유 이해

### Spring Framework
- [ ] Controller → Service → Repository 흐름 이해
- [ ] `@RequiredArgsConstructor` 생성자 주입 이해
- [ ] `@Transactional` 사용 위치 이해

### JPA
- [ ] `@ManyToOne` 연관관계 매핑
- [ ] `FetchType.LAZY` 사용 이유 이해
- [ ] Entity 변경 감지 (Dirty Checking) 이해

---

## 7. 생각해볼 질문

1. **왜 Controller에서 직접 Repository를 호출하지 않고 Service를 거치나요?**

2. **DTO를 사용하는 이유는?**

3. **`@Transactional(readOnly = true)`를 Service 클래스에 붙이고, 수정 메서드에만 `@Transactional`을 붙이는 이유는?**

4. **왜 Repository를 인터페이스로 만들고 구현체는 Spring이 생성하게 하나요?**

---

## 다음 단계 (Step 3)

- 예외 처리 및 Validation
- 페이징과 정렬
- 테스트 코드 작성
