package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "itemId"
)

public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @SequenceGenerator(name = "item_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
    private Long itemId;
    private String name;
    private Integer quantity = 0;
    private Double price = 0.0;
    private String status;
    private String urlImage;
    private String variations;
    private Long listingId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id")
    private ItemSection itemSection = new ItemSection(0L, "null", null);

    @Transient
    private String tempSection;
    @JsonIgnore
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();


    public Item(Long itemId, String name, Integer quantity, Double price, String status, ItemSection itemSection) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.itemSection = itemSection;
    }
    public Item(String name, Integer quantity, Double price, String status, ItemSection itemSection) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.itemSection = itemSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemId.equals(item.itemId) && Objects.equals(name, item.name) && Objects.equals(quantity, item.quantity) && Objects.equals(price, item.price) && Objects.equals(status, item.status) && Objects.equals(urlImage, item.urlImage) && Objects.equals(variations, item.variations) && Objects.equals(listingId, item.listingId) && Objects.equals(itemSection, item.itemSection) && Objects.equals(tempSection, item.tempSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, name, quantity, price, status, urlImage, variations, listingId, itemSection, tempSection);
    }
}
