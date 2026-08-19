package com.janani.jananimart.service;

import com.janani.jananimart.dao.ProductDAO;
import com.janani.jananimart.model.Product;
import com.janani.jananimart.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(Long sellerId, String name, String description,
                                  BigDecimal price, int stockQty, String category, String imageUrl) {
        if (!ValidationUtil.isNonEmpty(name)) throw new IllegalArgumentException("Name is required");
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be positive");
        if (stockQty < 0) throw new IllegalArgumentException("Stock cannot be negative");

        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStockQty(stockQty);
        p.setCategory(category);
        p.setImageUrl(imageUrl);
        return productDAO.create(p);
    }

    public List<Product> search(String keyword, String category) {
        return productDAO.search(keyword, category);
    }

    public Optional<Product> getById(Long id) {
        return productDAO.findById(id);
    }
}
