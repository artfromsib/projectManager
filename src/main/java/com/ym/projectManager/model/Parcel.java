package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.*;
import com.ym.projectManager.model.comparator.TrackParcelComparator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "parcel")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "parcelId"
)
public class Parcel {
    @Id
    @Column(name = "parcel_id", nullable = false)
    @SequenceGenerator(name = "parcel_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parcel_id_seq")
    private Long parcelId;
    private String trackNumber;
    private String lastStatus;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate shippingDate;

    @JsonIgnore
    @OneToMany(mappedBy = "parcel", fetch = FetchType.LAZY)
    private Set<TrackParcel> trackParcels;

    private Boolean delivered = false;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastUpdate = LocalDate.now();

    @JsonIgnore
    @OneToMany(mappedBy = "parcel", fetch = FetchType.LAZY)
    private Set<Order> parcelOrders = new HashSet<>();


    public Parcel(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Parcel(String trackNumber, String lastStatus, LocalDate lastUpdate, boolean delivered) {
        this.trackNumber = trackNumber;
        this.lastStatus = lastStatus;
        this.lastUpdate = lastUpdate;
        this.delivered = delivered;
    }

    public Parcel(Long parcelId, String trackNumber) {
        this.parcelId = parcelId;
        this.trackNumber = trackNumber;
    }

    public Parcel(String trackNumber, LocalDate shippingDate) {
        this.trackNumber = trackNumber;
        this.shippingDate = shippingDate;
    }

    @Transient
    public Set<TrackParcel> getTrackParcels() {
        return this.trackParcels.stream().sorted(new TrackParcelComparator().reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
