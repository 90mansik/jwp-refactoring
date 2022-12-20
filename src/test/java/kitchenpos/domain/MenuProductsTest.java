package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static kitchenpos.domain.MenuProductTestFixture.createMenuProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴 상품 집합 관련 도메인 테스트")
public class MenuProductsTest {

    private MenuProduct cheeseBurgerItem;
    private MenuProduct frenchFriesItem;

    @BeforeEach
    void setUp() {
        cheeseBurgerItem = createMenuProduct(1L, null, 1L, 1);
        frenchFriesItem = createMenuProduct(2L, null, 2L, 2);
    }

    @DisplayName("메뉴 상품 집합을 생성한다.")
    @Test
    void createMenuProducts() {
        // when
        MenuProducts menuProducts = MenuProducts.from(Arrays.asList(cheeseBurgerItem, frenchFriesItem));
        // then
        assertAll(
                () -> assertThat(menuProducts.values()).hasSize(2),
                () -> assertThat(menuProducts.values()).containsExactly(cheeseBurgerItem, frenchFriesItem)
        );
    }

    @DisplayName("메뉴 상품 집합 내 메뉴 상품이 없으면 에러가 발생한다.")
    @Test
    void isValidMenuProductsIsEmpty() {
        // when & then
        assertThatThrownBy(() -> MenuProducts.from(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.MENU_PRODUCTS_IS_NOT_EMPTY.getMessage());
    }

}
