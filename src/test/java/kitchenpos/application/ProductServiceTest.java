package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.dto.ProductRequest;
import kitchenpos.dto.ProductResponse;
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
import static kitchenpos.domain.ProductTestFixture.createProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product frenchFries;
    private Product cola;

    @BeforeEach
    void setUp() {
        frenchFries = createProduct(1L, "frenchFries", BigDecimal.valueOf(18000));
        cola = createProduct(2L, "cola", BigDecimal.valueOf(19000));
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void createProductTest() {

        // given
        ProductRequest productRequest = createProductRequest(frenchFries.getName(), frenchFries.getPrice());
        Product product = productRequest.toProduct();
        when(productRepository.save(product)).thenReturn(frenchFries);

        // when
        ProductResponse productResponse = productService.create(productRequest);
        // then
        assertAll(
                () -> assertThat(productResponse.getId()).isNotNull(),
                () -> assertThat(productResponse.getName()).isEqualTo(frenchFries.getName().value()),
                () -> assertThat(productResponse.getPrice()).isEqualTo(frenchFries.getPrice().value())
        );

    }

    @DisplayName("상품의 전체 목록을 조회한다.")
    @Test
    void 상품_전체_목록_조회() {
        // given
        List<Product> products = Arrays.asList(frenchFries, cola);
        when(productRepository.findAll()).thenReturn(products);
        // when
        List<ProductResponse> findProducts = productService.list();
        // then
        assertAll(
                () -> assertThat(findProducts).hasSize(products.size()),
                () -> assertThat(findProducts.stream().map(ProductResponse::getName))
                        .containsExactly(
                                frenchFries.getName().value(),
                                cola.getName().value()
                        )
        );
    }

    @DisplayName("가격이 0원 미만이면 예외 발생")
    @Test
    void isValidPriceIsNegative() {
        // given
        ProductRequest productRequest = createProductRequest(cola.getName().value(), BigDecimal.valueOf(-1000));
        // when
        Assertions.assertThatThrownBy(
                () -> productService.create(productRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격에 null이 들어가있을 시 예외 발생")
    @Test
    void isValidPriceIsNull() {
        // given
        ProductRequest productRequest = createProductRequest(cola.getName().value(), null);
        // when
        Assertions.assertThatThrownBy(
                () -> productService.create(productRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

}
