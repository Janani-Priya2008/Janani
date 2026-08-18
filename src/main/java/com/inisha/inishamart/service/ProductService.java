package com.inisha.inishamart.service;

import com.inisha.inishamart.dao.ProductDAO;
import com.inisha.inishamart.model.Product;

import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> browse(String keyword, String category) {
        return productDAO.search(keyword, category);
    }

    public Optional<Product> get(long id) {
        return productDAO.findById(id);
    }
}
