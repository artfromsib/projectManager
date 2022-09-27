package com.ym.projectManager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "track_parcel")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TrackParcel implements Comparable<TrackParcel>{
    @Id
    @Column( nullable = false)
    @SequenceGenerator(name = "track_parcel_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_parcel_id_seq")
    private Long trackParcelId;
    private String status = "";
    private Date date;

    @JsonBackReference(value = "parcel")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    public TrackParcel(String status, Date date) {
        this.status = status;
        this.date = date;

    }

    @Override
    public int compareTo(TrackParcel o) {
        return this.getDate().compareTo(o.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackParcel that = (TrackParcel) o;

        return status.equals(that.status) && date.getTime()==(that.date.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, date);
    }
}
