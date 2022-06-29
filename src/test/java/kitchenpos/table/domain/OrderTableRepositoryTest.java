package kitchenpos.table.domain;

import java.util.List;
import kitchenpos.JpaRepositoryTest;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTableRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

class OrderTableRepositoryTest extends JpaRepositoryTest {
    @Autowired
    private OrderTableRepository orderTableRepository;

    OrderTable orderTable1;
    OrderTable orderTable2;

    @BeforeEach
    void setUp(){
        orderTable1 = new OrderTable(0,true);
        orderTable2 = new OrderTable(0,true);
        orderTableRepository.saveAll(Lists.newArrayList(orderTable1,orderTable2));
    }


    @Test
    @DisplayName("아이디 여러개로 주문테이블 찾기")
    void finaAllByIdInTest(){
       List<OrderTable> orderTableList =  orderTableRepository.findAllByIdIn(Lists.newArrayList(orderTable1.getId(),orderTable2.getId()));
       assertThat(orderTableList).hasSize(2);
    }


    @Test
    @DisplayName("유효하지 않은 아이디의 경우 빈 array를 리턴")
    void finaAllByIdInTestWithNotValidId(){
        List<OrderTable> orderTableList =  orderTableRepository.findAllByIdIn(Lists.newArrayList(-1L,-2L));
        assertThat(orderTableList).hasSize(0).isNotNull();
    }
}
