package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "orderId"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    @Id
    @Column(name = "order_id", nullable = false)
    @SequenceGenerator(name = "order_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    private Long orderId;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDateTime dateSale = LocalDateTime.now();

    @JsonBackReference
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Double deliveryCost;
    private String status;
    private Double orderTotal = 0.0;
    private Integer countItems;

    @JsonBackReference(value = "order")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @JsonManagedReference(value = "order")
    @NotNull
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    public Order(Long orderId, String status, Parcel parcel) {
        this.orderId = orderId;
        this.status = status;
        this.parcel = parcel;
    }

    public Order(Long orderId, Customer customer, String status, Set<OrderItem> orderItems) {
        this.orderId = orderId;
        this.customer = customer;
        this.status = status;
        this.orderItems = orderItems;
    }

    @JsonIgnore
    @Transient
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        this.orderItems.stream().forEach(item -> items.add(item.getItem()));
        return items;
    }


}



