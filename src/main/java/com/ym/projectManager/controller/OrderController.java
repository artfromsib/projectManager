package com.ym.projectManager.controller;

import com.ym.projectManager.dto.OrderItemDto;
import com.ym.projectManager.dto.ParcelTrackingDto;
import com.ym.projectManager.enums.OrderStatus;
import com.ym.projectManager.dto.OrderStatusDto;
import com.ym.projectManager.service.OrderService;
import com.ym.projectManager.service.ParcelService;
import com.ym.projectManager.service.TrackerService;
import com.ym.projectManager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/main")

public class OrderController {

    private final OrderService orderService;
    private final TrackerService trackerService;
    private final ParcelService parcelService;

    @GetMapping(value = "/orders")
    public ResponseEntity<List<OrderItemDto>> findAllOrders() {
        final List<OrderItemDto> orders = orderService.getAllOrderItemDto();
        return responseOrder(orders, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/{id}")
    public ResponseEntity<OrderItemDto> getOrder(@PathVariable(name = "id") long id) {
        final OrderItemDto order = orderService.getOrderItemDto(id);
        return order != null
                ? new ResponseEntity<>(order, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderItemDto>> listOrdersByStatus(@PathVariable(name = "status") String status) {
        final List<OrderItemDto> orders = orderService.getOrdersByStatus(status);
        return responseOrder(orders, HttpStatus.OK);
    }

    private ResponseEntity<List<OrderItemDto>> responseOrder(List<OrderItemDto> orders, HttpStatus httpStatus) {
        return orders != null && !orders.isEmpty()
                ? new ResponseEntity<>(orders, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderItemDto> createOrder(@RequestBody OrderItemDto form) {

        OrderItemDto order = orderService.createOrUpdateOrder(form);

        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders/{id}")
                .buildAndExpand(order.getOrderItems().get(0).getOrder().getOrderId())
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    @PutMapping("/orders")
    public ResponseEntity<OrderItemDto> changeOrder(@RequestBody OrderItemDto form) {
        OrderItemDto order = orderService.createOrUpdateOrder(form);

        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders/{id}")
                .buildAndExpand(order.getOrderItems().get(0).getOrder().getOrderId())
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new ResponseEntity<>(order, headers, HttpStatus.OK);
    }

    @DeleteMapping("/orders")
    public ResponseEntity<HttpStatus> deleteOrder(@RequestParam(value = "order_id", required = false) long orderId) {
        orderService.deleteOrderById(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/order/set_status")
    public ResponseEntity<HttpStatus> setOrderStatus(OrderStatusDto orderStatusDto) {
        orderService.setOrderStatus(orderStatusDto.getOrderId(), orderStatusDto.getStatus());

        if (orderStatusDto.getStatus().equals(OrderStatus.COMPLETE.toString())) {
            trackerService.registerParcelInTracker(orderStatusDto.getTrackNum());
            orderService.addTrackNumberToOrderAndSaveParcel(orderStatusDto.getOrderId(), orderStatusDto.getTrackNum());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/order/tracking")
    public ResponseEntity<ParcelTrackingDto> orderTracking(@RequestParam(value = "parcel_id", required = false) Long parcelId) {
        ParcelTrackingDto parcel = parcelService.getParcelTracking(parcelId);
        return parcel != null
                ? new ResponseEntity<>(parcel, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/order/tracking/delivered")
    public ResponseEntity<HttpStatus> orderSetDelivered(@RequestParam(value = "parcel_id", required = false) Long parcelId) {
        Parcel parcel = orderService.setOrderDelivered(parcelId);
        return parcel != null
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
