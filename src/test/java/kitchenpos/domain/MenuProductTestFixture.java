package kitchenpos.domain;

public class MenuProductTestFixture {
    public static MenuProduct createMenuProduct(Long seq, Long menId, Long productId, long quantity){
        return MenuProduct.of(seq, menId, productId, quantity);
    }
}
