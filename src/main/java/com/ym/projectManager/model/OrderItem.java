package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name="OrderItemEntity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "item")
@Table(name="order_item")
public class OrderItem {


    @JsonIgnore
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "order_item_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_id_seq")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer count;
    private Double price;
    private Double sumValue;

    public OrderItem(Item item, Order order, Integer count, Double price, Double sumValue) {
        this.item = item;
        this.order = order;
        this.count = count;
        this.price = price;
        this.sumValue = sumValue;
    }


}
