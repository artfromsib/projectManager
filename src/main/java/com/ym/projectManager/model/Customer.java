package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="customer", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {
    @Id
    @Column(name = "customer_id", nullable = false)
    @SequenceGenerator(name = "customer_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    private Long customerId;
    private Long userOtherId;
    @NotNull(message = "Customer name is required.")
    @NotBlank
    private String fullName;
    private String address;
    private Integer countOrders = 1;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private Set<Order> customerOrders = new HashSet<>();

    public Customer(Long customerId, String fullName, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.address = address;
    }

    public Customer(String fullName, String address) {
        this.fullName = fullName;
        this.address = address;
    }

    @Override
    public String toString() {
        return "To: " + fullName + ", address: " + address;
    }
}
