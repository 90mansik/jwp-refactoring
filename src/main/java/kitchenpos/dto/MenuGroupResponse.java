package kitchenpos.dto;

import kitchenpos.domain.MenuGroup;

public class MenuGroupResponse {
    private Long id;
    private String name;

    public MenuGroupResponse(MenuGroup menuGroup) {
        this.id = menuGroup.getId();
        this.name = menuGroup.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
