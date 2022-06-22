package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("테이블 관련 기능")
public class TableAcceptanceTest extends AcceptanceTest {

    @DisplayName("주문 기능을 관리한다.")
    @TestFactory
    Stream<DynamicTest> manageMenu() {
        return Stream.of(
                dynamicTest("주문 테이블을 등록 한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 주문_테이블_등록_요청(true, 3);
                    // then
                    주문_테이블_등록됨(response);
                }),
                dynamicTest("주문 테이블을 조회한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 주문_테이블_조회_요청();
                    // then
                    주문_테이블_조회됨(response);
                }),
                dynamicTest("주문 테이블 상태를 변경한다..", () -> {
                    // given
                    ExtractableResponse<Response> response = 주문_테이블_등록_요청(true, 3);
                    OrderTable createdOrderTable = response.as(OrderTable.class);
                    // when
                    response = 주문_테이블_상태_변경_요청(createdOrderTable, false);
                    // then
                    주문_테이블_상태_변경됨(response, false);
                }),
                dynamicTest("주문 테이블 손님수를 변경한다.", () -> {
                    // given
                    ExtractableResponse<Response> response = 주문_테이블_등록_요청(false, 3);
                    OrderTable createdOrderTable = response.as(OrderTable.class);
                    // when
                    response = 주문_테이블_손님수_변경_요청(createdOrderTable, 4);
                    // then
                    주문_테이블_손님수_변경됨(response, 4);
                })

        );
    }

    public static ExtractableResponse<Response> 주문_테이블_등록_요청(boolean empty, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);

        return RestAssured
                .given().log().all()
                .body(orderTable)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/tables")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_조회_요청() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/tables")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_상태_변경_요청(OrderTable createOrderTable, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(empty);
        return RestAssured
                .given().log().all()
                .body(orderTable)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/tables/{orderTableId}/empty", createOrderTable.getId())
                .then().log().all().extract();
    }


    public static ExtractableResponse<Response> 주문_테이블_손님수_변경_요청(OrderTable createOrderTable, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return RestAssured
                .given().log().all()
                .body(orderTable)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/tables/{orderTableId}/number-of-guests", createOrderTable.getId())
                .then().log().all().extract();
    }

    public static void 주문_테이블_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 주문_테이블_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_테이블_상태_변경됨(ExtractableResponse<Response> response, boolean empty) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(OrderTable.class).isEmpty()).isEqualTo(empty);
    }

    public static void 주문_테이블_손님수_변경됨(ExtractableResponse<Response> response, int numberOfGuests) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(OrderTable.class).getNumberOfGuests()).isEqualTo(numberOfGuests);
    }
}
