package se.lexicon.ecommercejpaworkshop.service;

import se.lexicon.ecommercejpaworkshop.dto.ProductRequest;
import se.lexicon.ecommercejpaworkshop.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    List<ProductResponse> findAll();

    List<ProductResponse> searchByName(String name);
}
