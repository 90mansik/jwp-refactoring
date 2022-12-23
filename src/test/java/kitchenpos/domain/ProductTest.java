package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품 관련 도메인 테스트")
public class ProductTest {

    @DisplayName("상품 생성 시, 가격이 비어있으면 에러가 발생한다.")
    @Test
    void isValidPriceIsNull() {
        // when & then
        assertThatThrownBy(() -> createProduct("감자튀김", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.PRICE_NOT_NULL.getMessage());
    }

    @ParameterizedTest(name = "상품 생성 시, 가격은 음수이면 에러가 발생한다. (가격: {0})")
    @ValueSource(longs = {-1000, -2030})
    void isValidPriceIsNegative(long price) {
        // when & then
        assertThatThrownBy(() -> createProduct("감자튀김", BigDecimal.valueOf(price)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.PRICE_NOT_NEGATIVE.getMessage());
    }

    @DisplayName("상품 생성 시, 이름이 null이면 에러가 발생한다.")
    @Test
    void isValidNameIsNull() {
        // when & then
        assertThatThrownBy(() -> createProduct(null, BigDecimal.valueOf(3000)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.NAME_NOT_EMPTY.getMessage());
    }

    @DisplayName("상품 생성 시, 이름이 비어있이면 에러가 발생한다.")
    @Test
    void isValidNameIsEmpty() {
        // when & then
        assertThatThrownBy(() -> createProduct("", BigDecimal.valueOf(3000)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.NAME_NOT_EMPTY.getMessage());
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void createProducts() {
        // given
        String name = "감자튀김";
        BigDecimal price = BigDecimal.valueOf(3000);
        // when
        Product product = createProduct(name, price);
        // then
        assertAll(
                () -> assertThat(product.getName()).isEqualTo(Name.from(name)),
                () -> assertThat(product.getPrice()).isEqualTo(Price.from(price))
        );
    }
}
