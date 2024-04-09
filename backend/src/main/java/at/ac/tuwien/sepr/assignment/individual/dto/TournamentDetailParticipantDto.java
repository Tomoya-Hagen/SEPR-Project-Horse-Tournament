package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO record for the details of a single participant in a tournament.
 *
 * @param horseId the ID of the horse
 * @param name the name of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param entryNumber the entry number of the horse
 * @param roundReached the number of the reached round of the horse
 */
public record TournamentDetailParticipantDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    int entryNumber,
    int roundReached
) {
}
