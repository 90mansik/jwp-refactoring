package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class OrderLineItems {

    private List<OrderLineItem> orderLineItems = new ArrayList<>();

    protected OrderLineItems() {
    }

    private OrderLineItems(List<OrderLineItem> orderLineItems) {
        validateOrderLineItemsIsEmpty(orderLineItems);
        this.orderLineItems = new ArrayList<>(orderLineItems);
    }

    public static OrderLineItems from(List<OrderLineItem> orderLineItems) {
        return new OrderLineItems(orderLineItems);
    }

    private void validateOrderLineItemsIsEmpty(List<OrderLineItem> orderLineItems) {
        if (orderLineItems.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.ORDER_LINE_ITEMS_IS_EMPTY.getMessage());
        }
    }

    public List<OrderLineItem> values() {
        return Collections.unmodifiableList(orderLineItems);
    }

}
