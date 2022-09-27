package com.ym.projectManager.repository;

import com.ym.projectManager.model.ItemSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<ItemSection, Long> {
}
