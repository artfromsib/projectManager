package com.ym.projectManager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ym.projectManager.model.Parcel;
import com.ym.projectManager.model.TrackParcel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ParcelTrackingDto {
    private Parcel parcel;
    private Set<TrackParcel> trackParcel;
}
