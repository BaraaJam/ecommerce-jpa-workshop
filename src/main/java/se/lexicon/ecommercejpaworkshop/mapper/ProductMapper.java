package se.lexicon.ecommercejpaworkshop.mapper;

import org.springframework.stereotype.Component;
import se.lexicon.ecommercejpaworkshop.dto.ProductRequest;
import se.lexicon.ecommercejpaworkshop.dto.ProductResponse;
import se.lexicon.ecommercejpaworkshop.entity.Category;
import se.lexicon.ecommercejpaworkshop.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }

        return new Product(
                request.name(),
                request.price(),
                category
        );
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        String categoryName = (product.getCategory() != null)
                ? product.getCategory().getName()
                : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                categoryName
        );
    }
}