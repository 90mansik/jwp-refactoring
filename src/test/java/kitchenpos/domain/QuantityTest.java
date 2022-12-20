package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("수량 관련 테스트")
public class QuantityTest {

    @DisplayName("수량을 생성한다.")
    @Test
    void createQuantity(){
        // given
        long actualQuantity = 10;
        // when
        Quantity quantity = Quantity.from(actualQuantity);
        // then
        assertAll(
                () -> assertThat(quantity).isEqualTo(Quantity.from(actualQuantity)),
                () -> assertThat(quantity.getQuantity()).isEqualTo(actualQuantity)
        );
    }

    @DisplayName("가격이 비어있으면 에러가 발생한다.")
    @Test
    void isValidQuantityIsNegative(){
        // given
        long quantity = -1;
        // when & then
        assertThatThrownBy(
                () -> Quantity.from(quantity)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.QUANTITY_NOT_NEGATIVE.getMessage());
    }

    @DisplayName("수량의 형식을 BigDecimal로 변경 할 수 있다.")
    @Test
    void convertQuantityToBigDecimal(){
        // given
        long actualQuantity = 10;
        Quantity quantity = Quantity.from(actualQuantity);
        // when
        BigDecimal convertBigDecimal = quantity.toBigDecimal();
        // then
        assertAll(
                () -> assertThat(convertBigDecimal.longValue()).isEqualTo(actualQuantity),
                () -> assertThat(convertBigDecimal).isEqualTo(BigDecimal.valueOf(actualQuantity))
        );
    }
}
