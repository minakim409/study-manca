# Step 2: 객체지향과 계층 구조

## 학습 목표
- 객체지향 프로그래밍 핵심 개념 이해 (상속, 다형성, 인터페이스)
- Spring의 계층 구조 (Controller-Service-Repository) 이해
- 의존성 주입(DI)과 IoC 컨테이너 개념
- Entity 간 연관관계 매핑
- 만화책 대여 비즈니스 로직 구현

---

## 프로젝트 배경

**Step 2에서는 만화카페의 핵심 기능인 '만화책 대여' 기능을 구현합니다.**

### 대여 프로세스:
1. 회원이 좌석을 배정받음
2. 회원이 읽고 싶은 만화책을 선택
3. 대여 기록 생성 (대여일, 반납예정일 설정)
4. 만화책 상태를 'RENTED'로 변경
5. 만화책 반납 시 상태를 'AVAILABLE'로 변경

---

## 1. 자바 객체지향 개념 (자바의 정석 Ch6-7)

### 1-1. 상속 (Inheritance)

**개념:** 기존 클래스를 재사용하여 새로운 클래스를 작성

```java
// 부모 클래스
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;
}

// 자식 클래스
@Entity
public class Member extends BaseEntity {
    private Long id;
    private String name;
    // createdAt, updatedAt을 상속받음
}
```

**장점:**
- 코드 재사용성
- 유지보수 용이
- 공통 기능을 한 곳에서 관리

### 1-2. 다형성 (Polymorphism)

**개념:** 하나의 참조변수로 여러 타입의 객체를 참조

**기본 예시:**
```java
// List 인터페이스로 여러 구현체를 참조 가능
List<Book> books1 = new ArrayList<>();
List<Book> books2 = new LinkedList<>();

// 모두 같은 List 타입이지만, 실제 구현은 다름
books1.add(new Book());  // ArrayList의 add 실행
books2.add(new Book());  // LinkedList의 add 실행
```

**실무 활용 - JpaRepository:**

우리가 작성하는 Repository 인터페이스가 바로 다형성의 예시입니다.

```java
// 개발자가 작성하는 코드: 인터페이스만 정의
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByGenre(String genre);
}
```

```java
// Spring Data JPA가 런타임에 자동으로 구현체를 생성
// 개발자는 구현체를 직접 작성하지 않음!

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findMember(Long id) {
        // memberRepository는 인터페이스지만,
        // 런타임에 Spring이 생성한 구현체의 findById()가 실행됨
        return memberRepository.findById(id).orElseThrow();
    }
}
```

**핵심 포인트:**
- `MemberRepository`는 인터페이스 (구현 없음)
- Spring이 런타임에 프록시 객체를 생성해서 주입
- `findById()`, `save()`, `findByEmail()` 등이 실제로 동작함
- 개발자는 "어떻게 동작하는지" 몰라도 됨 → 이것이 다형성의 장점

### 1-3. 인터페이스 (Interface)

**개념:** 추상 메서드의 집합, 구현을 강제

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**장점:**
- 구현과 인터페이스 분리
- 테스트 용이 (Mock 객체 사용)
- 변경에 유연

### 1-4. 접근 제어자

| 접근 제어자 | 클래스 내부 | 같은 패키지 | 자식 클래스 | 전체 |
|------------|-----------|-----------|-----------|------|
| private | O | X | X | X |
| default | O | O | X | X |
| protected | O | O | O | X |
| public | O | O | O | O |

