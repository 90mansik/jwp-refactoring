# 키친포스

## 요구 사항

### 상품(Project)
- 메뉴를 관리하는 기준이 되는 데이터
- 식별자(id), 제품명(name), 가격(price) 정보 포함

- 상품(Product) 기본 기능
    - 상품 추가
    - 상품 리스트 출력
    
### 주문 테이블(OrderTable)
- 매장에서 주문이 발생하는 영역이다
- 식별자(id), 단체 지정 그룹(tableGroupId), 손님 수(numberOfGuests), 테이블 이용 여부(empty) 정보 포함한다. 

- (기능) `주문 테이블(OrderTable)` 생성 기능
    - `주문 테이블(OrderTable)`을 저장한다.
    
- (기능) `주문 테이블(OrderTable)` 조회 기능
    - `주문 테이블(OrderTable)` 목록을 가져온다.
    
- (기능) `주문 테이블(OrderTable)`의 `이용 여부(empty)` 상태 변경 기능
    - `주문 테이블(OrderTable)`의 `이용 여부(empty)` 상태를 변경한다.
    - (조건) `주문 테이블(OrderTable)`이 데이터베이스에 저장된 상태다.
    - (조건) 연관된 `단체 지정(TableGroup)` 정보가 존재하지 않는다. (null)
    - (조건) `주문 테이블(OrderTable)`와 연관된 `주문(Order)`이 `계산 완료(COMPLETION)` 상태다.
    
- (기능) `주문 테이블(OrderTable)`의 손님 수 변경 기능
    - `주문 테이블(OrderTable)`의 손님 수를 변경한다.
    - (조건) 0명 미만으로 변경할 수 없다.
    - (조건) 변경할 `주문 테이블(OrderTable)`이 데이터베이스에 저장된 상태다.
    - (조건) 변경할 `주문 테이블(OrderTable)`이 이용 중인 상태다. (Not empty)
    
### 단체 지정(TableGroup)
- 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능이다.
- 식별자(id), 생성 날짜(createdDate), 주문 테이블 목록(orderTables) 정보 포함한다.

- (기능) `단체 지정(TableGroup)` 생성 기능
    - `단체 지정(TableGroup)`를 저장한다.
    - `단체 지정(TableGroup)`과 연관된 `주문 테이블(OrderTable)` 상태를 `Not Empty` 상태로 변경한다.
    - `단체 지정(TableGroup)`과 연관된 `주문 테이블(OrderTable)` 외래키에 `단체 지정(TableGroup)` 정보를 업데이트한다.
    - (조건) 연관된 `주문 테이블(OrderTable)`이 2개 이상 존재한다.
    - (조건) 연관된 `주문 테이블(OrderTable)`이 데이터베이스에 저장된 상태다.
    - (조건) 연관된 `주문 테이블(OrderTable)`이 비어있는 상태다(empty).
    - (조건) 연관된 `주문 테이블(OrderTable)`이 기존에 다른 `단체 지정(TableGroup)`과 매핑되지 않은 상태다.
    
- (기능) `단체 지정(TableGroup)`과 `주문 테이블(OrderTable)` 간 그룹 해제 기능
    - `단체 지정(TableGroup)`과 연관된 `주문 테이블(OrderTable)` DB 외래키를 `null`로 업데이트 한다.
    - (조건) 연관된 `주문 테이블(OrTable)`과 연관된 `주문(Order)`이 `계산 완료(COMPLETION)` 상태다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |
