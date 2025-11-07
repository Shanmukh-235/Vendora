package com.vendora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vendora.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
