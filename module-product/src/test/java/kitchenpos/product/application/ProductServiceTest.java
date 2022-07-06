package kitchenpos.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import kitchenpos.ServiceTest;
import kitchenpos.product.application.behavior.ProductContextServiceBehavior;
import kitchenpos.product.domain.Product;
import kitchenpos.product.exception.InvalidProductPriceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends ServiceTest {

    @Autowired
    private ProductContextServiceBehavior productContextServiceBehavior;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품 추가")
    void 상품_추가() {
        String name = "상품1";
        int price = 1000;
        Product savedProduct = productContextServiceBehavior.상품_생성됨(name, price);
        Assertions.assertAll("추가된 상품의 정보를 확인한다"
                , () -> assertThat(savedProduct.getName()).isEqualTo(name)
                , () ->
                        assertThat(savedProduct.getPrice().intValue()).isEqualTo(price)
        );

    }

    @Test
    @DisplayName("가격이 음수인 경우 상품 추가 실패")
    void 상품_추가_실패() {
        String name = "상품1";
        int price = -1000;
        assertThatThrownBy(() -> productContextServiceBehavior.상품_생성됨(name, price))
                .isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    @DisplayName("상품 목록 조회")
    void 상품목록_조회() {
        productContextServiceBehavior.상품_생성됨("상품1", 1000);
        productContextServiceBehavior.상품_생성됨("상품2", 2000);
        List<Product> products = productService.list();

        assertThat(products).hasSize(2);
    }
}
