package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;

import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;

public class MenuGroupRestAssured {

    public static ExtractableResponse<Response> createMenuGroupRequest(Long id, String name) {
        MenuGroup menuGroup = createMenuGroup(id, name);

        return createMenuGroupRequest(menuGroup);
    }

    public static ExtractableResponse<Response> createMenuGroupRequest(MenuGroup menuGroup) {
        return RestAssured.given().log().all()
                .body(menuGroup)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }
}
