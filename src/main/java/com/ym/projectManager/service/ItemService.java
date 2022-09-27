package com.ym.projectManager.service;

import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.ItemSection;
import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item createOrUpdateItem(Item item, Optional<ItemSection> newSection);

    List<Item> getAllItems();

    Item getItemById(Long id);

    List<Item> getItemsBySection(Long sectionId);

    List<Item> getItemsByName(String name);

    void deleteItem(Long id);

    ItemSection createItemSection(String section);

    List<ItemSection> getItemSections();

    List<Item> getItemsByStatus(String itemStatus);

    List<Item> getItemsByStatusAndSection(String status, Long sectionId);
}
