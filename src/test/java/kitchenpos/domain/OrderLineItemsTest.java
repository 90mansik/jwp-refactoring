package kitchenpos.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static kitchenpos.domain.OrderTestFixture.createOrderLineItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 항목 집합 관련 도메인 테스트")
public class OrderLineItemsTest {

    private OrderLineItem cheeseBurgerOrderLineItem;
    private OrderLineItem frenchFriesOrderLineItem;

    @BeforeEach
    void setup() {
        cheeseBurgerOrderLineItem = createOrderLineItem(1L, 1L, 2L, 2);
        frenchFriesOrderLineItem = createOrderLineItem(2L, 1L, 3L, 1L);
    }

    @DisplayName("주문 항목 집합을 생성한다.")
    @Test
    void createOrderLineItems() {
        // when
        OrderLineItems orderLineItems = OrderLineItems.from(Arrays.asList(cheeseBurgerOrderLineItem, frenchFriesOrderLineItem));
        // then
        assertAll(() -> assertThat(orderLineItems.values()).hasSize(2), () -> assertThat(orderLineItems.values()).containsExactly(cheeseBurgerOrderLineItem, frenchFriesOrderLineItem));
    }

}
