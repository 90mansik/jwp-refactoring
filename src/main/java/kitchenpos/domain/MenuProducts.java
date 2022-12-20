package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class MenuProducts {

    private List<MenuProduct> menuProducts = new ArrayList<>();

    protected MenuProducts() {
    }

    private MenuProducts(List<MenuProduct> menuProducts) {
        isValidMenuProductsIsEmpty(menuProducts);
        this.menuProducts = new ArrayList<>(menuProducts);
    }

    public static MenuProducts from(List<MenuProduct> menuProducts) {
        return new MenuProducts(menuProducts);
    }

    private void isValidMenuProductsIsEmpty(List<MenuProduct> menuProducts) {
        if (menuProducts.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.MENU_PRODUCTS_IS_NOT_EMPTY.getMessage());
        }
    }

    public List<MenuProduct> values() {
        return Collections.unmodifiableList(menuProducts);
    }

}
