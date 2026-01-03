package com.rocketseat.trip_planner.trip;

import com.rocketseat.trip_planner.activity.ActivityData;
import com.rocketseat.trip_planner.activity.ActivityRequestPayload;
import com.rocketseat.trip_planner.activity.ActivityResponse;
import com.rocketseat.trip_planner.link.*;
import com.rocketseat.trip_planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(this.tripService.getTripDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        return ResponseEntity.ok(this.tripService.updateTrip(id, payload));
    }


    @PostMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.confirmTripAndSendEmailToParticipants(id));

    }

    // end Trip and start activity
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        ActivityResponse response = tripService.registerActivityWithRequestBody(id, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id ) {
        List<LinkData> linkData = this.tripService.findAllLinksByTripId(id);

        return ResponseEntity.ok(linkData);
    }
}
