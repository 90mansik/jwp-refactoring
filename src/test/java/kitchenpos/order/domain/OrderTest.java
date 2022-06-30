package kitchenpos.order.domain;

import kitchenpos.exception.IllegalOrderException;
import kitchenpos.exception.IllegalOrderLineItemException;
import kitchenpos.exception.IllegalOrderTableException;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menuGroup.domain.MenuGroup;
import kitchenpos.orderTable.domain.OrderTable;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("주문 도메인 테스트")
class OrderTest {
    private MenuGroup menuGroup_한식;
    private Product product_김치찌개;
    private Menu menu;
    private MenuProduct menuProduct_김치찌개;


    @BeforeEach
    public void setUp(){
        menuGroup_한식 = new MenuGroup(1L, "한식");
        product_김치찌개 = new Product(1L, "김치찌개", 8000);
        menu = new Menu(1L, "김치찌개", 8000, menuGroup_한식);
        menuProduct_김치찌개 = new MenuProduct(1L, menu, product_김치찌개, 1);
        menu.registerMenuProducts(Arrays.asList(menuProduct_김치찌개));
    }

    @DisplayName("주문을 생성한다")
    @Test
    void Order_생성() {
        OrderTable 테이블_1 = new OrderTable(1L, null, 3, false);
        Order order = new Order(1L, 테이블_1, LocalDateTime.now());
        OrderLineItem orderLineItem_김치찌개 = new OrderLineItem(1L, order, menu, 1);
        order.registerOrderLineItems(Arrays.asList(orderLineItem_김치찌개));

        assertAll(
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING),
                () -> assertThat(order.getOrderLineItems()).containsExactly(orderLineItem_김치찌개),
                () -> assertThat(orderLineItem_김치찌개.getOrder()).isEqualTo(order)
        );
    }

    @DisplayName("주문항목의 메뉴는 1개 이상이어야 한다")
    @Test
    void Order_주문항목_메뉴_1개_이상_검증(){
        OrderTable 테이블_1 = new OrderTable(1L, null, 3, false);
        Order order = new Order(1L, 테이블_1, LocalDateTime.now());
        OrderLineItem orderLineItem_김치찌개 = new OrderLineItem(order, menu, 1);

        assertThrows(IllegalOrderLineItemException.class,
                () -> order.registerOrderLineItems(new ArrayList<>()));
    }

    @DisplayName("주문항목의 메뉴는 중복될 수 없다")
    @Test
    void Order_주문항목_메뉴_중복_검증(){
        OrderTable 테이블_1 = new OrderTable(1L, null, 3, false);
        Order order = new Order(1L, 테이블_1, LocalDateTime.now());
        OrderLineItem orderLineItem_김치찌개 = new OrderLineItem(order, menu, 1);

        assertThrows(IllegalOrderLineItemException.class,
                () ->order.registerOrderLineItems(Arrays.asList(orderLineItem_김치찌개, orderLineItem_김치찌개)));
    }

    @DisplayName("주문테이블은 비어있을 수 없다")
    @Test
    void Order_테이블_Empty_검증(){
        OrderTable 테이블_1 = new OrderTable(1L, null, 0, true);

        assertThrows(IllegalOrderTableException.class,
                () ->new Order(1L, 테이블_1, LocalDateTime.now()));
    }

    @DisplayName("주문의 상태를 변경할 수 있다")
    @Test
    void Order_주문상태_변경(){
        OrderTable 테이블_1 = new OrderTable(1L, null, 3, false);
        Order order = new Order(1L, 테이블_1, LocalDateTime.now());
        order.changeStatus(OrderStatus.COMPLETION);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETION);
    }

    @DisplayName("주문의 상태가 COMPLETION이면 주문의 상태를 변경할 수 있다")
    @Test
    void Order_주문상태_변경_COMPLETION_검증(){
        OrderTable 테이블_1 = new OrderTable(1L, null, 3, false);
        Order order = new Order(1L, 테이블_1, LocalDateTime.now());
        order.changeStatus(OrderStatus.COMPLETION);

        assertThrows(IllegalOrderException.class, () -> order.changeStatus(OrderStatus.COOKING));
    }
}