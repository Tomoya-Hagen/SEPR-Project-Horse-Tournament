package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

public record TournamentDetailParticipantDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    int entryNumber,
    int roundReached
) {
}
