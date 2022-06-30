package kitchenpos.ui;

import static kitchenpos.utils.generator.OrderTableFixtureGenerator.비어있지_않은_주문_테이블_생성_요청;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import kitchenpos.domain.OrderTable;
import kitchenpos.utils.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("API:Table")
public class TableRestControllerTest extends BaseTest {

    public static final String TABLE_API_BASE_URL = "/api/tables";

    @Test
    @DisplayName("주문 테이블을 생성한다.")
    public void createOrderTable() throws Exception {
        // When
        ResultActions resultActions = mockMvcUtil.post(비어있지_않은_주문_테이블_생성_요청());

        // Then
        resultActions.andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("주문 테이블 목록을 조회한다.")
    public void getAllOrderTables() throws Exception {
        // When
        ResultActions resultActions = mockMvcUtil.get(TABLE_API_BASE_URL);

        // Then
        resultActions
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * @Given 하나의 주문 테이블을 생성하고
     * @When 주문 테이블의 사용 가능 상태를 수정하면
     * @Then 사용 가능 상태가 수정 된다.
     */
    @ParameterizedTest(name = "case[{index}] : {0} => {1}")
    @MethodSource
    @DisplayName("주문 테이블의 사용 가능 여부를 수정한다.")
    public void updateNumberOfGuests(final boolean givenEmpty, final String givenDescription) throws Exception {
        // Given
        final String updateTableEmptyUrlTemplate = TABLE_API_BASE_URL.concat("/{orderTableId}/empty");
        OrderTable savedOrderTable = mockMvcUtil.as(mockMvcUtil.post(비어있지_않은_주문_테이블_생성_요청()), OrderTable.class);

        OrderTable updateOrderTableEmptyRequest = new OrderTable();
        updateOrderTableEmptyRequest.setEmpty(givenEmpty);

        // When
        ResultActions resultActions = mockMvcUtil.put(
            updateTableEmptyUrlTemplate,
            updateOrderTableEmptyRequest,
            savedOrderTable.getId()
        );

        // Then
        resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedOrderTable.getId()))
            .andExpect(jsonPath("$.empty").value(givenEmpty))
            .andExpect(jsonPath("$.numberOfGuests").value(savedOrderTable.getNumberOfGuests()));
    }

    private static Stream<Arguments> updateNumberOfGuests() {
        return Stream.of(
            Arguments.of(true, "테이블을 사용 가능 상태로 수정한다."),
            Arguments.of(false, "테이블을 사용 불가능 상태로 수정한다.")
        );
    }

    /**
     * @Given 하나의 주문 테이블을 생성하고
     * @When 주문 테이블의 객수를 수정하면
     * @Then 주문 테이블의 객수가 수정 된다.
     */
    @Test
    @DisplayName("테이블의 객수를 수정한다.")
    public void updateTableUsingStatus() throws Exception {
        // Given
        final String updateNumberOfGuestApiUrlTemplate = TABLE_API_BASE_URL.concat("/{orderTableId}/number-of-guests");
        final OrderTable savedOrderTable = mockMvcUtil.as(mockMvcUtil.post(비어있지_않은_주문_테이블_생성_요청()), OrderTable.class);

        final int newNumberOfGuests = 4;
        OrderTable updateNumberOfGuestsRequest = new OrderTable();
        updateNumberOfGuestsRequest.setNumberOfGuests(newNumberOfGuests);

        // When
        ResultActions resultActions = mockMvcUtil.put(
            updateNumberOfGuestApiUrlTemplate,
            updateNumberOfGuestsRequest,
            savedOrderTable.getId()
        );

        // Then
        resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedOrderTable.getId()))
            .andExpect(jsonPath("$.empty").value(savedOrderTable.isEmpty()))
            .andExpect(jsonPath("$.numberOfGuests").value(newNumberOfGuests));
    }
}
