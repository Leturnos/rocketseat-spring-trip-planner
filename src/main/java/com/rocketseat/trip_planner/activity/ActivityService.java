package com.rocketseat.trip_planner.activity;

import com.rocketseat.trip_planner.exception.InvalidDateException;
import com.rocketseat.trip_planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse saveActivity(ActivityRequestPayload payload, Trip trip) {
        LocalDateTime occurrenceDate = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (trip.getStartsAt().isAfter(occurrenceDate) || trip.getEndsAt().isBefore(occurrenceDate)) {
            throw new InvalidDateException("A data da atividade deve estar entre o início e o término da viagem");
        }
        Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);
        this.activityRepository.save(newActivity);
        return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID tripId) {
        return this.activityRepository.findByTripId(tripId).stream().map(activity -> new ActivityData(activity.getId(),activity.getTitle(),activity.getOccursAt())).toList();
    }
}
