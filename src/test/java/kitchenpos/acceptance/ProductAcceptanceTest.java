package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.dto.ProductRequest;
import kitchenpos.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.utils.RestAssuredMethods.get;
import static kitchenpos.utils.RestAssuredMethods.post;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 관련 기능")
public class ProductAcceptanceTest extends AcceptanceTest {
    private ProductRequest 김치찌개_productRequest;
    private ProductRequest 공기밥_productRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        김치찌개_productRequest = ProductRequest.of("김치찌개", 8000);
        공기밥_productRequest = ProductRequest.of("공기밥", 1000);
    }

    /**
     * Feature: 상품 관련 기능
     *
     *   Scenario: 상품을 관리
     *     When 김치찌개 상품 등록 요청
     *     Then 김치찌개 상품 등록됨
     *     When 공기밥 상품 등록 요청
     *     Then 공기밥 상품 등록됨
     *     When 상품 조회 요청
     *     Then 김치찌개, 공기밥 상품 조회됨
     */
    @DisplayName("상품을 관리한다")
    @Test
    void 상품_관리_정상_시나리오() {
        ExtractableResponse<Response> 김치찌개_등록 = 상품_등록_요청(김치찌개_productRequest);
        상품_등록됨(김치찌개_등록);

        ExtractableResponse<Response> 공기밥_등록 = 상품_등록_요청(공기밥_productRequest);
        상품_등록됨(공기밥_등록);

        ExtractableResponse<Response> 상품목록_조회 = 상품_목록_조회_요청();
        상품_목록_응답됨(상품목록_조회);
        상품_목록_포함됨(상품목록_조회, Arrays.asList(김치찌개_등록, 공기밥_등록));
    }

    /**
     * Feature: 상품 관련 기능
     *
     *   Scenario: 상품을 관리 실패
     *     When 가격 0 미만 상품 등록 요청
     *     Then 상품 등록 실패함
     */
    @DisplayName("상품을 관리에 실패한다")
    @Test
    void 상품_관리_비정상_시나리오() {
        ProductRequest 비정상_productRequest = ProductRequest.of("비정상", -8000);
        ExtractableResponse<Response> 비정상_등록 = 상품_등록_요청(김치찌개_productRequest);
        상품_등록_실패됨(비정상_등록);
    }

    public static ExtractableResponse<Response> 상품_등록_요청(ProductRequest params) {
        return post("/api/products", params);
    }

    public static ExtractableResponse<Response> 상품_목록_조회_요청() {
        return get("/api/products");
    }

    public static void 상품_등록됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 상품_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 상품_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedProductIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultProductIds = response.jsonPath().getList(".", ProductResponse.class).stream()
                .map(ProductResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultProductIds).containsAll(expectedProductIds);
    }

    public static void 상품_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 상품_등록되어_있음(String name, int price) {
        return 상품_등록_요청(ProductRequest.of(name, price));
    }
}
