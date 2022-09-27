package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "section")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemSection {

    @Id
    @Column(name = "section_id", nullable = false)
    @SequenceGenerator(name = "section_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_id_seq")
    private Long itemSectionId;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "itemSection", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    private Set<Item> items;

    public ItemSection(String name) {
        this.name = name;
    }

    public ItemSection(Long ItemSectionId, String name) {
        this.itemSectionId = ItemSectionId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSection section = (ItemSection) o;
        return Objects.equals(itemSectionId, section.itemSectionId) && Objects.equals(name, section.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemSectionId, name);
    }
}
