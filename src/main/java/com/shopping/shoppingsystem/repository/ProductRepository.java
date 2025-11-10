package com.shopping.shoppingsystem.repository;

import com.shopping.shoppingsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
