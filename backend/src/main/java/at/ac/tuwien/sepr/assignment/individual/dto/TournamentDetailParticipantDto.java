package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO record for the details of a single participant in a tournament.
 */
public record TournamentDetailParticipantDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    int entryNumber,
    int roundReached
) {
}
