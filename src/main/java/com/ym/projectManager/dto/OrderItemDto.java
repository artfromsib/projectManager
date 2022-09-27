package com.ym.projectManager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ym.projectManager.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderItemDto {

    private List<OrderItem> orderItems;
    private Customer customer;
    private Parcel parcel;
}
