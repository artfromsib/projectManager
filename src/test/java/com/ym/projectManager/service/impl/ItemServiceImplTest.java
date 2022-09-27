package com.ym.projectManager.service.impl;

import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.ItemSection;
import com.ym.projectManager.repository.ItemRepository;
import com.ym.projectManager.repository.SectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private SectionRepository sectionRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private static final Long SECTION_ID = 0l;
    private static final String SELECTED_STATUS_READY = "READY";
    private static final ItemSection SECTION_DRESS = new ItemSection(0l, "dress");
    private static final ItemSection SECTION_PANTS = new ItemSection(1l, "pants");
    private static final Item ITEM_RECORD_1 = new Item(1l, "red dress", 2, 5000.0, "NEW", SECTION_DRESS);
    private static final Item ITEM_RECORD_2 = new Item(2l, "blue pants", 1, 3000.0, "READY", SECTION_PANTS);
    private static final Item ITEM_RECORD_3 = new Item(3l, "gold shoes", 1, 4000.0, "IN_PROGRESS", null);
    private static final Item ITEM_RECORD_4 = new Item(4l, "black dress", 1, 3000.0, "READY", SECTION_DRESS);
    private static final List<Item> ALLITEMS = Arrays.asList(ITEM_RECORD_1, ITEM_RECORD_2, ITEM_RECORD_3, ITEM_RECORD_4);
    private static final List<Item> ITEMS_SECTION_DRESS = Arrays.asList(ITEM_RECORD_1, ITEM_RECORD_4);
    private static final List<Item> ITEMS_STATUS_READY = Arrays.asList(ITEM_RECORD_2, ITEM_RECORD_4);
    private static final List<Item> ITEMS_SECTION_DRESS_AND_STATUS_READY = Arrays.asList(ITEM_RECORD_4);


    @Test
    void shouldSaveNewItemsWithoutSection() {

        doReturn(ITEM_RECORD_3)
                .when(itemRepository).saveAndFlush(any(Item.class));
        Item newItem = new Item("gold shoes", 1, 4000.0, "IN_PROGRESS", null);

        var actualResult = itemService.createOrUpdateItem(newItem, Optional.ofNullable(null));
        assertNotNull(actualResult);
        assertNotNull(actualResult.getItemId());
        assertEquals(newItem.getName(), actualResult.getName());
        assertEquals(newItem.getPrice(), actualResult.getPrice());
        verify(itemRepository).saveAndFlush(newItem);
    }

    @Test
    void shouldSaveNewItemsWithSection() {

        doReturn(SECTION_PANTS)
                .when(sectionRepository).saveAndFlush(any(ItemSection.class));
        doReturn(ITEM_RECORD_2)
                .when(itemRepository).saveAndFlush(any(Item.class));

        Item newItem = new Item("blue pants", 1, 3000.0, "READY", null);

        var actualResult = itemService.createOrUpdateItem(newItem, Optional.of(SECTION_PANTS));
        assertNotNull(actualResult);
        assertNotNull(actualResult.getItemId());
        assertEquals(newItem.getName(), actualResult.getName());
        assertEquals(newItem.getPrice(), actualResult.getPrice());
        assertEquals(SECTION_PANTS, actualResult.getItemSection());
        assertEquals(SECTION_PANTS.getName(), actualResult.getItemSection().getName());
        verify(itemRepository).saveAndFlush(newItem);
    }

    @Test
    void shouldReturnAllItemsList() {
        doReturn(ALLITEMS)
                .when(itemRepository).findAll();
        var actualResult = itemService.getAllItems();
        assertNotNull(actualResult);
        assertEquals(ALLITEMS.size(), actualResult.size());
        assertEquals(ALLITEMS, actualResult);
        verify(itemRepository).findAll();
    }

    @Test
    void shouldReturnEmptyItemsListIfItemsNotFound() {
        doReturn(null)
                .when(itemRepository).findAll();
        var actualResult = itemService.getAllItems();
        assertNull(actualResult);
        verify(itemRepository).findAll();
    }

    @Test
    void shouldReturnItemByIdIfExists() {
        doReturn(Optional.of(ITEM_RECORD_1))
                .when(itemRepository).findById(any());
        var actualResult = itemService.getItemById(1l);
        assertNotNull(actualResult);
        assertEquals(ITEM_RECORD_1.getName(), actualResult.getName());
        assertEquals(ITEM_RECORD_1, actualResult);
        verify(itemRepository).findById(any());
    }

    @Test
    void shouldThrowNotFoundExceptionIfItemByIdNotExists() {
        doReturn(Optional.ofNullable(null))
                .when(itemRepository).findById(any());
        Throwable actualException = assertThrows(NotFoundException.class,
                () -> {
                    itemService.getItemById(1l);
                });

        assertTrue(actualException.getMessage().contains("Item with \"1\" doesn't exist."));
        verify(itemRepository).findById(any());
    }

    @Test
    void shouldDeleteItemByIdIfExist() {
        doReturn(Optional.of(ITEM_RECORD_1))
                .when(itemRepository).findById(any());
        doReturn(ALLITEMS)
                .when(itemRepository).findAll();

        List<Item> itemsList = itemService.getAllItems();
        itemService.deleteItem(1l);

        doReturn(Arrays.asList(ITEM_RECORD_2, ITEM_RECORD_3, ITEM_RECORD_4))
                .when(itemRepository).findAll();

        List<Item> actualItemsList = itemService.getAllItems();
        assertEquals(ALLITEMS.size() - 1, actualItemsList.size());
        verify(itemRepository).findById(any());
    }

    @Test
    void shouldThrowNotFoundExceptionIfDeleteItemByIdNotExists() {
        doReturn(Optional.ofNullable(null))
                .when(itemRepository).findById(any());

        Throwable actualException = assertThrows(NotFoundException.class,
                () -> {
                    itemService.deleteItem(1l);
                });

        assertTrue(actualException.getMessage().contains("Item with \"1\" doesn't exist."));
        verify(itemRepository).findById(any());
    }

    @Test
    void shouldReturnPageItemsSelectedOnlyBySection() {
        doReturn(ITEMS_SECTION_DRESS)
                .when(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);

        var actualResult = itemService.getItemsBySection(SECTION_ID);

        assertNotNull(actualResult);
        assertEquals(ITEMS_SECTION_DRESS.size(), actualResult.size());
        assertNotNull(ITEMS_SECTION_DRESS.get(0).getName(), actualResult.get(0).getName());
        assertEquals(SECTION_ID, actualResult.get(0).getItemSection().getItemSectionId());
        verify(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);
    }

    @Test
    void shouldReturnPageItemsSelectedOnlyByStatus() {

        doReturn(ITEMS_STATUS_READY)
                .when(itemRepository).findItemsByStatus(SELECTED_STATUS_READY);

        var actualResult = itemService.getItemsByStatus(SELECTED_STATUS_READY);

        assertNotNull(actualResult);
        assertEquals(ITEMS_STATUS_READY.size(), actualResult.size());
        assertNotNull(ITEMS_STATUS_READY.get(0).getName(), actualResult.get(0).getName());
        assertEquals(SELECTED_STATUS_READY, actualResult.get(0).getStatus());
        verify(itemRepository).findItemsByStatus(SELECTED_STATUS_READY);
    }

    @Test
    void shouldReturnPageItemsSelectedBySectionAndStatus() {

        doReturn(ITEMS_SECTION_DRESS_AND_STATUS_READY)
                .when(itemRepository).findItemsByItemSection_ItemSectionIdAndStatus(SECTION_ID, SELECTED_STATUS_READY);

        var actualResult = itemService.getItemsByStatusAndSection(SELECTED_STATUS_READY, SECTION_ID);

        assertNotNull(actualResult);
        assertEquals(ITEMS_SECTION_DRESS_AND_STATUS_READY.size(), actualResult.size());
        assertEquals(ITEMS_SECTION_DRESS_AND_STATUS_READY.get(0).getName(), actualResult.get(0).getName());
        assertEquals(SELECTED_STATUS_READY, actualResult.get(0).getStatus());
        assertEquals(SECTION_ID, actualResult.get(0).getItemSection().getItemSectionId());
        verify(itemRepository).findItemsByItemSection_ItemSectionIdAndStatus(SECTION_ID, SELECTED_STATUS_READY);
    }

    @Test
    void shouldReturnNullIfNotExistsItems() {
        doReturn(null)
                .when(itemRepository).findAll();

        var actualResult = itemService.getAllItems();

        assertNull(actualResult);
        verify(itemRepository).findAll();
    }

    @Test
    void shouldReturnItemsBySection() {
        doReturn(ITEMS_SECTION_DRESS)
                .when(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);

        var actualResult = itemService.getItemsBySection(SECTION_ID);

        assertNotNull(actualResult);
        assertEquals(ITEMS_SECTION_DRESS.size(), actualResult.size());
        assertEquals(ITEMS_SECTION_DRESS.get(0).getName(), actualResult.get(0).getName());
        assertEquals(SECTION_ID, actualResult.get(0).getItemSection().getItemSectionId());
        verify(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);
    }

    @Test
    void shouldReturnNullIfNotExistsItemsBySection() {
        doReturn(null)
                .when(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);

        var actualResult = itemService.getItemsBySection(SECTION_ID);

        assertNull(actualResult);
        verify(itemRepository).findItemsByItemSection_ItemSectionId(SECTION_ID);
    }


    @Test
    void shouldReturnItemsByName() {
        doReturn(ITEMS_SECTION_DRESS)
                .when(itemRepository).getItemsByNameContainingIgnoreCase(any());
        var actualResult = itemService.getItemsByName("dress");

        assertNotNull(actualResult);
        assertEquals(ITEMS_SECTION_DRESS.size(), actualResult.size());
        assertTrue(actualResult.get(0).getName().toLowerCase().contains("dress"));
        verify(itemRepository).getItemsByNameContainingIgnoreCase(any());
    }

}
