package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.acceptance.ProductRestAssured.상품_목록_조회_요청;
import static kitchenpos.acceptance.ProductRestAssured.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 관련 인수 테스트")
public class ProductAcceptanceTest extends AbstractAcceptanceTest {

    @DisplayName("상품을 생성")
    @Test
    void createProduct() {
        //when
        ExtractableResponse<Response> response = 상품_생성_요청("허니콤보", BigDecimal.valueOf(18000));
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("상품 목록을 조회")
    @Test
    void selectProducts() {
        // given
        ExtractableResponse<Response> response1 = 상품_생성_요청("허니콤보", BigDecimal.valueOf(18000));
        ExtractableResponse<Response> response2 = 상품_생성_요청("레드윙", BigDecimal.valueOf(190000));
        // when
        ExtractableResponse<Response> result = 상품_목록_조회_요청();

        List<Long> expectedProductIds = getExpectedProductIds(Arrays.asList(response1, response2));

        List<Long> resultProductIds = getResultProductIds(result);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultProductIds).containsAll(expectedProductIds);
    }


    private static List<Long> getExpectedProductIds(List<ExtractableResponse<Response>> responses) {
        return responses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[3]))
                .collect(Collectors.toList());
    }

    private static List<Long> getResultProductIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", Product.class).stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }

}
