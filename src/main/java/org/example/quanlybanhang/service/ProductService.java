package org.example.quanlybanhang.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;


public class ProductService {

    private final ProductDAO productDAO;


    public ProductService() {
        Connection connection = DatabaseConnection.getConnection();
        this.productDAO = new ProductDAO(connection);

    }

    public ObservableList<Product> getAllProducts() {
        return FXCollections.observableArrayList(productDAO.getAll());
    }

    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    public Product getProductById(int id) {
        return productDAO.findById(id);
    }

    public int countRelatedProducts(int categoryId, int excludedId) {
        return productDAO.countRelatedProducts(categoryId, excludedId);
    }

    public List<Product> getRelatedProducts(int categoryId, int excludedId, int offset, int limit) {
        return productDAO.getRelatedProducts(categoryId, excludedId, offset, limit);
    }


    public void saveProduct(Product product) {
        productDAO.save(product);
    }

}
