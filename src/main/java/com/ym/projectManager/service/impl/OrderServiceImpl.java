package com.ym.projectManager.service.impl;


import com.ym.projectManager.dto.OrderItemDto;
import com.ym.projectManager.enums.OrderStatus;
import com.ym.projectManager.repository.*;
import com.ym.projectManager.service.CustomerService;
import com.ym.projectManager.service.OrderService;
import com.ym.projectManager.model.*;
import com.ym.projectManager.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final ParcelRepository parcelRepository;
    private final CustomerService customerService;
    private final ParcelService parcelService;

    @Override
    public List<OrderItemDto> getAllOrderItemDto() {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            orderItemDtos.add(new OrderItemDto(orderItemRepository.findOrderItemsByOrder_OrderId(order.getOrderId()),
                    order.getCustomer(), order.getParcel()));
        }
        return orderItemDtos;
    }

    @Override
    public OrderItemDto getOrderItemDto(Long id) {
        Optional<Order> order = orderRepository.getByOrderId(id);
        if (order.isPresent()) {
            List<OrderItem> orderItem = orderItemRepository.findOrderItemsByOrder_OrderId(id);
            return new OrderItemDto(orderItem, order.get().getCustomer(), order.get().getParcel());
        } else {
            new ResourceNotFoundException("Product not found");
        }
        return null;
    }

    @Override
    public OrderItemDto createOrUpdateOrder(OrderItemDto form) {
        double orderTotal = 0;
        int orderValue = 0;
        Order order;
        List<OrderItem> items = form.getOrderItems();
        validateItemsExistence(items);

        if (form.getOrderItems().get(0).getOrder().getOrderId() == null) {
            order = new Order();
            order.setStatus(OrderStatus.NEW.toString());
            order.setCustomer(customerService.createOrUpdateCustomer(form.getCustomer()));
            order.setParcel(checkParcel(order));
            order = orderRepository.save(order);
        } else {
            order = form.getOrderItems().get(0).getOrder();
            order.setParcel(checkParcel(order));
            orderRepository.saveAndFlush(order);
        }

        for (int i = 0; i < items.size(); i++) {
            Item newItem;
            if (items.get(i).getItem().getItemId() == null) {
                newItem = itemRepository.save(items.get(i).getItem());
            } else {
                newItem = itemRepository.saveAndFlush(items.get(i).getItem());
            }

            int quantity = items.get(i).getCount();
            double price = items.get(i).getPrice();
            orderTotal += price * quantity;
            orderValue += quantity;

            OrderItem orderItem = new OrderItem(newItem, order, quantity, price, quantity * price);

            if (orderItemRepository.findByOrderEqualsAndItemEquals(order, newItem) == null)
                items.set(i, orderItemRepository.save(orderItem));
        }

        order.setCountItems(orderValue);
        order.setOrderTotal(orderTotal);
        order = orderRepository.saveAndFlush(order);

        return new OrderItemDto(items, order.getCustomer(), order.getParcel());
    }

    private void validateItemsExistence(List<OrderItem> items) {
        List<OrderItem> list = items
                .stream()
                .filter(op -> op.getItem().getItemId() != null && Objects.isNull(itemRepository.findById(op.getItem().getItemId())))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(list)) {
            new ResourceNotFoundException("Item not found");
        }
    }

    private Parcel checkParcel(Order order){
        if (order.getParcel() != null)
            return parcelService.createOrUpdateParcel(order.getParcel());
        else return null;
    }




    @Override
    public Order saveOrderWithItemsCustomerAndParcel(Order order, List<Item> items, Customer customer) {
        Order newOrder;

        if (customer.getCustomerId() == null) {
            Customer newCustomer = customerRepository.save(customer);
            order.setCustomer(newCustomer);
        } else {
            order.setCustomer(customerRepository.saveAndFlush(customer));
        }

        if (order.getParcel().getTrackNumber().length() > 1)
            if (order.getParcel().getParcelId() == null) {
                Parcel parcel = parcelRepository.save(order.getParcel());
                order.setParcel(parcel);
            } else {
                parcelRepository.saveAndFlush(order.getParcel());
            }
        else {
            order.setParcel(null);
        }

        if (order.getOrderId() == null) {
            newOrder = orderRepository.save(order);
        } else {
            newOrder = order;
        }
        AtomicReference<Double> orderTotal = new AtomicReference<>(0.0);
        AtomicInteger orderValue = new AtomicInteger();
        Order finalNewOrder = newOrder;
        items.forEach(item -> {
            Item newItem;

            if (item.getItemId() == null) {
                newItem = itemRepository.save(item);
            } else {
                newItem = itemRepository.saveAndFlush(item);
            }
            orderTotal.getAndSet((orderTotal.get() + (item.getPrice() * item.getQuantity())));
            orderValue.getAndSet(orderValue.get() + item.getQuantity());
            OrderItem orderItem = new OrderItem(newItem, finalNewOrder, item.getQuantity(),
                    item.getPrice(), item.getQuantity() * item.getPrice());
            if (orderItemRepository.findByOrderEqualsAndItemEquals(finalNewOrder, newItem) == null)
                orderItemRepository.save(orderItem);

        });

        newOrder = orderRepository.saveAndFlush(newOrder);
        return newOrder;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    @Override
    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Order with \"%s\" doesn't exist.", id))));
    }

    @Override
    public void deleteOrderById(Long id) {
        getOrderOrThrowException(id);
        orderRepository.deleteById(id);
    }

    private void getOrderOrThrowException(Long id) {
        orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Order with \"%s\" doesn't exist.", id)));
    }

    @Override
    public List<OrderItemDto> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatusOrderByDateSaleDesc(status.toUpperCase(Locale.ROOT));
        List<OrderItemDto> orderItemDtos = new ArrayList<>(orders.size());
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            orderItemDtos.add(new OrderItemDto(orderItemRepository.findOrderItemsByOrder_OrderId(order.getOrderId()),
                    order.getCustomer(), order.getParcel()));
        }
        return orderItemDtos;
    }

    @Override
    public void setOrderStatus(Long id, String status) {
        Order order = orderRepository.getByOrderId(id).get();
        order.setStatus(status);
        //orderRepository.saveAndFlush(order);
        /*if (status.equals("COMPLETE")) {
            order. setShippingDate(LocalDateTime.now());
        }*/
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void addTrackNumberToOrderAndSaveParcel(Long orderId, String trackNum) {
        Parcel parcel = parcelRepository.findFirstByTrackNumber(trackNum);
        if (parcel == null) {
            parcel = parcelRepository.save(new Parcel(trackNum, LocalDate.now()));

        } else {
            parcelRepository.saveAndFlush(parcel);
        }
        Order order = orderRepository.getByOrderId(orderId).get();
        order.setParcel(parcel);
        orderRepository.saveAndFlush(order);
    }


    @Override
    public Parcel setOrderDelivered(Long parcelId) {
        Parcel parcel = parcelRepository.getById(parcelId);
        parcel.setDelivered(true);
        return parcelRepository.saveAndFlush(parcel);
    }


}
