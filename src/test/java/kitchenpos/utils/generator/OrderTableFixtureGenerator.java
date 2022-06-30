package kitchenpos.utils.generator;

import static kitchenpos.ui.TableRestControllerTest.TABLE_API_BASE_URL;
import static kitchenpos.utils.MockMvcUtil.postRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import kitchenpos.domain.OrderTable;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class OrderTableFixtureGenerator {

    private static int NUMBER_OF_GUESTS = 0;
    private static boolean EMPTY = false;
    private static int COUNTER = 0;

    public static OrderTable generateEmptyOrderTable() {
        COUNTER++;
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(NUMBER_OF_GUESTS + COUNTER);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable generateNotEmptyOrderTable() {
        OrderTable orderTable = generateEmptyOrderTable();
        orderTable.setEmpty(EMPTY);
        return orderTable;
    }

    public static List<OrderTable> generateOrderTables(int count) {
        List<OrderTable> orderTables = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orderTables.add(generateNotEmptyOrderTable());
        }
        return orderTables;
    }

    public static MockHttpServletRequestBuilder 비어있지_않은_주문_테이블_생성_요청() throws Exception {
        return postRequestBuilder(TABLE_API_BASE_URL, generateNotEmptyOrderTable());
    }

    public static MockHttpServletRequestBuilder 비어있는_주문_테이블_생성_요청() throws Exception {
        return postRequestBuilder(TABLE_API_BASE_URL, generateEmptyOrderTable());
    }
}
