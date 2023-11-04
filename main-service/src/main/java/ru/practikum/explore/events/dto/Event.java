package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.user.dto.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(min = 20, max = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created")
    @NotNull
    private LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "event_date")
    @NotNull
    private LocalDateTime eventDate;
    @Size(min = 20, max = 7000)
    private String description;
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private StatusEvent state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Size(min = 3, max = 120)
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;


}
