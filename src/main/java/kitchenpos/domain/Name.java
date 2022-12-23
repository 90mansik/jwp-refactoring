package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

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

    public String value() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
