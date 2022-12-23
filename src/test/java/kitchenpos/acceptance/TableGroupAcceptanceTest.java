package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.OrderTableRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static kitchenpos.acceptance.TableGroupRestAssured.groupRequest;
import static kitchenpos.acceptance.TableGroupRestAssured.unGroupRequest;
import static kitchenpos.acceptance.TableRestAssured.createOrderTableRequest;
import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static kitchenpos.domain.OrderTableTestFixture.setOrderTableRequest;
import static kitchenpos.domain.TableGroupTestFixture.createTableGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("단체 지정 관련 인수 테스트")
public class TableGroupAcceptanceTest extends AbstractAcceptanceTest {

    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private TableGroup group1;

    @BeforeEach
    public void setUp() {
        super.setUp();
        orderTable1 = createOrderTableRequest(setOrderTableRequest( 5, true)).as(OrderTable.class);
        orderTable2 = createOrderTableRequest(setOrderTableRequest(4, true)).as(OrderTable.class);
        group1 = createTableGroup(Arrays.asList(orderTable1, orderTable2));
    }

    @DisplayName("주문 테이블들에 대해 `단체를 설정한다.")
    @Test
    void createTableGroup1() {
        // when
        ExtractableResponse<Response> response = groupRequest(group1);
        // then
        단체_지정됨(response);
    }

    @DisplayName("등록된 테이블 단체 지정을 해제한다.")
    @Test
    void ungroup() {
        // given
        TableGroup tableGroup = groupRequest(group1).as(TableGroup.class);
        // when
        ExtractableResponse<Response> response = unGroupRequest(tableGroup.getId());
        // then
        단체_해제됨(response);
    }

    private static void 단체_지정됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private static void 단체_해제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
