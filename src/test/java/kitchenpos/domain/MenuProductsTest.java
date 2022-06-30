package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;
import kitchenpos.dto.MenuProductResponse;
import org.junit.jupiter.api.Test;

class MenuProductsTest {
    @Test
    void 메뉴와_메뉴_상품들_간의_연관관계를_설정할_수_있어야_한다() {
        // given
        final MenuProducts menuProducts = new MenuProducts();
        final Menu menu = new Menu(1L, "menu", new Price(15000L), 1L, menuProducts);
        final MenuProduct menuProduct1 = new MenuProduct(1L, 10L);
        final MenuProduct menuProduct2 = new MenuProduct(2L, 20L);

        // when
        menuProducts.makeRelations(menu, Arrays.asList(menuProduct1, menuProduct2));

        // then
        assertThat(menu.getMenuProducts()
                .stream().map(MenuProductResponse::getProductId).collect(Collectors.toList()))
                .containsExactly(menuProduct1.getProductId(), menuProduct2.getProductId());
    }
}
