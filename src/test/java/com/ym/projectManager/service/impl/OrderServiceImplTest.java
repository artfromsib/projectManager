package com.ym.projectManager.service.impl;

import com.ym.projectManager.model.*;
import com.ym.projectManager.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ParcelRepository parcelRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private static final Customer CUSTOMER_RECORD = new Customer(0l,"Vasilij Petrov", "Moscow, ul. Lenina, d 5");

    private static Order ORDER_RECORD_1;
    private static final Order ORDER_RECORD_2 = new Order(1l,"COMPLETE", null);

    private static final ItemSection SECTION_DRESS = new ItemSection(0l,"dress");

    private static final Item ITEM_RECORD_1 = new Item(1l, "red dress", 2, 5000.0, "NEW", SECTION_DRESS);
    private static final Item ITEM_RECORD_2 = new Item("green dress", 2, 4000.0, "READY", null);

    private static final OrderItem ORDER_ITEM_1_RECORD = new OrderItem(ITEM_RECORD_1, ORDER_RECORD_1,1, 10.0,10.0);
    private static final OrderItem ORDER_ITEM_2_RECORD = new OrderItem(ITEM_RECORD_2, ORDER_RECORD_1,1, 10.0,10.0);

    private static final Set<OrderItem> ITEM_1_SET = Set.of(ORDER_ITEM_1_RECORD);
    private static final Set<OrderItem> ITEM_2_SET = Set.of(ORDER_ITEM_2_RECORD);

    private static final Order ORDER_RECORD_WITH_CUSTOMER = new Order(0l, CUSTOMER_RECORD, "NEW",ITEM_1_SET);
    private static final Order ORDER_RECORD_2_WITH_CUSTOMER =   new Order(0l, CUSTOMER_RECORD, "NEW", ITEM_2_SET);

    private static final String STATUS_NEW = "NEW";
    private static final String TRACK_NUM = "RR2422424RU";
    private static final Parcel PARCEL_RECORD_1 = new Parcel(0l,TRACK_NUM);

    @BeforeEach
    void init() {
        ORDER_RECORD_1 = new Order(0l,"NEW", null);
    }

    @Test
    void shouldSaveNewOrderWithNewCustomer(){
        doReturn(CUSTOMER_RECORD)
                .when(customerRepository).save(Mockito.any());
        doReturn(ORDER_RECORD_1)
                .when(orderRepository).save(Mockito.any());
        doReturn(null)
                .when(orderItemRepository).findByOrderEqualsAndItemEquals(Mockito.any(), Mockito.any());
        doReturn(ORDER_ITEM_1_RECORD)
                .when(orderItemRepository).save(Mockito.any());
        doReturn(ORDER_RECORD_WITH_CUSTOMER)
                .when(orderRepository).saveAndFlush(Mockito.any());
        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setParcel(new Parcel(""));
        List<Item> newItems =  Arrays.asList(ITEM_RECORD_1);
        Customer newCustomer = new Customer("Vasilij Petrov", "Moscow, ul. Lenina, d 5");
        var actualResult = orderService.saveOrderWithItemsCustomerAndParcel(newOrder, newItems,newCustomer);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getOrderId());
        assertEquals(newCustomer.getFullName(), actualResult.getCustomer().getFullName());
        assertEquals(newItems.get(0).getName(), actualResult.getItems().get(0).getName());
        verify(orderRepository).saveAndFlush(Mockito.any());
    }

    @Test
    void shouldSaveNewOrderWithCustomerByDB(){
        doReturn(CUSTOMER_RECORD)
                .when(customerRepository).saveAndFlush(Mockito.any());
        doReturn(ORDER_RECORD_1)
                .when(orderRepository).save(Mockito.any());
        doReturn(null)
                .when(orderItemRepository).findByOrderEqualsAndItemEquals(Mockito.any(), Mockito.any());
        doReturn(ORDER_ITEM_1_RECORD)
                .when(orderItemRepository).save(Mockito.any());
        doReturn(ORDER_RECORD_WITH_CUSTOMER)
                .when(orderRepository).saveAndFlush(Mockito.any());
        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setParcel(null);
        newOrder.setCustomer(CUSTOMER_RECORD);
        newOrder.setParcel(new Parcel(""));

        List<Item> newItems =  Arrays.asList(ITEM_RECORD_1);
        Customer customerFromDB = new Customer(0l,"Vasilij Petrov", "Moscow, ul. Lenina, d 5");
        var actualResult = orderService.saveOrderWithItemsCustomerAndParcel(newOrder, newItems,customerFromDB);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getOrderId());
        assertEquals(customerFromDB.getFullName(), actualResult.getCustomer().getFullName());
        verify(orderRepository).saveAndFlush(Mockito.any());
        verify(customerRepository).saveAndFlush(Mockito.any());
    }

    @Test
    void shouldSaveNewOrderWithNewItem(){
        doReturn(CUSTOMER_RECORD)
                .when(customerRepository).saveAndFlush(Mockito.any());
        doReturn(ORDER_RECORD_1)
                .when(orderRepository).save(Mockito.any());
        doReturn(null)
                .when(orderItemRepository).findByOrderEqualsAndItemEquals(Mockito.any(), Mockito.any());
        doReturn(ITEM_RECORD_2)
                .when(itemRepository).save(Mockito.any());
        doReturn(ORDER_ITEM_2_RECORD)
                .when(orderItemRepository).save(Mockito.any());
        doReturn(ORDER_RECORD_2_WITH_CUSTOMER)
                .when(orderRepository).saveAndFlush(Mockito.any());

        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setParcel(null);
        newOrder.setCustomer(CUSTOMER_RECORD);
        newOrder.setParcel(new Parcel(""));

        List<Item> newItems =  Arrays.asList(ITEM_RECORD_2);
        Customer customerFromDB = new Customer(0l,"Vasilij Petrov", "Moscow, ul. Lenina, d 5");

        var actualResult = orderService.saveOrderWithItemsCustomerAndParcel(newOrder, newItems,customerFromDB);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getOrderId());
        assertEquals(ORDER_ITEM_2_RECORD.getItem().getName(), actualResult.getItems().get(0).getName());
        assertEquals(newItems.get(0).getName(), actualResult.getItems().get(0).getName());
        verify(orderItemRepository).save(Mockito.any());
    }
    @Test
    void shouldSaveNewOrderWithItemFromDB(){
        doReturn(CUSTOMER_RECORD)
                .when(customerRepository).saveAndFlush(Mockito.any());
        doReturn(ORDER_RECORD_1)
                .when(orderRepository).save(Mockito.any());
        doReturn(null)
                .when(orderItemRepository).findByOrderEqualsAndItemEquals(Mockito.any(), Mockito.any());
        doReturn(ITEM_RECORD_1)
                .when(itemRepository).saveAndFlush(Mockito.any());
        doReturn(ORDER_ITEM_1_RECORD)
                .when(orderItemRepository).save(Mockito.any());
        doReturn(ORDER_RECORD_WITH_CUSTOMER)
                .when(orderRepository).saveAndFlush(Mockito.any());

        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setParcel(null);
        newOrder.setCustomer(CUSTOMER_RECORD);
        newOrder.setParcel(new Parcel(""));

        List<Item> itemsFromDb =  Arrays.asList(ITEM_RECORD_1);
        Customer customerFromDB = new Customer(0l,"Vasilij Petrov", "Moscow, ul. Lenina, d 5");

        var actualResult = orderService.saveOrderWithItemsCustomerAndParcel(newOrder, itemsFromDb,customerFromDB);
        assertNotNull(actualResult);
        assertNotNull(actualResult.getOrderId());
        assertEquals(ORDER_ITEM_1_RECORD.getItem().getName(), actualResult.getItems().get(0).getName());
        assertEquals(itemsFromDb.get(0).getName(), actualResult.getItems().get(0).getName());
        verify(orderItemRepository).save(Mockito.any());
    }

    @Test
    void shouldReturnAllOrdersList(){
        List<Order> orders = Arrays.asList(ORDER_RECORD_1, ORDER_RECORD_2);
        doReturn(orders).when(orderRepository).findAll();
        var actualResult = orderService.getAllOrders();

        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        assertEquals(ORDER_RECORD_1.getOrderId(), actualResult.get(0).getOrderId());
        verify(orderRepository).findAll();
    }

    @Test
    void shouldReturnEmptyOrdersListIfOrdersNotFound(){
        doReturn(null)
                .when(orderRepository).findAll();
        var actualResult = orderService.getAllOrders();
        assertNull(actualResult);
        verify(orderRepository).findAll();
    }

    @Test
    void shouldReturnOrderByIdIfExists(){
        doReturn(Optional.of(ORDER_RECORD_1)).when(orderRepository).findById(Mockito.any());

        var actualResult = orderService.getOrderById(Mockito.any());

        assertNotNull(actualResult);
        assertEquals(ORDER_RECORD_1.getOrderId(), actualResult.get().getOrderId());
        verify(orderRepository).findById(Mockito.any());
    }

    @Test
    void  shouldThrowNotFoundExceptionIfOrderByIdNotExists() {
        doReturn(Optional.ofNullable(null))
                .when(orderRepository).findById(any());
        Throwable actualException = assertThrows(NotFoundException.class,
                () -> {
                    orderService.getOrderById(1l);
                });

        assertTrue(actualException.getMessage().contains("Order with \"1\" doesn't exist."));
        verify(orderRepository).findById(any());
    }

    @Test
    void shouldDeleteOrderByIdIfExist(){
        doReturn(Optional.of(ORDER_RECORD_1))
                .when(orderRepository).findById(any());
        List<Order> orders = Arrays.asList(ORDER_RECORD_1, ORDER_RECORD_2);
        doReturn(orders).when(orderRepository).findAll();
        List<Order> expectedOrderList = orderService.getAllOrders();

        orderService.deleteOrderById(1l);

        doReturn(Arrays.asList(ORDER_RECORD_2))
                .when(orderRepository).findAll();

        List<Order> actualOrderList = orderService.getAllOrders();
        assertEquals(expectedOrderList.size()-1, actualOrderList.size());
        verify(orderRepository).deleteById(any());
    }

    @Test
    void shouldThrowNotFoundExceptionIfDeleteOrderByIdNotExists(){
        doReturn(Optional.ofNullable(null))
                .when(orderRepository).findById(any());

        Throwable actualException = assertThrows(NotFoundException.class,
                () -> {
                    orderService.deleteOrderById(1l);
                });

        assertTrue(actualException.getMessage().contains("Order with \"1\" doesn't exist."));
        verify(orderRepository).findById(any());
    }

    @Test
    void shouldReturnOrdersListBySection(){
        List<Order> orders = Arrays.asList(ORDER_RECORD_1);
        doReturn(orders)
                .when(orderRepository).findByStatusOrderByDateSaleDesc(Mockito.any());

        var actualResult =  orderService.getOrdersByStatus(Mockito.any());

        assertNotNull(actualResult);
        assertEquals(orders.size(), actualResult.size());
        assertEquals(orders.get(0).getOrderId(), actualResult.get(0).getOrderItems().get(0).getOrder().getOrderId());
        assertEquals(STATUS_NEW, actualResult.get(0).getOrderItems().get(0).getOrder().getStatus());
        verify(orderRepository).findByStatusOrderByDateSaleDesc(Mockito.any());
    }

    @Test
    void shouldReturnNullIfNotExistsItemsBySection(){
        doReturn(null)
                .when(orderRepository).findByStatusOrderByDateSaleDesc(Mockito.any());

        var actualResult =  orderService.getOrdersByStatus(Mockito.any());

        assertNull(actualResult);
        verify(orderRepository).findByStatusOrderByDateSaleDesc(Mockito.any());
    }
    @Test
    void shouldsetOrderStatus(){
        doReturn(ORDER_RECORD_1)
                .when(orderRepository).getById(Mockito.any());

        orderService.setOrderStatus(Mockito.any(),"COMPLETE");
        doReturn(new Order(0l,"COMPLETE", null))
                .when(orderRepository).getById(Mockito.any());

        var actualResult =orderRepository.getById(0l);

        assertNotNull(actualResult);
        Assertions.assertEquals("COMPLETE", actualResult.getStatus());
    }
    @Test
    void shouldAddTrackNumberToOrderIfNewParcel(){
        doReturn(null)
                .when(parcelRepository).findFirstByTrackNumber(Mockito.any());
        doReturn(new Parcel(TRACK_NUM))
                .when(parcelRepository).save(Mockito.any());

        Order orderWithParcel = ORDER_RECORD_1;
        orderWithParcel.setParcel(new Parcel(TRACK_NUM));

        doReturn(ORDER_RECORD_1)
                .when(orderRepository).getById(Mockito.any());
        doReturn(orderWithParcel)
                .when(orderRepository).saveAndFlush(Mockito.any());

        orderService.addTrackNumberToOrderAndSaveParcel(Mockito.any(),TRACK_NUM);

        doReturn(orderWithParcel)
                .when(orderRepository).getById(Mockito.any());

        var actualResult =orderRepository.getById(ORDER_RECORD_1.getOrderId());
        assertNotNull(actualResult);
        Assertions.assertEquals(TRACK_NUM, actualResult.getParcel().getTrackNumber());
    }

    @Test
    void shouldAddTrackNumberToOrderIfDBExistParcel(){
        doReturn(PARCEL_RECORD_1)
                .when(parcelRepository).findFirstByTrackNumber(Mockito.any());
        doReturn(PARCEL_RECORD_1)
                .when(parcelRepository).saveAndFlush(Mockito.any());

        Order orderWithParcel = ORDER_RECORD_1;
        orderWithParcel.setParcel(PARCEL_RECORD_1);

        doReturn(ORDER_RECORD_1)
                .when(orderRepository).getById(Mockito.any());
        doReturn(orderWithParcel)
                .when(orderRepository).saveAndFlush(Mockito.any());

        orderService.addTrackNumberToOrderAndSaveParcel(Mockito.any(),TRACK_NUM);

        doReturn(orderWithParcel)
                .when(orderRepository).getById(Mockito.any());

        var actualResult =orderRepository.getById(ORDER_RECORD_1.getOrderId());
        assertNotNull(actualResult);
        Assertions.assertEquals(TRACK_NUM, actualResult.getParcel().getTrackNumber());
    }
    @Test
    void shouldsetOrderDeliveredAndReturnParcel(){
        doReturn(PARCEL_RECORD_1)
                .when(parcelRepository).getById(Mockito.any());
        Parcel deliveredParcel = PARCEL_RECORD_1;
        deliveredParcel.setDelivered(true);

        doReturn(deliveredParcel)
                .when(parcelRepository).saveAndFlush(Mockito.any());

        var actualResult = orderService.setOrderDelivered(0l);

        assertNotNull(actualResult);
        assertTrue(actualResult.getDelivered());
        verify(parcelRepository).saveAndFlush(Mockito.any());
    }




}
