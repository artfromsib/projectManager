package com.ym.projectManager.repository;

import com.ym.projectManager.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    int countItemByItemSection_ItemSectionIdAndStatus(Long sectionId, String status);
    int countItemByItemSection_ItemSectionId(Long sectionId);
    int countItemByStatus(String status);
    List<Item> findItemsByItemSection_ItemSectionId(Long sectionId);
    List<Item> getItemsByNameContainingIgnoreCase(String name);
    List<Item> findItemsByItemSection_ItemSectionIdAndStatus(Long sectionId, String status);
    List<Item> findItemsByStatus(String selectedStatus);



}
