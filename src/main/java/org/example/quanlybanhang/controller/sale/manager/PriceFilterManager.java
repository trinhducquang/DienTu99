package org.example.quanlybanhang.controller.sale.manager;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public class PriceFilterManager {
    private final Slider minPriceSlider;
    private final Slider maxPriceSlider;
    private final Label minPriceLabel;
    private final Label maxPriceLabel;

    private BigDecimal minPrice = BigDecimal.ZERO;
    private BigDecimal maxPrice = new BigDecimal("900000000");

    private BiConsumer<BigDecimal, BigDecimal> onPriceFilterChanged;

    public PriceFilterManager(Slider minPriceSlider, Slider maxPriceSlider,
                              Label minPriceLabel, Label maxPriceLabel) {
        this.minPriceSlider = minPriceSlider;
        this.maxPriceSlider = maxPriceSlider;
        this.minPriceLabel = minPriceLabel;
        this.maxPriceLabel = maxPriceLabel;

        initPriceSliders();
    }

    private void initPriceSliders() {
        minPriceSlider.setValue(minPrice.doubleValue());
        maxPriceSlider.setValue(maxPrice.doubleValue());
        updatePriceLabels();

        minPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> onMinSliderChanged(newVal));
        maxPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> onMaxSliderChanged(newVal));

        minPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) triggerPriceFilterChanged();
        });

        maxPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) triggerPriceFilterChanged();
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
            triggerPriceFilterChanged();
        }
    }

    private void onMaxSliderChanged(Number newValue) {
        maxPrice = BigDecimal.valueOf(newValue.doubleValue());
        updatePriceLabels();

        if (maxPrice.compareTo(BigDecimal.valueOf(minPriceSlider.getValue())) < 0) {
            minPriceSlider.setValue(maxPrice.doubleValue());
        } else if (!maxPriceSlider.isValueChanging()) {
            triggerPriceFilterChanged();
        }
    }

    private void triggerPriceFilterChanged() {
        if (onPriceFilterChanged != null) {
            onPriceFilterChanged.accept(minPrice, maxPrice);
        }
    }

    public void setOnPriceFilterChanged(BiConsumer<BigDecimal, BigDecimal> callback) {
        this.onPriceFilterChanged = callback;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}