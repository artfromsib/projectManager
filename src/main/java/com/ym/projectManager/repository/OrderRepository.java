package com.ym.projectManager.repository;

import com.ym.projectManager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByDateSaleDesc(String name);
    Order getById(Long id);

}
