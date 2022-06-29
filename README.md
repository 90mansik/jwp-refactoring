# 키친포스

## 요구 사항

#### :memo:상품
- 상품은 상품명과 가격으로 이루어져 있다.
- 상품을 등록할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 가격이 없거나 0원이면 등록할 수 없다. | IllegalArgumentException |

- 등록되어있는 상품 목록을 조회할 수 있다.

#### :memo:메뉴 그룹
- 메뉴 그룹은 메뉴 그룹명으로 이루어져 있다.
- 메뉴 그룹을 등록할 수 있다. 
- 등록되어있는 메뉴 그룹 목록을 조회할 수 있다.

#### :memo:메뉴
- 메뉴는 메뉴 가격, 메뉴 그룹, 메뉴 상품으로 이루어져 있다.
- 메뉴를 등록할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 메뉴 그룹이 없는 경우 등록 할 수 없다. | IllegalArgumentException |
      | 메뉴 가격이 금액(가격 * 수량) 보다 큰 경우 등록 할 수 없다. | IllegalArgumentException |
    
- 등록되어있는 메뉴 목록을 조회할 수 있다.

#### :memo:테이블
- 테이블은 단체 지정, 빈 테이블 여부, 방문 손님 수로 이루어져 있다.
- 테이블을 등록할 수 있다.
- 테이블 목록을 조회할 수 있다. (주문 테이블 & 빈 테이블 )
- 주문 테이블인지 빈 테이블인지 여부를 업데이트 할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 단체 지정이 있으면 업데이트 할 수 없다. | IllegalArgumentException |
      | 주문 상태가 조리, 식사인 경우 업데이트 할 수 없다. | IllegalArgumentException |

- 방문한 손님 수를 업데이트 할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 방문한 손님 수를 0 미만으로 요청하면 업데이트 할 수 없다.  | IllegalArgumentException |
      | 빈 테이블인 경우 업데이트 할 수 없다.  | IllegalArgumentException |

#### :memo:단체 지정
- 단체 지정은 테이블들로 이루어져 있다.
- 단체 지정을 등록 할 수 있다. (빈 테이블 그룹화)
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 단체 지정할 테이블이 2개 미만인 경우 등록 할 수 없다.  | IllegalArgumentException |
      | 존재하지 않는 테이블이 있는 경우에는 등록 할 수 없다. | IllegalArgumentException |
      | 주문 테이블 있는 경우 등록 할 수 없다.  | IllegalArgumentException |
      | 이미 단체 지정된 테이블이 있는 경우 등록 할 수 없다.  | IllegalArgumentException |

- 단체 지정을 해제 할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 요청한 테이블들 중 하나라도 주문 상태가 조리, 식사인 경우 해제 할 수 없다.| IllegalArgumentException |

#### :memo:주문
- 주문은 단체 지정, 주문 상태, 주문 항목으로 이루어져 있다. 
- 주문을 등록 할 수 있다.
    - 주문 상태가 조리로 변경된다.
    - 주문 항목을 등록한다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 주문 항목이 없는 경우 등록 할 수 없다.| IllegalArgumentException |
      | 없는 메뉴가 있는 경우 등록 할 수 없다.| IllegalArgumentException |
      | 주문 테이블이 없거나 빈 테이블인 경우 등록 할 수 없다.| IllegalArgumentException |

- 주문 목록을 조회 할 수 있다.
- 주문 상태를 업데이트 할 수 있다.
    * 예외 상황
      |내용|Exception|
      |---|---|
      | 주문 상태가 계산 완료인 경우 업데이트 할 수 없다. | IllegalArgumentException |
      
## 의존성 분리
- 객체 간 분리
    * [X] Order -/-> OrderTable
    * [X] OrderLineItem  -/-> Menu
    * [X] MenuProduct  -/-> Product


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
