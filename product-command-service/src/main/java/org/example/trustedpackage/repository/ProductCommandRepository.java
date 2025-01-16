package org.example.trustedpackage.repository;

import org.example.trustedpackage.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductCommandRepository extends JpaRepository<Product, UUID> {
}
