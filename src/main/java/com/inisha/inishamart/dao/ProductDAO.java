package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    List<Product> search(String keyword, String category);
    Optional<Product> findById(long id);
    void decrementStock(long productId, int quantity);
}