**실무 활용:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;  // private: 외부에서 직접 수정 불가

    public void changeStatus(BookStatus newStatus) {  // public: 비즈니스 로직으로 상태 변경
        this.status = newStatus;
    }
}
```

---

## 2. Spring Boot 계층 구조

### 2-1. Layered Architecture

```
┌─────────────────────────────────────┐
│   Controller (Presentation Layer)   │  ← HTTP 요청/응답 처리
├─────────────────────────────────────┤
│     Service (Business Layer)        │  ← 비즈니스 로직
├─────────────────────────────────────┤
│  Repository (Data Access Layer)     │  ← 데이터 접근
├─────────────────────────────────────┤
│         Database (PostgreSQL)        │
└─────────────────────────────────────┘
```

### 2-2. 각 계층의 역할

#### Controller (컨트롤러)
- HTTP 요청 받기
- 요청 데이터 검증
- Service 호출
- HTTP 응답 반환

```java
@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponse> rentBook(@RequestBody RentalRequest request) {
        RentalResponse rental = rentalService.rentBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
}
```

#### Service (서비스)
- 비즈니스 로직 처리
- 트랜잭션 관리
- 여러 Repository 조합

```java
@Service
@Transactional(readOnly = true)
public class RentalService {
    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RentalResponse rentBook(RentalRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 2. 책 조회
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // 3. 대여 가능 여부 확인
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available");
        }

        // 4. 대여 처리
        Rental rental = Rental.builder()
            .member(member)
            .book(book)
            .rentalDateTime(LocalDateTime.now())
            .dueDateTime(LocalDateTime.now().plusDays(7))
            .build();

        rentalRepository.save(rental);
        return RentalResponse.from(rental);
    }
}
```

#### Repository (리포지토리)
- 데이터베이스 접근
- CRUD 작업

```java
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByMemberId(Long memberId);
    List<Rental> findByStatus(RentalStatus status);
}
```

### 2-3. 계층 간 데이터 흐름

```
Request DTO → Controller → Service → Repository → Entity
                  ↓          ↓          ↓           ↓
Response DTO ← Controller ← Service ← Repository ← Entity
```

---

## 3. 의존성 주입(DI)과 IoC

### 3-1. 의존성 주입 (Dependency Injection)

**문제:** 객체가 직접 의존 객체를 생성하면 결합도가 높아짐

```java
// 나쁜 예 - 강한 결합
public class RentalService {
    private RentalRepository rentalRepository = new RentalRepositoryImpl();
}
```

**해결:** 외부에서 의존 객체를 주입

```java
// 좋은 예 - 느슨한 결합
@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    // 생성자 주입
    @RequiredArgsConstructor  // Lombok이 생성자 자동 생성
    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }
}
```

### 3-2. IoC 컨테이너

**IoC (Inversion of Control)**: 제어의 역전

- 개발자가 아닌 Spring이 객체 생성과 생명주기 관리
- `@Component`, `@Service`, `@Repository`, `@Controller` 어노테이션이 붙은 클래스를 자동으로 Bean 등록

```
개발자: "RentalService는 RentalRepository가 필요해"
Spring: "알았어, 내가 RentalRepository 객체를 만들어서 주입해줄게"
```

---

## 4. Entity 연관관계 매핑

### 4-1. 다대일 (Many-to-One)

```java
@Entity
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 여러 대여 → 한 명의 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;  // 여러 대여 → 한 권의 책
}
```

### 4-2. 지연 로딩 (Lazy Loading) vs 즉시 로딩 (Eager Loading)

**지연 로딩 (LAZY):** 연관된 엔티티를 실제 사용할 때 조회
```java
@ManyToOne(fetch = FetchType.LAZY)
private Member member;

// rental.getMember()를 호출할 때 DB 조회
```

**즉시 로딩 (EAGER):** 엔티티 조회 시 연관된 엔티티도 함께 조회
```java
@ManyToOne(fetch = FetchType.EAGER)
private Member member;

// rental을 조회할 때 member도 함께 조회
```

**권장:** 기본적으로 LAZY 사용 (성능 최적화)

---

## 5. 실습: 만화책 대여 기능 구현

### 5-1. DTO 작성

```java
// RentalRequest.java
@Getter
@NoArgsConstructor
public class RentalRequest {
    private Long memberId;
    private Long bookId;
}

// RentalResponse.java
@Getter
public class RentalResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime rentalDateTime;
    private LocalDateTime dueDateTime;
    private RentalStatus status;

    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
            rental.getId(),
            rental.getMember().getId(),
            rental.getMember().getName(),
            rental.getBook().getId(),
            rental.getBook().getTitle(),
            rental.getRentalDateTime(),
            rental.getDueDateTime(),
            rental.getStatus()
        );
    }
}
```

### 5-2. Repository 작성

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(BookStatus status);
    List<Book> findByGenre(String genre);
}

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByMemberId(Long memberId);
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findByMemberIdAndStatus(Long memberId, RentalStatus status);
}
```

