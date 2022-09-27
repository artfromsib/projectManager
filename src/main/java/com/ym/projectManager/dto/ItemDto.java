package com.ym.projectManager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.ItemSection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemDto {
    private Item item;
    private ItemSection section;
}
