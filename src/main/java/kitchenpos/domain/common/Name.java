package kitchenpos.domain.common;

import kitchenpos.common.ErrorCode;

public class Name {

    private String name;

    private Name(String name) {
        this.name = name;
    }

    public static Name from(String name) {
        isValidNameIsNull(name);
        return new Name(name);
    }

    private static void isValidNameIsNull(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.NAME_NOT_EMPTY.getMessage());
        }
    }

    public String getName() {
        return name;
    }
}
