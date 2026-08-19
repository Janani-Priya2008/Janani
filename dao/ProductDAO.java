package com.janani.jananimart.dao;

import com.janani.jananimart.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(Long id);
    List<Product> search(String keyword, String category);
    boolean decrementStock(Long productId, int qty, Connection existingConn) throws java.sql.SQLException;
    void update(Product product);
    void delete(Long id);
}
