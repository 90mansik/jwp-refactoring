package kitchenpos.common;

public enum ErrorCode {

    NAME_NOT_EMPTY("이름은 비어있을 수 없습니다."),
    PRICE_NOT_NEGATIVE("가격은 0보다 작을 수 없습니다."),
    PRICE_NOT_NULL("가격은 비어 있을 수 없습니다."),
    QUANTITY_NOT_NEGATIVE("수량은 0보다 작을 수 없습니다."),
    NUMBER_OF_GUEST_NOT_NEGATIVE("방문한 손님 수는 0보다 작을수 없습니다."),
    ORDER_TABLE_NOT_MIN_SIZE("주문 테이블은 2개 이상이여야 합니다."),
    MENU_PRODUCTS_IS_NOT_EMPTY("메뉴 상품은 비어있을 수 없습니다."),
    ORDER_LINE_ITEMS_IS_EMPTY("주문 항목은 비어있을 수 없습니다.");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
