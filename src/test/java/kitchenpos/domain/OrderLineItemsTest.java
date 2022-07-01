package kitchenpos.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderLineItemsTest {

    @Test
    void 주문_항목이_있는지_확인한다() {
        // when & then
        assertThatThrownBy(() ->
                new OrderLineItems(new ArrayList<>())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목이 없습니다.");
    }

    @Test
    void 메뉴_아이디_목록을_조회한다() {
        // when
        List<Long> result = createOrderLineItems().getMenuIds();

        // then
        assertThat(result).containsExactly(1L, 2L);
    }

    @Test
    void 중복된_메뉴가_있는지_확인한다() {
        // when & then
        assertThatThrownBy(() ->
                createDuplicateOrderLineItems().validateDuplicateMenu(1)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 메뉴가 있습니다.");
    }

    private OrderLineItems createOrderLineItems() {
        return new OrderLineItems(Arrays.asList(
                new OrderLineItem(1L, 1),
                new OrderLineItem(2L, 2)
        ));
    }

    private OrderLineItems createDuplicateOrderLineItems() {
        return new OrderLineItems(Arrays.asList(
                new OrderLineItem(1L, 2),
                new OrderLineItem(1L, 2)
        ));
    }
}
