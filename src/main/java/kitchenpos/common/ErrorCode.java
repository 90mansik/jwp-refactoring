package kitchenpos.common;

public enum ErrorCode {

    CAN_NOT_LESS_THEN_ZERO_PRICE("가격은 0원 이하일 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
