package kitchenpos.acceptance;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.OrderTableRequest;
import org.springframework.http.MediaType;

public class TableRestAssured {
    public static ExtractableResponse<Response> createOrderTableRequest(OrderTableRequest orderTableRequest) {
        return RestAssured
                .given().log().all()
                .body(orderTableRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/tables")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> selectOrderTablesRequest() {
        return RestAssured
                .given().log().all()
                .when().get("/api/tables")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> changeOrderTableEmptyRequest(Long orderTableId, OrderTableRequest orderTableRequest) {
        return RestAssured
                .given().log().all()
                .body(orderTableRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/tables/{orderTableId}/empty", orderTableId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> changeOrderTableQuantityRequest(Long orderTableId, OrderTableRequest orderTableRequest) {
        return RestAssured
                .given().log().all()
                .body(orderTableRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/tables/{orderTableId}/number-of-guests", orderTableId)
                .then().log().all()
                .extract();
    }

}
