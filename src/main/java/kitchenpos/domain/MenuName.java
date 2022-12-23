package kitchenpos.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MenuName {

    @Column(nullable = false)
    private String name;

    protected MenuName() {

    }

    private MenuName(String name) {
        this.name = name;
    }

    public static MenuName from(String name) {
        return new MenuName(name);
    }

    public String getName() {
        return name;
    }
}
