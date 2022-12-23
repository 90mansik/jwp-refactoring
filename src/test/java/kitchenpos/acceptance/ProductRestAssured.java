package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static kitchenpos.domain.ProductTestFixture.createProduct;

public class ProductRestAssured {

    public static ExtractableResponse<Response> createProductRequest(Long id, String name, BigDecimal price) {
        Product product = createProduct(id, name, price);

        return createProductRequest(product);
    }

    public static ExtractableResponse<Response> createProductRequest(Product product) {
        return RestAssured.given().log().all()
                .body(product)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/products")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> selectProductsRequest() {
        return RestAssured.given().log().all()
                .when().get("/api/products")
                .then().log().all()
                .extract();
    }

}
