package com.inisha.inishamart.controller;

import com.inisha.inishamart.dao.ProductDAOImpl;
import com.inisha.inishamart.listener.DataSourceListener;
import com.inisha.inishamart.model.Product;
import com.inisha.inishamart.service.ProductService;
import com.inisha.inishamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/products")
public class ProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute(DataSourceListener.ATTR_NAME);
        productService = new ProductService(new ProductDAOImpl(ds));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("q");
        String category = req.getParameter("category");
        List<Product> products = productService.browse(keyword, category);
        JsonUtil.writeSuccess(resp, HttpServletResponse.SC_OK, products);
    }
}
