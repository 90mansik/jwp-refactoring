package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 테이블 관련 테스트 구현")
public class OrderTableTest {

    @DisplayName("주문 테이블의 그룹 상태를 해제한다.")
    @Test
    void unGroupOrderTable() {
        // given
        OrderTable orderTable = createOrderTable(1L, 1L, 5, true);
        // when
        orderTable.unGroup();
        // then
        assertThat(orderTable.getTableGroupId()).isNull();
    }

}
