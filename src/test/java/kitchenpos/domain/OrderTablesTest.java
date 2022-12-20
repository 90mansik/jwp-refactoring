package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 테이블 집합 관련 테스트")
public class OrderTablesTest {

    private OrderTable orderTable1;
    private OrderTable orderTable2;

    @BeforeEach
    void setUp() {
        orderTable1 = createOrderTable(1L, 1L, 5, true);
        orderTable2 = createOrderTable(2L, 1L, 4, true);
    }

    @DisplayName("주문 테이블 집합을 생성한다.")
    @Test
    void createOrderTables() {
        // when
        OrderTables orderTables = OrderTables.from(Arrays.asList(orderTable1, orderTable2));
        // then
        assertAll(
                () -> assertThat(orderTables.values()).hasSize(2),
                () -> assertThat(orderTables.values()).containsExactly(orderTable1, orderTable2)
        );
    }

    @DisplayName("주문 테이블 집합 내 주문 테이블들의 그룹 상태를 해제하면, 각 주문 테이블의 그룹은 null이 된다.")
    @Test
    void upGroupOrderTables() {
        // given
        OrderTables orderTables = OrderTables.from(Arrays.asList(orderTable1, orderTable2));
        // when
        orderTables.unGroupOrderTables();
        // then
        assertThat(
                orderTables.values()
                        .stream()
                        .map(OrderTable::getTableGroupId))
                .containsExactly(null, null);
    }

    @DisplayName("주문 테이블 집합 내 주문 테이블이 2개 미만이면 주문 테이블 집합 생성 시 에러가 발생한다.")
    @Test
    void isValidOrderTableMinSize() {
        // when & then
        assertThatThrownBy(
                () -> OrderTables.from(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.ORDER_TABLE_NOT_MIN_SIZE.getMessage());
    }

}
