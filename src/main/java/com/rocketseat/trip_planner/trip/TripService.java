package com.rocketseat.trip_planner.trip;

import com.rocketseat.trip_planner.activity.ActivityData;
import com.rocketseat.trip_planner.activity.ActivityRequestPayload;
import com.rocketseat.trip_planner.activity.ActivityResponse;
import com.rocketseat.trip_planner.activity.ActivityService;
import com.rocketseat.trip_planner.exception.InvalidDateException;
import com.rocketseat.trip_planner.link.*;
import com.rocketseat.trip_planner.participant.ParticipantCreateResponse;
import com.rocketseat.trip_planner.participant.ParticipantData;
import com.rocketseat.trip_planner.participant.ParticipantRequestPayload;
import com.rocketseat.trip_planner.participant.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @Transactional
    public Trip registerTrip(TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);

        validateDates(newTrip.getStartsAt(), newTrip.getEndsAt());

        this.tripRepository.save(newTrip);

        participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return newTrip;
    }

    public Optional<Trip> getTripDetails(UUID id) {
        return this.tripRepository.findById(id);
    }

    public Optional<Trip> updateTrip(UUID id, TripRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();

            LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

            if (endsAt.isBefore(startsAt)) {
                throw new InvalidDateException("A data de término não pode ser anterior à data de início.");
            }

            rawTrip.setStartsAt(startsAt);
            rawTrip.setEndsAt(endsAt);
            rawTrip.setDestination(payload.destination());

            this.tripRepository.save(rawTrip);
        }
        return trip;
    }

    @Transactional
    public Optional<Trip> confirmTripAndSendEmailToParticipants(UUID id) {
        Optional<Trip> trip  = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.tripRepository.save(rawTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);

        }
        return trip;
    }

    public Optional<ActivityResponse> registerActivityWithRequestBody(UUID id, ActivityRequestPayload payload) {
        return this.tripRepository.findById(id)
                .map(trip -> this.activityService.saveActivity(payload, trip));
    }

    public List<ActivityData> findAllActivitiesByTripId(UUID id) {
        return this.activityService.getAllActivitiesFromId(id);
    }

    public List<ParticipantData> findAllParticipantsByTripId(UUID id) {
        return this.participantService.getAllParticipantsFromEvent(id);
    }

    @Transactional
    public ParticipantCreateResponse inviteParticipantByEmail(UUID id, ParticipantRequestPayload payload) {
        Trip trip = this.tripRepository.findById(id).orElseThrow();
        // to-do: Treat error if there is no trip with the id

        ParticipantCreateResponse participantCreateResponse = this.participantService.registerParticipantToEvent(payload.email(), trip);

        if (trip.getIsConfirmed()) {
            this.participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return participantCreateResponse;
    }

    public LinkResponse registerLink(UUID id, LinkRequestPayload payload) {
        Trip trip = this.tripRepository.findById(id).orElseThrow();
        // to-do: Treat error if there is no trip with the id

        LinkResponse linkResponse = this.linkService.saveLink(payload, trip);

        return linkResponse;

    }

    public List<LinkData> findAllLinksByTripId(UUID id) {
        return this.linkService.getAllLinksFromTrip(id);
    }

    // methods
    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new InvalidDateException("A data de início não pode ser no passado.");
        }
        if (end.isBefore(start)) {
            throw new InvalidDateException("A data de término não pode ser anterior à data de início.");
        }
    }
}
