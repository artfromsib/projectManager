package com.ym.projectManager.service.impl;

import com.ym.projectManager.enums.ItemStatus;
import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.ItemSection;
import com.ym.projectManager.repository.ItemRepository;
import com.ym.projectManager.repository.SectionRepository;
import com.ym.projectManager.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final SectionRepository sectionRepository;

    @Override
    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items;
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Item with \"%s\" doesn't exist.", id)));
    }

    @Override
    public void deleteItem(Long id) {
        getItemOrThrowException(id);
        itemRepository.deleteById(id);
    }


    private Boolean isValidItemStatus(String status){
        try{
            ItemStatus.valueOf(status.toUpperCase(Locale.ROOT));
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public List<Item> getItemsByStatus(String status){
       if(isValidItemStatus(status))
            return itemRepository.findItemsByStatus(status.toUpperCase(Locale.ROOT));
        else return Collections.emptyList();
    }

    @Override
    public List<Item> getItemsBySection(Long sectionId) {
        return itemRepository.findItemsByItemSection_ItemSectionId(sectionId);
    }

    @Override
    public  List<Item> getItemsByStatusAndSection(String status, Long sectionId){
        if(isValidItemStatus(status))
            return itemRepository.findItemsByItemSection_ItemSectionIdAndStatus(sectionId, status.toUpperCase(Locale.ROOT));
       else return Collections.emptyList();
    }

    @Override
    public Item createOrUpdateItem(Item item, Optional<ItemSection> section) {
        if (section.isPresent()) {
            ItemSection save = sectionRepository.saveAndFlush(section.get());
            item.setItemSection(save);
        }
        return itemRepository.saveAndFlush(item);
    }

    @Override
    public List<Item> getItemsByName(String name) {
        return itemRepository.getItemsByNameContainingIgnoreCase(name);
    }

    private void getItemOrThrowException(Long id) {
        itemRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Item with \"%s\" doesn't exist.", id)));
    }

    @Override
    public ItemSection createItemSection(String section){
        return sectionRepository.save(new ItemSection(section));
    }

    @Override
    public List<ItemSection> getItemSections(){
        return sectionRepository.findAll();
    }

}
