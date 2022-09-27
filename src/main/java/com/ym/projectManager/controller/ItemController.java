package com.ym.projectManager.controller;

import com.ym.projectManager.dto.ItemDto;
import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.ItemSection;
import com.ym.projectManager.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/main/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Item>> findAllItems() {
        final List<Item> items = itemService.getAllItems();
        return responseItems(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable(name = "id") long id) {
        final Item item = itemService.getItemById(id);
        return item != null
                ? new ResponseEntity<>(item, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    public ResponseEntity<Item> createItem(@RequestBody ItemDto form) {
        return createOrUpdateItem(form, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<Item> saveItem(@RequestBody ItemDto form) {
        return createOrUpdateItem(form, HttpStatus.OK);
    }

    private ResponseEntity<Item> createOrUpdateItem(ItemDto itemDto, HttpStatus status){
        Item item = itemService.createOrUpdateItem(itemDto.getItem(), Optional.of(itemDto.getSection()));
        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/items/{id}")
                .buildAndExpand(item.getItemId())
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new ResponseEntity<>(item, headers, status);
    }

    @DeleteMapping("")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable long id) {
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Item>> getItemsByStatus(@PathVariable String status) {
        List<Item> items = itemService.getItemsByStatus(status);
        return responseItems(items);
    }

    @GetMapping("/status/{status}/section/{id}")
    public ResponseEntity<List<Item>> getItemsByStatusAnsSection(@PathVariable String status, @PathVariable long id) {
        List<Item> items = itemService.getItemsByStatusAndSection(status, id);
        return responseItems(items);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        List<Item> items = itemService.getItemsByName(name);
        return responseItems(items);
    }

    private ResponseEntity<List<Item>> responseItems(List<Item> items) {
        return items != null && !items.isEmpty()
                ? new ResponseEntity<>(items, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/section/{id}")
    public ResponseEntity<List<Item>> getItemsBySection(@PathVariable long id) {
        List<Item> items = itemService.getItemsBySection(id);
        return responseItems(items);
    }

    @GetMapping("/section")
    public ResponseEntity<List<ItemSection>> getItemSections(){
        List<ItemSection> itemSections = itemService.getItemSections();
        return itemSections != null && !itemSections.isEmpty()
                ? new ResponseEntity<>(itemSections, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/section")
    public ResponseEntity<ItemSection> addSection(@PathVariable String sectionName) {
        ItemSection itemSection = itemService.createItemSection(sectionName);
        return itemSection != null
                ? new ResponseEntity<>(itemSection, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.CREATED);
    }

}
