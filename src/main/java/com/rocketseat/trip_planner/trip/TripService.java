package com.rocketseat.trip_planner.trip;

import com.rocketseat.trip_planner.participant.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;

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
}
