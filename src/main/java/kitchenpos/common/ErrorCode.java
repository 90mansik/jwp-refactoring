package kitchenpos.common;

public enum ErrorCode {

    NAME_NOT_EMPTY("이름은 비어있을 수 없습니다."),
    PRICE_NOT_NEGATIVE("가격은 0보다 작을 수 없습니다."),
    PRICE_NOT_NULL("가격은 비어 있을 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
