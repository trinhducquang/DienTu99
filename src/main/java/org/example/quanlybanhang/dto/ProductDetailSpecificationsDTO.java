package org.example.quanlybanhang.dto;

public class ProductDetailSpecificationsDTO {
    private String configMemory;
    private String camera;
    private String battery;
    private String features;
    private String connectivity;
    private String designMaterials;

    public String getConfigMemory() {
        return configMemory;
    }

    public void setConfigMemory(String configMemory) {
        this.configMemory = configMemory;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public String getDesignMaterials() {
        return designMaterials;
    }

    public void setDesignMaterials(String designMaterials) {
        this.designMaterials = designMaterials;
    }
}
