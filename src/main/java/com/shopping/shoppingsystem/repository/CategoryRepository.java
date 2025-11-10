package com.shopping.shoppingsystem.repository;

import com.shopping.shoppingsystem.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
