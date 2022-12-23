package kitchenpos.application;

import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    private Product honeyCombo;
    private Product redWing;

    @BeforeEach
    void setUp() {
        honeyCombo = createProduct(1L, "honeyCombo", BigDecimal.valueOf(18000));
        redWing = createProduct(2L, "redWing", BigDecimal.valueOf(19000));
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void 상품_생성() {
        // given
        when(productDao.save(honeyCombo)).thenReturn(honeyCombo);
        // when
        Product result = productService.create(honeyCombo);
        // then
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(honeyCombo.getName()),
                () -> assertThat(result.getPrice()).isEqualTo(honeyCombo.getPrice())
        );
    }

    @DisplayName("상품의 전체 목록을 조회한다.")
    @Test
    void 상품_전체_목록_조회() {
        // given
        List<Product> products = Arrays.asList(honeyCombo, redWing);
        when(productDao.findAll()).thenReturn(products);
        // when
        List<Product> findProducts = productService.list();
        // then
        assertAll(
                () -> assertThat(findProducts).hasSize(products.size()),
                () -> assertThat(findProducts).containsExactly(honeyCombo, redWing)
        );
    }

    @DisplayName("가격이 비어있는 상품 등록은 예외 발생")
    @Test
    void 상품_생성_예외_테스트1() {
        // given
        Product product = createProduct(3L, "coke", BigDecimal.valueOf(-1));
        // when
        Assertions.assertThatThrownBy(
                () -> productService.create(product)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격에 null이 들어가있을 시 예외 발생")
    @Test
    void 상품_생성_예외_테스트2() {
        // given
        Product product = createProduct(3L, "coke", null);
        // when
        Assertions.assertThatThrownBy(
                () -> productService.create(product)
        ).isInstanceOf(IllegalArgumentException.class);
    }

}
