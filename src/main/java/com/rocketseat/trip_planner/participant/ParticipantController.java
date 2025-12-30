package com.rocketseat.trip_planner.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        return participantService.confirmParticipantFromId(id, payload)
                .map(participant -> ResponseEntity.ok(participant))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
