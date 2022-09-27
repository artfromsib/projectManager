package com.ym.projectManager.enums;


import com.ym.projectManager.controller.ItemController;

import java.util.stream.Stream;

public enum ItemStatus {
    NEW,
    IN_PROGRESS,
    READY,
    SOLD;

    public static Stream<ItemStatus> stream() {
        return Stream.of(ItemStatus.values());
    }
}