### 5-3. Service 작성

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {
    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    // 대여
    @Transactional
    public RentalResponse rentBook(RentalRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available");
        }

        Rental rental = Rental.builder()
            .member(member)
            .book(book)
            .rentalDateTime(LocalDateTime.now())
            .dueDateTime(LocalDateTime.now().plusDays(7))
            .build();

        rentalRepository.save(rental);
        return RentalResponse.from(rental);
    }

    // 반납
    @Transactional
    public void returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        rental.returnBook();  // Entity 메서드로 비즈니스 로직 처리
    }

    // 회원별 대여 내역 조회
    public List<RentalResponse> getMemberRentals(Long memberId) {
        return rentalRepository.findByMemberId(memberId).stream()
            .map(RentalResponse::from)
            .collect(Collectors.toList());
    }
}
```

### 5-4. Controller 작성

```java
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    // 대여하기
    @PostMapping
    public ResponseEntity<RentalResponse> rentBook(@RequestBody RentalRequest request) {
        RentalResponse rental = rentalService.rentBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    // 반납하기
    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        rentalService.returnBook(id);
        return ResponseEntity.ok().build();
    }

    // 회원별 대여 내역
    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<RentalResponse>> getMemberRentals(@PathVariable Long memberId) {
        List<RentalResponse> rentals = rentalService.getMemberRentals(memberId);
        return ResponseEntity.ok(rentals);
    }
}
```

---

## 6. 학습 포인트

### 자바 객체지향 (Ch6-7)
1. **상속**: BaseEntity를 상속받는 모든 Entity
2. **다형성**: JpaRepository 인터페이스
3. **인터페이스**: Repository, Service 계층
4. **접근 제어자**: Entity 필드는 private, 메서드는 public

### Spring Framework
1. **계층 구조**: Controller → Service → Repository
2. **의존성 주입**: `@RequiredArgsConstructor`로 생성자 주입
3. **IoC 컨테이너**: `@Service`, `@Repository` Bean 자동 관리
4. **트랜잭션**: `@Transactional`로 데이터 일관성 보장

### JPA
1. **연관관계 매핑**: `@ManyToOne`
2. **지연 로딩**: `fetch = FetchType.LAZY`
3. **영속성 컨텍스트**: 엔티티 변경 감지

---

## 7. 실습 과제

### 과제 1: Book CRUD API 구현
- BookController, BookService 작성
- 책 목록 조회, 특정 책 조회, 책 등록, 수정, 삭제 API 구현

### 과제 2: 대여 기능 테스트
1. 회원 등록
2. 책 등록
3. 책 대여
4. 대여 내역 조회
5. 책 반납

### 과제 3: 비즈니스 로직 추가
- 한 회원이 최대 3권까지만 대여 가능
- 연체 중인 회원은 대여 불가
- 대여 시 책 상태를 'RENTED'로 변경

---

## 8. 생각해볼 질문

1. **왜 Controller에서 직접 Repository를 호출하지 않고 Service를 거치나요?**
   - 비즈니스 로직이 Controller에 섞이면 재사용이 어려움
   - 트랜잭션 관리가 Service 계층에서 이루어짐
   - 테스트가 용이함

2. **DTO를 사용하는 이유는?**
   - Entity를 직접 노출하면 보안 문제 (password 등)
   - API 스펙 변경 시 Entity 변경을 강제하지 않음
   - 순환 참조 문제 방지

3. **인터페이스를 사용하는 이유는?**
   - 구현체 교체가 쉬움 (테스트 시 Mock 사용)
   - 느슨한 결합
   - 다형성 활용

---

## 다음 단계 (Step 3)

- 예외 처리 및 Validation
- 페이징과 정렬
- QueryDSL을 활용한 동적 쿼리
- 테스트 코드 작성
