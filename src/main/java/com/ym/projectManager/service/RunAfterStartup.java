package com.ym.projectManager.service;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RunAfterStartup {
    private final ParcelService parcelService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional()
    public void runAfterStartup() {
        parcelService.updateParcelsTracking();
    }

}