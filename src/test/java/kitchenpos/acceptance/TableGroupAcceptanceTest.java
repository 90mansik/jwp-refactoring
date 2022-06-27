package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.helper.KitchenPosBehaviors;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.fixture.OrderTableFixtureFactory;
import kitchenpos.fixture.TableGroupFixtureFactory;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TableGroupAcceptanceTest extends AcceptanceTest {

    OrderTable table1;
    OrderTable table2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        table1 = KitchenPosBehaviors.테이블_생성됨(OrderTableFixtureFactory.createEmptyOrderTable());
        table2 = KitchenPosBehaviors.테이블_생성됨(OrderTableFixtureFactory.createEmptyOrderTable());
    }

    /**
     * Given 빈 테이블이 2개 등록되어 있다.
     * When 테이블 그룹 지정을 시도한다.
     * Then 그룹이 지정된다.
     * When 테이블 그룹을 조회한다.
     * Then 테이블 그룹이 조회된다.
     * When 테이블 그룹을 해제한다.
     * Then 테이블 그룹이 해제된다.
     */
    @Test
    @DisplayName("테이블그룹 지정, 조회, 그룹 해제 기능 인수테스트")
    void tableGroupAcceptanceTest() {
        TableGroup tableGroup = TableGroupFixtureFactory.createTableGroup(Lists.newArrayList(table1, table2));

        ExtractableResponse<Response> createResponse = KitchenPosBehaviors.테이블그룹_생성_요청(tableGroup);
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        TableGroup savedTableGroup = createResponse.as(TableGroup.class);
        ExtractableResponse<Response> deleteResponse = KitchenPosBehaviors.테이블그룹_해제_요청(savedTableGroup.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(org.springframework.http.HttpStatus.NO_CONTENT.value());
    }
}
