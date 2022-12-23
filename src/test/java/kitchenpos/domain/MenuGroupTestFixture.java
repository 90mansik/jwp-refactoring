package kitchenpos.domain;

import kitchenpos.dto.MenuGroupRequest;

public class MenuGroupTestFixture {

    public static MenuGroup createMenuGroup(Long id, String name) {
        return MenuGroup.of(id, name);
    }

    public static MenuGroupRequest createMenuGroup(String name) {
        return MenuGroupRequest.from(name);
    }
}
