package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.Count;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountRepository extends JpaRepository<Count, Long> {
}
