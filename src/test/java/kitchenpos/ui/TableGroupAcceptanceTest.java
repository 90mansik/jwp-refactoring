package kitchenpos.ui;

import static kitchenpos.ui.MenuAcceptanceTestFixture.메뉴_생성_되어있음;
import static kitchenpos.ui.MenuGroupAcceptanceTestFixture.메뉴_그룹_생성되어있음;
import static kitchenpos.ui.OrderAcceptanceTestFixture.주문_등록_되어있음;
import static kitchenpos.ui.ProductAcceptanceTestFixture.상품_생성_되어있음;
import static kitchenpos.ui.TableAcceptanceTestFixture.빈_테이블_여부_수정_요청;
import static kitchenpos.ui.TableAcceptanceTestFixture.주문_테이블_목록;
import static kitchenpos.ui.TableAcceptanceTestFixture.주문_테이블_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("단쳬 지정 인수 테스트")
public class TableGroupAcceptanceTest extends TableGroupAcceptanceTestFixture {
    /**
     *   When 단체 지정을 생성하면
     *   Then 단체 지정이 생성된다
     */
    @DisplayName("단체 지정을 생성한다")
    @Test
    void 단체_지정_생성() {
        // When
        ExtractableResponse<Response> response = 단체_지정_생성_요청(단체_1);

        // Then
        단체_지정_생성됨(response);

        // Then
        TableGroup 생성된_단체_지정 = 단체_지정_정보(response);
        assertAll(
                () -> assertThat(생성된_단체_지정.getId()).isNotNull(),
                () -> 생성된_단체_지정.getOrderTables()
                        .forEach((테이블 -> assertThat(테이블.getTableGroupId()).isEqualTo(생성된_단체_지정.getId()))),
                () -> 생성된_단체_지정.getOrderTables().forEach((테이블 -> assertThat(테이블.isEmpty()).isEqualTo(false)))
        );
    }

    /**
     *   Given 단체 지정을 생성하고
     *   When  단체 지정 해제하면
     *   Then  단체 지정 해제된다
     */
    @DisplayName("단체 지정 해제한다")
    @Test
    void 단체_지정_상태_수정() {
        // Given
        TableGroup 생성된_단체_지정 = 단체_지정_생성_되어있음(단체_1);

        // When
        ExtractableResponse<Response> response = 단체_지정_해제_요청(생성된_단체_지정.getId());

        // Then
        단체_지정_해제됨(response);

        // Then
        List<OrderTable> 조회된_주문_테이블_목록 = 주문_테이블_목록(주문_테이블_조회_요청());
        List<OrderTable> 단체_지정되었던_주문_테이블_목록 = 조회된_주문_테이블_목록.stream()
                .filter(주문_테이블 -> 주문_테이블_목록에_포함_확인(주문_테이블_목록, 주문_테이블.getId()))
                .collect(Collectors.toList());
        assertAll(
                () -> 단체_지정되었던_주문_테이블_목록.forEach((테이블 -> assertThat(테이블.getTableGroupId()).isNull())),
                () -> 단체_지정되었던_주문_테이블_목록.forEach((테이블 -> assertThat(테이블.isEmpty()).isEqualTo(false)))
        );
    }

    /**
     *   When 생성되지 않은 주문 테이블에 대해 단체 지정하면
     *   Then 단체 지정이 생성되지 않는다
     */
    @DisplayName("생성되지 않은 주문 테이블에 대해 단체 지정 요청")
    @Test
    void 생성되지_않은_주문_테이블_단체_지정() {
        // When
        OrderTable 생성안된_주문_테이블_1 = new OrderTable(null, null, 0, true);
        OrderTable 생성안된_주문_테이블_2 = new OrderTable(null, null, 0, true);
        List<OrderTable> 생성안된_주문_테이블_목록 = Arrays.asList(생성안된_주문_테이블_1, 생성안된_주문_테이블_2);
        TableGroup 단체_3 = new TableGroup(null, null, 생성안된_주문_테이블_목록);
        ExtractableResponse<Response> response = 단체_지정_생성_요청(단체_3);

        // Then
        단체_지정_생성되지_않음(response);
    }

