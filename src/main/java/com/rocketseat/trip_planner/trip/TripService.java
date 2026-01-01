package com.rocketseat.trip_planner.trip;

import com.rocketseat.trip_planner.activity.ActivityRequestPayload;
import com.rocketseat.trip_planner.activity.ActivityResponse;
import com.rocketseat.trip_planner.activity.ActivityService;
import com.rocketseat.trip_planner.link.Link;
import com.rocketseat.trip_planner.link.LinkRequestPayload;
import com.rocketseat.trip_planner.link.LinkResponse;
import com.rocketseat.trip_planner.link.LinkService;
import com.rocketseat.trip_planner.participant.ParticipantCreateResponse;
import com.rocketseat.trip_planner.participant.ParticipantRequestPayload;
import com.rocketseat.trip_planner.participant.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // to-do: validateDates(payload.starts_at(), payload.ends_at());

        Trip newTrip = new Trip(payload);
        this.tripRepository.save(newTrip);

        participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return newTrip;
    }

    public Optional<Trip> getTripDetails(UUID id) {
        return this.tripRepository.findById(id);
    }

    public Optional<Trip> updateTrip(UUID id, TripRequestPayload payload) {
        Optional<Trip> trip  = this.tripRepository.findById(id);

        // to-do: validateDates(payload.starts_at(), payload.ends_at());

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
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
                .map(trip -> {
                    // to-do: validateDate(payload.occurs_at());

                    return this.activityService.saveActivity(payload, trip);
                });
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
}
