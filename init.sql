-- 만화카페 관리 시스템 데이터베이스 초기화

-- Members 테이블
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Books 테이블
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    book_code VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    volume INTEGER NOT NULL,
    genre VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    condition VARCHAR(20) NOT NULL DEFAULT 'GOOD',
    location VARCHAR(50),
    remarks VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Seats 테이블
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    seat_number VARCHAR(20) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    current_member_id BIGINT,
    remarks VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (current_member_id) REFERENCES members(id)
);

-- Rentals 테이블
CREATE TABLE rentals (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    rental_date_time TIMESTAMP NOT NULL,
    return_date_time TIMESTAMP,
    due_date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    remarks VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Menus 테이블 (만화카페 먹거리/음료 메뉴)
CREATE TABLE menus (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description VARCHAR(500),
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Orders 테이블
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remarks VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    FOREIGN KEY (menu_id) REFERENCES menus(id)
);

-- 인덱스 생성
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_genre ON books(genre);
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_rentals_member_id ON rentals(member_id);
CREATE INDEX idx_rentals_book_id ON rentals(book_id);
CREATE INDEX idx_rentals_status ON rentals(status);
CREATE INDEX idx_seats_status ON seats(status);
CREATE INDEX idx_orders_member_id ON orders(member_id);
CREATE INDEX idx_orders_menu_id ON orders(menu_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_menus_category ON menus(category);
CREATE INDEX idx_menus_is_available ON menus(is_available);

-- Members 샘플 데이터
INSERT INTO members (name, email, phone, created_at, updated_at) VALUES
('김철수', 'chulsoo.kim@example.com', '010-1234-5678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', 'younghee.lee@example.com', '010-2345-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('박민수', 'minsoo.park@example.com', '010-3456-7890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('정수진', 'soojin.jung@example.com', '010-4567-8901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('최동욱', 'dongwook.choi@example.com', '010-5678-9012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Books 샘플 데이터
INSERT INTO books (book_code, title, author, publisher, volume, genre, status, condition, location, created_at, updated_at) VALUES
('MH-001-001', '원피스', '오다 에이이치로', '서울문화사', 1, '액션', 'AVAILABLE', 'GOOD', 'A-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-001-002', '원피스', '오다 에이이치로', '서울문화사', 2, '액션', 'AVAILABLE', 'GOOD', 'A-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-001-003', '원피스', '오다 에이이치로', '서울문화사', 3, '액션', 'RENTED', 'GOOD', 'A-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-002-001', '나루토', '키시모토 마사시', '서울문화사', 1, '액션', 'AVAILABLE', 'EXCELLENT', 'A-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-002-002', '나루토', '키시모토 마사시', '서울문화사', 2, '액션', 'RENTED', 'GOOD', 'A-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-003-001', '슬램덩크', '이노우에 다케히코', '대원씨아이', 1, '스포츠', 'AVAILABLE', 'FAIR', 'A-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-003-002', '슬램덩크', '이노우에 다케히코', '대원씨아이', 2, '스포츠', 'AVAILABLE', 'GOOD', 'A-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-004-001', '헌터X헌터', '토가시 요시히로', '서울문화사', 1, '판타지', 'AVAILABLE', 'EXCELLENT', 'A-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-005-001', '강철의 연금술사', '아라카와 히로무', '학산문화사', 1, 'SF', 'AVAILABLE', 'GOOD', 'B-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-005-002', '강철의 연금술사', '아라카와 히로무', '학산문화사', 2, 'SF', 'AVAILABLE', 'GOOD', 'B-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-006-001', '데스노트', '오바 츠구미', '서울문화사', 1, '미스터리', 'AVAILABLE', 'EXCELLENT', 'B-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-007-001', '진격의 거인', '이사야마 하지메', '학산문화사', 1, '액션', 'AVAILABLE', 'GOOD', 'B-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-007-002', '진격의 거인', '이사야마 하지메', '학산문화사', 2, '액션', 'RENTED', 'FAIR', 'B-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-008-001', '귀멸의 칼날', '고토게 코요하루', '서울미디어코믹스', 1, '액션', 'AVAILABLE', 'EXCELLENT', 'B-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MH-008-002', '귀멸의 칼날', '고토게 코요하루', '서울미디어코믹스', 2, '액션', 'AVAILABLE', 'GOOD', 'B-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seats 샘플 데이터
INSERT INTO seats (seat_number, type, status, current_member_id, created_at, updated_at) VALUES
('A-01', 'REGULAR', 'OCCUPIED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('A-02', 'REGULAR', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('A-03', 'REGULAR', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('A-04', 'REGULAR', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('B-01', 'PREMIUM', 'OCCUPIED', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('B-02', 'PREMIUM', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('C-01', 'COUPLE', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('C-02', 'COUPLE', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('R-01', 'ROOM', 'OCCUPIED', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('R-02', 'ROOM', 'AVAILABLE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Rentals 샘플 데이터
INSERT INTO rentals (member_id, book_id, rental_date_time, return_date_time, due_date_time, status, created_at, updated_at) VALUES
(1, 3, CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, CURRENT_TIMESTAMP + INTERVAL '5 days', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, CURRENT_TIMESTAMP + INTERVAL '6 days', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 13, CURRENT_TIMESTAMP - INTERVAL '10 days', NULL, CURRENT_TIMESTAMP - INTERVAL '3 days', 'OVERDUE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 1, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '8 days', 'RETURNED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Menus 샘플 데이터
INSERT INTO menus (name, category, price, description, is_available, created_at, updated_at) VALUES
-- 음료
('아메리카노', 'BEVERAGE', 3000.00, '깊고 진한 에스프레소', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('카페라떼', 'BEVERAGE', 3500.00, '부드러운 우유와 에스프레소의 조화', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('핫초코', 'BEVERAGE', 4000.00, '달콤한 초콜릿 음료', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('녹차라떼', 'BEVERAGE', 4000.00, '고소한 녹차와 우유', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('콜라', 'BEVERAGE', 2000.00, '코카콜라 355ml', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('사이다', 'BEVERAGE', 2000.00, '칠성사이다 355ml', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- 스낵
('감자튀김', 'SNACK', 4000.00, '바삭한 감자튀김', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('치즈스틱', 'SNACK', 4500.00, '쭉쭉 늘어나는 치즈스틱 5개', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('오징어땅콩', 'SNACK', 2500.00, '고소한 오징어땅콩', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('팝콘', 'SNACK', 3000.00, '카라멜 팝콘', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- 식사
('라면', 'MEAL', 4000.00, '신라면 + 계란', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('치즈라면', 'MEAL', 4500.00, '신라면 + 계란 + 치즈', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('떡볶이', 'MEAL', 5000.00, '매콤달콤 떡볶이', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('김밥', 'MEAL', 3500.00, '참치김밥 1줄', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Orders 샘플 데이터
INSERT INTO orders (member_id, seat_id, menu_id, quantity, total_price, order_date_time, status, created_at, updated_at) VALUES
(1, 1, 1, 1, 3000.00, CURRENT_TIMESTAMP - INTERVAL '1 hour', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 1, 7, 1, 4000.00, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, 2, 1, 3500.00, CURRENT_TIMESTAMP - INTERVAL '20 minutes', 'PREPARING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 9, 3, 2, 8000.00, CURRENT_TIMESTAMP - INTERVAL '10 minutes', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 통계를 위한 뷰
CREATE VIEW book_stats AS
SELECT
    COUNT(*) as total_books,
    COUNT(CASE WHEN status = 'AVAILABLE' THEN 1 END) as available_count,
    COUNT(CASE WHEN status = 'RENTED' THEN 1 END) as rented_count,
    COUNT(CASE WHEN status = 'LOST' THEN 1 END) as lost_count,
    COUNT(CASE WHEN status = 'DAMAGED' THEN 1 END) as damaged_count
FROM books;

-- 대여 중인 책 조회 뷰
CREATE VIEW active_rentals_view AS
SELECT
    r.id as rental_id,
    b.book_code,
    b.title as book_title,
    b.author,
    b.volume,
    m.name as member_name,
    m.phone,
    r.rental_date_time,
    r.due_date_time,
    r.status,
    CASE
        WHEN r.status = 'ACTIVE' AND r.due_date_time < CURRENT_TIMESTAMP THEN true
        ELSE false
    END as is_overdue
FROM rentals r
JOIN books b ON r.book_id = b.id
JOIN members m ON r.member_id = m.id
WHERE r.status IN ('ACTIVE', 'OVERDUE');

-- 좌석 사용 현황 뷰
CREATE VIEW seat_usage_view AS
SELECT
    s.seat_number,
    s.type,
    s.status,
    m.name as current_member_name,
    m.phone as current_member_phone
FROM seats s
LEFT JOIN members m ON s.current_member_id = m.id;

-- 테이블 코멘트
COMMENT ON TABLE members IS '회원 정보';
COMMENT ON TABLE books IS '만화책 정보';
COMMENT ON TABLE seats IS '좌석 정보';
COMMENT ON TABLE rentals IS '대여 정보';
COMMENT ON TABLE menus IS '메뉴 정보';
COMMENT ON TABLE orders IS '주문 정보';
