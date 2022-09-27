package com.ym.projectManager.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ym.projectManager.dto.ParcelTrackingDto;
import com.ym.projectManager.model.Parcel;
import com.ym.projectManager.model.TrackParcel;
import com.ym.projectManager.model.comparator.TrackParcelComparator;
import com.ym.projectManager.repository.ParcelRepository;
import com.ym.projectManager.repository.TrackParcelRepository;
import com.ym.projectManager.service.ParcelService;
import com.ym.projectManager.service.TrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ParcelServiceImpl implements ParcelService {
    private final ParcelRepository parcelRepository;
    private final TrackParcelRepository trackParcelRepository;
    private final TrackerService trackerService;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private String parcelStatusDecoding(int code) {
        switch (code) {
            case 0:
                return "Not found";
            case 10:
                return "In transit";
            case 20:
                return "Expired";
            case 30:
                return "Pick up";
            case 40:
                return "Undelivered";
            case 50:
                return "Delivered";
            case 60:
                return "Alert";
            default:
                return "";
        }

    }

    @Override
    public Parcel createOrUpdateParcel(Parcel parcel){
        if (parcel.getTrackNumber().length() > 1)
            if (parcel.getParcelId() == null) {
                return parcelRepository.save(parcel);
            } else {
                return parcelRepository.saveAndFlush(parcel);
            }
        else {
            return null;
        }
    }

    @Override
    public ParcelTrackingDto getParcelTracking(long parcelId){
        Parcel parcel = parcelRepository.getParcelByParcelId(parcelId);
        return new ParcelTrackingDto(parcel, parcel.getTrackParcels());
    }

    @Override
    public void updateParcelsTracking() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder requestData = new StringBuilder("[");
        Optional<Parcel> notDeliveredParcel = parcelRepository.findFirstByDeliveredIsFalseAndTrackNumberIsNotNullOrderByLastUpdateDesc();
        if (notDeliveredParcel.isPresent()) {
            if(!notDeliveredParcel.get().getLastUpdate().equals(LocalDate.now())){
                Set<Parcel> parcels = parcelRepository.findAllByDeliveredIsFalse();
                if (parcels != null) {
                    parcels.forEach(parcel -> {
                        requestData.append("{\"number\": \"");
                        requestData.append(parcel.getTrackNumber() + "\"},");
                    });
                    requestData.deleteCharAt(requestData.length() - 1).append("]");
                    try {
                        String result = trackerService.orderOnlineByJson(requestData.toString(), "getTrackInfo");
                        JsonObject trackInfo = JsonParser.parseString(result).getAsJsonObject();
                        parseJsonAndSaveData(trackInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void parseJsonAndSaveData(JsonObject trackInfo) {
        String data0 = trackInfo.get("code").toString();

        if (data0.equals("0")) {
            JsonObject data = trackInfo.get("data").getAsJsonObject();
            JsonArray parcels = data.get("accepted").getAsJsonArray();

            for (JsonElement parcel : parcels) {
                String trackNum = parcel.getAsJsonObject().get("number").getAsString();
                JsonObject track = parcel.getAsJsonObject().get("track").getAsJsonObject();
                JsonObject lastStatJson = track.get("z0").getAsJsonObject();
                boolean delivered = track.get("e").toString().equals("40");

                StringBuilder lastStatus = new StringBuilder(lastStatJson.get("a").getAsString()).append(", ");
                lastStatus.append(lastStatJson.get("c").getAsString()).append(", ").append(lastStatJson.get("d").getAsString());
                lastStatus.append(", ").append(lastStatJson.get("z").getAsString());


                JsonArray tracking = track.get("z1").getAsJsonArray();
                Parcel newParcel = new Parcel(trackNum, lastStatus.toString(), LocalDate.now(), delivered);
                Set<TrackParcel> trackParcel = new HashSet<>();

                for (JsonElement trackStatus : tracking) {
                    JsonObject statusJson = trackStatus.getAsJsonObject();
                    String updDate = statusJson.get("a").getAsString();

                    StringBuilder stat = new StringBuilder(statusJson.get("c").getAsString()).append(", ");
                    stat.append(statusJson.get("d").getAsString()).append(", ").append(statusJson.get("z").getAsString());

                    try {
                        trackParcel.add(new TrackParcel(stat.toString(), formatter.parse(updDate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                newParcel.setTrackParcels(trackParcel);
                saveParcel(newParcel);
            }

        }

    }

    private void saveParcel(Parcel parcel) {
        try{
        parcel.setParcelId(parcelRepository.findFirstByTrackNumber(parcel.getTrackNumber()).getParcelId());
        Set<TrackParcel> oldTrackParcel = trackParcelRepository.findAllByParcelParcelIdOrderByTrackParcelIdDesc(parcel.getParcelId());
        Set<TrackParcel> newTrackParcel = parcel.getTrackParcels();
        if (oldTrackParcel != null) {
            newTrackParcel.removeAll(oldTrackParcel);
        }

        if (newTrackParcel.size() > 0) {
            newTrackParcel.stream().sorted(new TrackParcelComparator().reversed()).forEach(status -> {
                status.setParcel(parcel);
                status.setTrackParcelId(trackParcelRepository.save(status).getTrackParcelId());
            });

            parcel.setTrackParcels(trackParcelRepository.findAllByParcelOrderByTrackParcelIdDesc(parcel.getParcelId()));
            parcelRepository.saveAndFlush(parcel);
        } else {
            parcel.setTrackParcels(oldTrackParcel);
            parcelRepository.saveAndFlush(parcel);
        }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
