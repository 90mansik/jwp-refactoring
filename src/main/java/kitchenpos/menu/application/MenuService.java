package kitchenpos.menu.application;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroupRepository;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.domain.MenuProductRepository;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.menu.dto.MenuDto;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuProductRepository menuProductRepository;
    private final ProductRepository productRepository;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final MenuProductRepository menuProductRepository,
            final ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuProductRepository = menuProductRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public MenuDto create(final MenuDto menuDto) {
        Menu menu = menuDto.toMenu();
        final BigDecimal price = menu.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

        if (!menuGroupRepository.existsById(menu.getMenuGroup().getId())) {
            throw new IllegalArgumentException();
        }

        final List<MenuProduct> menuProducts = menu.getMenuProducts();

        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProduct : menuProducts) {
            final Product product = productRepository.findById(menuProduct.getProductId())
                    .orElseThrow(IllegalArgumentException::new);
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException();
        }

        return MenuDto.of(menuRepository.save(menu));
    }

    public List<MenuDto> list() {
        List<Menu> menus = menuRepository.findAll();
        return menus.stream().map(MenuDto::of).collect(toList());
    }
}
