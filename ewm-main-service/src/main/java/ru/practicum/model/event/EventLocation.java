package ru.practicum.model.event;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class EventLocation {
    @Column(name = "location_lat")
    private Double lat;

    @Column(name = "location_lon")
    private Double lon;
}