    /**
     *   Given 비어있지 않은 주문 테이블을 생성하고
     *   When  단체 지정 생성 요청하면
     *   Then  단체 지정이 생성되지 않는다
     */
    @DisplayName("비어있지 않은 주문 테이블에 대해 단체 지정 요청")
    @Test
    void 비어있지_않은_주문_테이블_단체_지정() {
        // When
        주문_테이블_1.setEmpty(false);
        빈_테이블_여부_수정_요청(주문_테이블_1.getId(), 주문_테이블_1);
        TableGroup 단체_3 = new TableGroup(null, null, 주문_테이블_목록);
        ExtractableResponse<Response> response = 단체_지정_생성_요청(단체_3);

        // Then
        단체_지정_생성되지_않음(response);
    }

    /**
     *   Given 두 주문 테이블에 대해 단체 지정 생성하고
     *   When  단체 지정 생성 요청하면
     *   Then  단체 지정이 생성되지 않는다
     */
    @DisplayName("이미 단체 지정된 주문 테이블에 대해 단체 지정 요청")
    @Test
    void 단체_지정_중복_요청() {
        // Given
        단체_지정_생성_요청(단체_1);

        // When
        TableGroup 단체_3 = new TableGroup(null, null, 주문_테이블_목록);
        ExtractableResponse<Response> response = 단체_지정_생성_요청(단체_3);

        // Then
        단체_지정_생성되지_않음(response);
    }

    /**
     *   Given 두 주문 테이블에 대해 단체 지정 생성 후
     *   And   주문 등록하고
     *   When  단체 지정 생성 요청하면
     *   Then  단체 지정이 생성되지 않는다
     */
    @DisplayName("주문이 등록된 주문 테이블에 대해 단체 지정 요청")
    @Test
    void 주문_등록된_주문테이블_단체_지정() {
        // Given
        TableGroup 생성된_단체_지정 = 단체_지정_정보(단체_지정_생성_요청(단체_1));

        MenuGroup 세트 = 메뉴_그룹_생성되어있음(new MenuGroup(null, "세트"));
        Product 떡볶이 = 상품_생성_되어있음(new Product(null, "떡볶이", BigDecimal.valueOf(4500)));
        Product 튀김 = 상품_생성_되어있음(new Product(null, "튀김", BigDecimal.valueOf(2500)));
        Product 순대 = 상품_생성_되어있음(new Product(null, "순대", BigDecimal.valueOf(4000)));

        MenuProduct 떡튀순_상품_떡볶이 = new MenuProduct(null, null, 떡볶이.getId(), 1);
        MenuProduct 떡튀순_상품_튀김 = new MenuProduct(null, null, 튀김.getId(), 1);
        MenuProduct 떡튀순_상품_순대 = new MenuProduct(null, null, 순대.getId(), 1);
        MenuProduct 떡튀순_곱배기_상품_떡볶이 = new MenuProduct(null, null, 떡볶이.getId(), 2);

        List<MenuProduct> 떡튀순_상품_목록 = Arrays.asList(떡튀순_상품_떡볶이, 떡튀순_상품_튀김, 떡튀순_상품_순대);
        List<MenuProduct> 떡튀순_곱배기_상품_목록 = Arrays.asList(떡튀순_곱배기_상품_떡볶이, 떡튀순_상품_튀김, 떡튀순_상품_순대);

        Menu 떡튀순 = 메뉴_생성_되어있음(new Menu(null, "떡튀순", BigDecimal.valueOf(10000), 세트.getId(), 떡튀순_상품_목록));
        Menu 떡튀순_곱배기 = 메뉴_생성_되어있음(new Menu(null, "떡튀순_곱배기", BigDecimal.valueOf(10000), 세트.getId(), 떡튀순_곱배기_상품_목록));

        OrderLineItem 주문_아이템_1 = new OrderLineItem(null, null, 떡튀순.getId(), 2);
        OrderLineItem 주문_아이템_2 = new OrderLineItem(null, null, 떡튀순_곱배기.getId(), 1);
        List<OrderLineItem> 주문_아이템_목록 = Arrays.asList(주문_아이템_1, 주문_아이템_2);
        Order 주문 = new Order(null, 주문_테이블_1.getId(), OrderStatus.COOKING.name(), null, 주문_아이템_목록);
        주문_등록_되어있음(주문);

        // When
        ExtractableResponse<Response> response = 단체_지정_해제_요청(생성된_단체_지정.getId());

        // Then
        단체_지정_해제되지_않음(response);
    }
}
