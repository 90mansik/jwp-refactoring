package kitchenpos.menu.dto;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProducts;

public class MenuResponse {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Long menuGroupId;

    private final List<MenuProductResponse> menuProducts;

    public MenuResponse(Long id, String name, Integer price, Long menuGroupId,
                        List<MenuProductResponse> menuProducts) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.menuGroupId = menuGroupId;
        this.menuProducts = menuProducts;
    }

    public static MenuResponse from(Menu menu) {
        MenuProducts menuProducts = menu.getMenuProducts();
        List<MenuProductResponse> menuProductResponses = menuProducts.getMenuProducts().stream()
                .map(MenuProductResponse::from)
                .collect(Collectors.toList());

        return new MenuResponse(menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getMenuGroup().getId(),
                menuProductResponses);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public List<MenuProductResponse> getMenuProducts() {
        return menuProducts;
    }
}
