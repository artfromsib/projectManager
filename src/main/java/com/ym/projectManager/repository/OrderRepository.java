package com.ym.projectManager.repository;

import com.ym.projectManager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByDateSaleDesc(String name);
    Optional<Order> getByOrderId(Long id);

}
