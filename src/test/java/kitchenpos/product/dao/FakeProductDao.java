package kitchenpos.product.dao;

import static kitchenpos.ServiceTestFactory.HONEY_COMBO;
import static kitchenpos.ServiceTestFactory.RED_COMBO;

import kitchenpos.product.domain.Product;
import kitchenpos.product.infrastructure.ProductDao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FakeProductDao implements ProductDao {
    @Override
    public Product save(Product entity) {
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Arrays.asList(HONEY_COMBO, RED_COMBO).stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return Arrays.asList(HONEY_COMBO, RED_COMBO);
    }
}
