package ru.practicum.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private LocalDateTime eventDate;
    private String title;
    private String annotation;
    private String description;

    @Embedded
    private EventLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator;

    private Integer participantLimit;
    private Boolean isPaymentRequired;
    private Boolean requestModeration;
    private Integer confirmedRequestsCount;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private LocalDateTime publishedOn;
    private LocalDateTime createdOn;

    @Transient
    private Long views;
}