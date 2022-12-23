package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.dto.MenuGroupRequest;
import org.springframework.http.MediaType;

import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;

public class MenuGroupRestAssured {

    public static ExtractableResponse<Response> createMenuGroupRequest(MenuGroupRequest menuGroupRequest) {
        return RestAssured.given().log().all()
                .body(menuGroupRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }
}
