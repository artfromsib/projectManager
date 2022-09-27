package com.ym.projectManager.service;


import com.ym.projectManager.dto.OrderItemDto;
import com.ym.projectManager.model.Customer;
import com.ym.projectManager.model.Item;
import com.ym.projectManager.model.Order;
import com.ym.projectManager.model.Parcel;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderItemDto> getAllOrderItemDto();

    OrderItemDto getOrderItemDto(Long id);

    OrderItemDto createOrUpdateOrder(OrderItemDto form);

    Order saveOrderWithItemsCustomerAndParcel(Order order, List<Item> items, Customer customer);

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    void deleteOrderById(Long id);

    List<OrderItemDto> getOrdersByStatus(String status);

    void setOrderStatus(Long id, String status);

    void addTrackNumberToOrderAndSaveParcel(Long orderId, String trackNum);

    Parcel setOrderDelivered(Long parcelI);

}
