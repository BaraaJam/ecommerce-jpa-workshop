package se.lexicon.ecommercejpaworkshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.ecommercejpaworkshop.dto.ProductRequest;
import se.lexicon.ecommercejpaworkshop.dto.ProductResponse;
import se.lexicon.ecommercejpaworkshop.entity.Category;
import se.lexicon.ecommercejpaworkshop.entity.Product;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.ProductMapper;
import se.lexicon.ecommercejpaworkshop.repository.CategoryRepository;
import se.lexicon.ecommercejpaworkshop.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        // 1. INPUT: Validate arguments
        if (request == null) {
            throw new IllegalArgumentException("ProductRequest cannot be null");
        }

        // 2. PROCESS: Validate that the category exists in the database
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        // 3. PERSIST: Map DTO + Category -> Entity -> Save to DB
        Product product = productMapper.toEntity(request, category);
        Product savedProduct = productRepository.save(product);

        // 4. OUTPUT: Map Entity -> Response DTO
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        // 1. Fetch all products from repository
        List<Product> products = productRepository.findAll();

        // 2. Map list of entities to list of response DTOs
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String name) {
        // 1. INPUT: Validate search keyword
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        // 2. PROCESS: Find products containing keyword (case-insensitive)
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);

        // 3. OUTPUT: Map entities to response DTOs
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
