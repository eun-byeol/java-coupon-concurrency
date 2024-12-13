# DB 복제와 캐시

## 1단계

### 1. 쿠폰 생성, 조회 기능 구현

- 두 대의 DB를 사용한다.
- 조회 기능은 reader DB 데이터를 조회한다.

### 쿠폰

- 이름
    - notnull, 30자 이하
- 할인 금액
    - 1,000원 ~ 10,000원
    - 500원 단위
    - 할인율
        - 할인금액 / 최소 주문 금액 (소수점 버림)
        - 3% ~ 20%
- 최소 주문 금액
    - 5,000원 ~ 100,000원
- 카테고리
    - 오직 하나의 카테고리만 선택
    - 패션, 가전, 가구, 식품
- 발급 기간
    - 시작일 <= 종료일
    - 시작일 : 00:00:00.000000 ~
    - 종료일 : ~ 23:59:59.999999

### 회원 쿠폰

- 회원당 쿠폰 한장 발급
    - 회원 쿠폰 PK
    - 쿠폰 PK
    - 회원 PK
    - 사용 여부
    - 발급 일시
    - 만료 일시
- 발급일 포함 7일 동안 사용 가능
- 만료일의 23:59:59.999999 까지 사용

### 2. 복제 지연으로 인한 이슈 해결

- 쿠폰을 생성한 후 즉시 조회했을 때 복제 지연으로 인해 데이터 조회에 실패한다.
- 해결한 이유를 리뷰어가 이해할 수 있도록 설명해야 한다.

## 2단계

### 1. 회원 쿠폰 발급 기능 구현

- 회원 당 최대 5장까지 발급 가능하다.
- 중복 쿠폰 발급 가능하다.

### 2. 회원 쿠폰 목록 조회 기능 구현

- 쿠폰, 회원 쿠폰 테이블 조인 불가하다.
- 회원 쿠폰 목록 조회 시, 쿠폰과 발급된 쿠폰 정보를 보여줘야 한다.

### 3. 쿠폰 캐싱

- 쿠폰 목록 조회 시, 캐시로 쿠폰 정보 조회하도록 수정한다.
- 선택한 캐시 전략과 이유를 설명해야 한다.

## 3단계

- Coupon 엔티티에 @DynamicUpdate 어노테이션 설정

### 1. 쿠폰 할인 금액 변경, 최소 주문 금액 변경 기능 구현

- 쿠폰 수정시에도 쿠폰의 제약조건을 만족해야 한다.

### 2. 쿠폰 수정 기능의 동시성 이슈 해결

- 동시 호출로 인해 제약조건을 위반하는 쿠폰이 생성될 수 있는 이슈를 해결한다.
