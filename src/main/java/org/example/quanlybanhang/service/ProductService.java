package org.example.quanlybanhang.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.ImportedWarehouseDTO;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductDAO productDAO;
    private final WarehouseDAO warehouseDAO; // ✅ Thêm dòng này

    public ProductService() {
        Connection connection = DatabaseConnection.getConnection();
        this.productDAO = new ProductDAO(connection);
        this.warehouseDAO = new WarehouseDAO(); // ✅ Và dòng này
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

    public List<ImportedWarehouseDTO> getImportedProductsOnly() {
        return warehouseDAO.getProductsImportedFromWarehouse().stream()
                .map(dto -> new ImportedWarehouseDTO(dto.getId(), dto.getName(), dto.getTransactionCode()))
                .distinct()
                .collect(Collectors.toList());
    } // ddang buggg

    public void saveProduct(Product product) {
        productDAO.save(product);
    }
}
