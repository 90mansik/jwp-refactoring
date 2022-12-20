package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

    @Column(nullable = false)
    private String name;

    protected Name() {
    }

    private Name(String name) {
        isValidNameIsNull(name);
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
