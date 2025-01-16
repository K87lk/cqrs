package org.example.trustedpackage.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.trustedpackage.dto.ProductEvent;
import org.example.trustedpackage.dto.ProductEventType;
import org.example.trustedpackage.model.Product;
import org.example.trustedpackage.repository.ProductQueryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductQueryService {
    private final ProductQueryRepository productQueryRepository;

    public ProductQueryService(ProductQueryRepository productQueryRepository) {
        this.productQueryRepository = productQueryRepository;
    }

    public Product getProductById(UUID id) {
        return productQueryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Product with id " + id + " not found"));
    }

    public List<Product> getAllProducts() {
        return productQueryRepository.findAll();
    }

    @KafkaListener(topics = "product-topic", topicPartitions = @TopicPartition(topic = "product-topic", partitions = "0"))
    public void processCreateProductEvent(ProductEvent productEvent) {
        Product product = productEvent.getProduct();

        if (productEvent.getEventType().equals(ProductEventType.CREATED)) {
            productQueryRepository.save(product);
        }
    }

    @KafkaListener(topics = "product-topic", topicPartitions = @TopicPartition(topic = "product-topic", partitions = "1"))
    public void processUpdateProductEvent(ProductEvent productEvent) {
        Product product = productEvent.getProduct();

        if (productEvent.getEventType().equals(ProductEventType.UPDATED)) {
            Product existingProduct = productQueryRepository.findById(product.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product with id " + product.getId() + " not found"));
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setDescription(product.getDescription());
            productQueryRepository.save(existingProduct);
        }
    }

    @KafkaListener(topics = "product-topic", topicPartitions = @TopicPartition(topic = "product-topic", partitions = "2"))
    public void processDeleteProductEvent(ProductEvent productEvent) {
        Product product = productEvent.getProduct();

        if (productEvent.getEventType().equals(ProductEventType.DELETED)) {
            Product existingProduct = productQueryRepository.findById(product.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product with id " + product.getId() + " not found"));

            productQueryRepository.deleteById(existingProduct.getId());
        }
    }
}
