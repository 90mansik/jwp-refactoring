package kitchenpos.common.domain;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    COOKING, MEAL, COMPLETION;

    public static final List<OrderStatus> UNCOMPLETED_STATUSES =
            Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL);

}
