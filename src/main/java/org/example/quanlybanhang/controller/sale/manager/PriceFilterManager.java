package org.example.quanlybanhang.controller.sale.manager;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;

public class PriceFilterManager {

    private final Slider minPriceSlider;
    private final Slider maxPriceSlider;
    private final Label minPriceLabel;
    private final Label maxPriceLabel;
    private final ObservableList<Product> filteredProducts;
    private final ObservableList<Product> allProducts;
    private final Runnable onFilterApplied;

    private BigDecimal minPrice = BigDecimal.ZERO;
    private BigDecimal maxPrice = new BigDecimal("900000000");

    public PriceFilterManager(Slider minPriceSlider,
                              Slider maxPriceSlider,
                              Label minPriceLabel,
                              Label maxPriceLabel,
                              ObservableList<Product> filteredProducts,
                              ObservableList<Product> allProducts,
                              Runnable onFilterApplied) {
        this.minPriceSlider = minPriceSlider;
        this.maxPriceSlider = maxPriceSlider;
        this.minPriceLabel = minPriceLabel;
        this.maxPriceLabel = maxPriceLabel;
        this.filteredProducts = filteredProducts;
        this.allProducts = allProducts;
        this.onFilterApplied = onFilterApplied;
    }

    public void initPriceSliders() {
        minPriceSlider.setValue(minPrice.doubleValue());
        maxPriceSlider.setValue(maxPrice.doubleValue());
        updatePriceLabels();

        minPriceSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                onMinSliderChanged(newVal));
        maxPriceSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                onMaxSliderChanged(newVal));

        minPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) filterProductsByPrice();
        });

        maxPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) filterProductsByPrice();
        });
    }

    private void updatePriceLabels() {
        minPriceLabel.setText(MoneyUtils.formatVN(minPrice));
        maxPriceLabel.setText(MoneyUtils.formatVN(maxPrice));
    }

    private void onMinSliderChanged(Number newValue) {
        minPrice = BigDecimal.valueOf(newValue.doubleValue());
        updatePriceLabels();

        if (minPrice.compareTo(BigDecimal.valueOf(maxPriceSlider.getValue())) > 0) {
            maxPriceSlider.setValue(minPrice.doubleValue());
        } else if (!minPriceSlider.isValueChanging()) {
            filterProductsByPrice();
        }
    }

    private void onMaxSliderChanged(Number newValue) {
        maxPrice = BigDecimal.valueOf(newValue.doubleValue());
        updatePriceLabels();

        if (maxPrice.compareTo(BigDecimal.valueOf(minPriceSlider.getValue())) < 0) {
            minPriceSlider.setValue(maxPrice.doubleValue());
        } else if (!maxPriceSlider.isValueChanging()) {
            filterProductsByPrice();
        }
    }

    public void filterProductsByPrice() {
        filteredProducts.setAll(
                allProducts.filtered(product -> {
                    BigDecimal price = product.getPrice();
                    return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
                })
        );
        onFilterApplied.run();
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}