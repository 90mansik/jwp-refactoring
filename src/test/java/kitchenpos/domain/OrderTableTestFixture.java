package kitchenpos.domain;

import kitchenpos.dto.OrderTableRequest;

public class OrderTableTestFixture {

    public static OrderTable createOrderTable(int numberOfGuests, boolean empty){
        return  OrderTable.of(numberOfGuests, empty);
    }

    public static OrderTableRequest setOrderTableRequest(int numberOfGuests, boolean empty){
        return  OrderTableRequest.of(numberOfGuests, empty);
    }


}
