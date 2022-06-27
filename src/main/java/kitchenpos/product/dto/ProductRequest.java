package kitchenpos.product.dto;

import kitchenpos.product.domain.Product;

import java.math.BigDecimal;

public class ProductRequest {
    private String name;
    private int price;

    protected ProductRequest() {
    }

    private ProductRequest(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public static ProductRequest of(String name, int price) {
        return new ProductRequest(name, price);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Product toProduct() {
        return new Product(name, price);
    }
}
