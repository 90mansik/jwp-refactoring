# 키친포스

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

<br/>

## 1단계 - 테스를 통한 코드 보호

## 요구 사항
- [x] `kitchenpos` 패키지의 코드를 보고 키친포스의 요구 사항을 `README.md`에 작성한다. 미션을 진행함에 있어 아래 문서를 적극 활용한다.
- [x] 정리한 키친포스의 요구 사항을 토대로 테스트 코드를 작성한다. 모든 `Business Object`에 대한 테스트 코드를 작성한다. `@SpringBootTest`를 이용한 통합 테스트 코드 또는 `@ExtendWith(MockitoExtension.class)`를 이용한 단위 테스트 코드를 작성한다.

<br/>
<br/>

### 상품(Product)
- 상품을 등록할 수 있다.
  - 조건에 따라 상품을 등록할 수 없다.
    - 상품의 가격은 0원 이상이어야 한다.
- 상품 목록 조회할 수 있다.

### 메뉴그룹(MenuGroup)
- 메뉴그룹을 등록할 수 있다.
- 메뉴그룹 목록을 조회할 수 있다.

### 메뉴(Memu)
- 메뉴를 등록할 수 있다.
  - 조건에 따라 메뉴를 등록할 수 없다.
    - 메뉴의 가격은 0원 이상이어야 한다.
    - 메뉴의 가격은 포함하려는 상품들의 가격의 합보다 클 수 없다.
    - 사전에 등록된 메뉴 그룹에 속해야 한다.
    - 사전에 등록된 상품이어야 한다.
- 메뉴 목록을 조회할 수 있다.

### 주문 테이블(Table)
- 주문 테이블을 등록할 수 있다.
- 주문 테이블 목록을 조회할 수 있다.
- 주문 테이블이 비었는지 여부를 변경할 수 있다.
  - 조건에 따라 주문 테이블이 비었는지 여부를 변경할 수 없다.
    - 주문 테이블이 등록되어 있어야 한다.
    - 주문 테이블이 단체 지정에 속하지 않아야 한다.
    - 주문 테이블의 주문 상태가 조리, 식사 중이면 안된다.
- 주문 테이블에 방문한 손님 수를 변경할 수 있다.
  - 조건에 따라 주문 테이블에 방문한 손님 수를 변경할 수 없다.
    - 주문 테이블에 방문한 손님 수가 0명 이상이어야 한다.
    - 주문 테이블이 등록되어 있어야 한다.
    - 주문 테이블이 비어있으면 안된다.

### 단체 지정 (TableGroup)
- 단체 지정을 등록할 수 있다.
  - 조건에 따라 단체 지정을 등록할 수 없다.
    - 주문 테이블의 수가 2 이상이어야 한다.
    - 주문 테이블은 사전에 등록된 주문 테이블이어야 한다.
    - 빈 주문 테이블이어야 한다.
    - 단체 지정에 속하지 않은 주문 테이블이어야 한다.
- 단체 지정을 해제할 수 있다.
  - 조건에 따라 단체 지정을 해제할 수 없다.
    - 단체의 주문 테이블들의 주문 상태가 조리, 식사 중이면 안된다.

### 주문(Order)
- 주문을 등록할 수 있다.
  - 조건에 따라 주문을 등록할 수 없다.
    - 주문 항목은 1개 이상이여야 한다.
    - 주문에 속하는 메뉴는 사전에 등록된 메뉴이어야 한다.
    - 주문 테이블은 사전에 등록된 주문 테이블이어야 한다.
    - 주문 테이블이 빈 주문 테이블이면 안된다.
- 주문 목록을 조회할 수 있다.
- 주문 상태를 변경할 수 있다.
  - 조건에 따라 주문 상태를 변경할 수 없다.
    - 주문이 존재해야 한다.
    - 이미 완료된 주문은 변경할 수 없다.
