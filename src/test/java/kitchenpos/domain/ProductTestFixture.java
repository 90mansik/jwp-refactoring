package kitchenpos.domain;

import kitchenpos.dto.ProductRequest;

import java.math.BigDecimal;

public class ProductTestFixture {

    public static Product createProduct(Long id, String name, BigDecimal price){
        return Product.of(id, name, price);
    }

    public static Product createProduct(String name, BigDecimal price){
        return Product.of(name, price);
    }

    public static ProductRequest createProductRequest(String name, BigDecimal price){
        return new ProductRequest(name, price);
    }

    public static ProductRequest createProductRequest(Name name, Price price){
        return new ProductRequest(name.value(), price.value());
    }
}
