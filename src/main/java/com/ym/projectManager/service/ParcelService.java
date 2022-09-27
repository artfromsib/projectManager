package com.ym.projectManager.service;

import com.ym.projectManager.dto.ParcelTrackingDto;
import com.ym.projectManager.model.Parcel;

public interface ParcelService {

    Parcel createOrUpdateParcel(Parcel parcel);

    ParcelTrackingDto getParcelTracking(long parcelId);

    void updateParcelsTracking();

}
