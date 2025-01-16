package org.example.trustedpackage.dto;

import org.example.trustedpackage.model.Product;



public class ProductEvent {

    public ProductEvent() {
    }

    private ProductEventType eventType;
    private Product product;

    public ProductEvent(ProductEventType eventType, Product product) {
        this.eventType = eventType;
        this.product = product;
    }

    public ProductEventType getEventType() {
        return eventType;
    }

    public void setEventType(ProductEventType eventType) {
        this.eventType = eventType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProductEvent{");
        sb.append("eventType=").append(eventType);
        sb.append(", product=").append(product);
        sb.append('}');
        return sb.toString();
    }
}