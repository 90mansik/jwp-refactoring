package kitchenpos.domain;

import kitchenpos.domain.menu.MenuPrice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴 가격 관련 테스트")
public class MenuPriceTest {

    @DisplayName("가격을 생성한다.")
    @Test
    void createPrice() {
        // given
        BigDecimal price = BigDecimal.valueOf(2000);
        // when
        MenuPrice menuPrice = MenuPrice.from(price);
        // then
        assertThat(menuPrice.getPrice()).isEqualTo(price);
    }

    @ParameterizedTest(name = "가격이 0보다 작으면 에러 발생")
    @ValueSource(longs = {-1000, -500})
    void isValidPriceIsNegative(long price) {
        // given
        BigDecimal actualPrice = BigDecimal.valueOf(price);
        // when & then
        Assertions.assertThatThrownBy(
                () -> MenuPrice.from(actualPrice)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격이 null이면 에러가 발생한다.")
    @Test
    void isValidPriceIsNull() {
        // when & then
        Assertions.assertThatThrownBy(
                () -> MenuPrice.from(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격을 비교할 수 있다.")
    @Test
    void comparePrice() {
        // given
        BigDecimal smallDecimal = BigDecimal.valueOf(2000);
        BigDecimal bigDecimal = BigDecimal.valueOf(3000);
        MenuPrice smallPrice = MenuPrice.from(smallDecimal);
        // when
        int compare = smallPrice.compareTo(bigDecimal);
        // then
        assertAll(
                () -> assertThat(compare).isEqualTo(smallPrice.compareTo(bigDecimal)),
                () -> assertThat(compare).isEqualTo(-1)
        );
    }

}
