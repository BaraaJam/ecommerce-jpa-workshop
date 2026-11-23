package se.lexicon.ecommercejpaworkshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.ecommercejpaworkshop.dto.ProductRequest;
import se.lexicon.ecommercejpaworkshop.dto.ProductResponse;
import se.lexicon.ecommercejpaworkshop.entity.Category;
import se.lexicon.ecommercejpaworkshop.entity.Product;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.ProductMapper;
import se.lexicon.ecommercejpaworkshop.repository.CategoryRepository;
import se.lexicon.ecommercejpaworkshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category electronicsCategory;
    private Product laptopProduct;
    private ProductRequest validProductRequest;

    @BeforeEach
    void setUp() {
        electronicsCategory = new Category("Electronics");
        electronicsCategory.setId(1L);

        laptopProduct = new Product("Gaming Laptop", new BigDecimal("1299.99"), electronicsCategory);
        laptopProduct.setId(10L);

        validProductRequest = new ProductRequest("Gaming Laptop", new BigDecimal("1299.99"), 1L);
    }

    // ==========================================
    // Create Tests
    // ==========================================

    @Test
    @DisplayName("create: Success - Should save and return ProductResponse")
    void create_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            toSave.setId(10L);
            return toSave;
        });

        // Act
        ProductResponse response = productService.create(validProductRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Gaming Laptop");
        assertThat(response.price()).isEqualByComparingTo("1299.99");
        assertThat(response.categoryName()).isEqualTo("Electronics");

        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("create: Category Not Found - Should throw ResourceNotFoundException")
    void create_CategoryNotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.create(validProductRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 1");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("create: Null Request - Should throw IllegalArgumentException")
    void create_NullRequest_ThrowsException() {
        assertThatThrownBy(() -> productService.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ==========================================
    // Find All Tests
    // ==========================================

    @Test
    @DisplayName("findAll: Success - Should return list of ProductResponses")
    void findAll_Success() {
        // Arrange
        Product phone = new Product("Smartphone", new BigDecimal("799.00"), electronicsCategory);
        phone.setId(11L);

        when(productRepository.findAll()).thenReturn(List.of(laptopProduct, phone));

        // Act
        List<ProductResponse> responses = productService.findAll();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Gaming Laptop");
        assertThat(responses.get(1).name()).isEqualTo("Smartphone");
    }

    // ==========================================
    // Search By Name Tests
    // ==========================================

    @Test
    @DisplayName("searchByName: Success - Should return matching products")
    void searchByName_Success() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("laptop")).thenReturn(List.of(laptopProduct));

        // Act
        List<ProductResponse> responses = productService.searchByName("laptop");

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Gaming Laptop");
        assertThat(responses.get(0).categoryName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("searchByName: Empty Keyword - Should throw IllegalArgumentException")
    void searchByName_EmptyKeyword_ThrowsException() {
        assertThatThrownBy(() -> productService.searchByName("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
