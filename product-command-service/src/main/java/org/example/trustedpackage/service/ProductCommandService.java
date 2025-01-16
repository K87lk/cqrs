package org.example.trustedpackage.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.trustedpackage.dto.ProductEvent;
import org.example.trustedpackage.dto.ProductEventType;
import org.example.trustedpackage.model.Product;
import org.example.trustedpackage.repository.ProductCommandRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductCommandService {
    private final ProductCommandRepository productCommandRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductCommandService(ProductCommandRepository productCommandRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.productCommandRepository = productCommandRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Product createProduct(Product product) {
        product.setId(UUID.randomUUID());

        ProductEvent productEvent = new ProductEvent();
        productEvent.setEventType(ProductEventType.CREATED);
        productEvent.setProduct(product);

        kafkaTemplate.send("product-topic", 0, "created-key", productEvent);
        return productCommandRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, Product product) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setEventType(ProductEventType.UPDATED);
        productEvent.setProduct(product);

        return productCommandRepository.findById(id)
                .map(p -> {
                    p.setName(product.getName());
                    p.setDescription(product.getDescription());
                    p.setPrice(product.getPrice());
                    kafkaTemplate.send("product-topic", 1, "updated-key", productEvent);
                    return productCommandRepository.save(p);
                })
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productCommandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found"));

        ProductEvent productEvent = new ProductEvent();
        productEvent.setEventType(ProductEventType.DELETED);
        productEvent.setProduct(product);

        productCommandRepository.deleteById(id);
        kafkaTemplate.send("product-topic", 2, "deleted-key", productEvent);
    }
}
