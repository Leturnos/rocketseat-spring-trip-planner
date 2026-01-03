package com.rocketseat.trip_planner.trip;

import com.rocketseat.trip_planner.activity.ActivityData;
import com.rocketseat.trip_planner.activity.ActivityRequestPayload;
import com.rocketseat.trip_planner.activity.ActivityResponse;
import com.rocketseat.trip_planner.activity.ActivityService;
import com.rocketseat.trip_planner.link.*;
import com.rocketseat.trip_planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // start Trip
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){

        Trip newTrip = tripService.registerTrip(payload);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        return tripService.getTripDetails(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        return tripService.updateTrip(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        // to-do: handle data errors
    }


    @PostMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        return tripService.confirmTripAndSendEmailToParticipants(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // end Trip and start activity
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        return tripService.registerActivityWithRequestBody(id, payload)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id ) {
        List<ActivityData>  activityData = this.tripService.findAllActivitiesByTripId(id);

        return ResponseEntity.ok(activityData);
    }

    // end Activity and start Participant
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id ) {
        List<ParticipantData> participantList = this.tripService.findAllParticipantsByTripId(id);

        return ResponseEntity.ok(participantList);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        ParticipantCreateResponse response = tripService.inviteParticipantByEmail(id, payload);
        return ResponseEntity.ok(response);
    }

    // end Participant and start Link
    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        LinkResponse response = tripService.registerLink(id, payload);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id ) {
        List<LinkData> linkData = this.tripService.findAllLinksByTripId(id);

        return ResponseEntity.ok(linkData);
    }
}
