package kitchenpos.fixture;

import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.dto.OrderTableRequest;
import kitchenpos.table.dto.OrderTableResponse;
import org.springframework.test.util.ReflectionTestUtils;

public class OrderTableFixtureFactory {
    private OrderTableFixtureFactory() {
    }

    public static OrderTableResponse createEmptyOrderTableResponse(Long id) {
        OrderTable orderTable = new OrderTable(0,true);
        ReflectionTestUtils.setField(orderTable,"id",id);
        return OrderTableResponse.of(orderTable);
    }

    public static OrderTableRequest createEmptyOrderTable() {
        return new OrderTableRequest(true);
    }

    public static OrderTableRequest createNotEmptyOrderTable(int numberOfGuests) {
        return new OrderTableRequest(numberOfGuests,false);
    }

    public static OrderTableRequest createParamForChangeEmptyState(boolean empty) {
        return new OrderTableRequest(empty);
    }

    public static OrderTableRequest createParamForChangeNumberOfGuests(int numberOfGuests) {
        return new OrderTableRequest(numberOfGuests);
    }
}
