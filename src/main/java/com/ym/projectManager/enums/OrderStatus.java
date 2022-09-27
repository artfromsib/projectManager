package com.ym.projectManager.enums;

import java.util.stream.Stream;

public enum OrderStatus {
    NEW("new"),
    IN_PROGRESS("in progress"),
    COMPLETE("complete");

    private String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public static Stream<OrderStatus> stream() {
        return Stream.of(OrderStatus.values());
    }
}
