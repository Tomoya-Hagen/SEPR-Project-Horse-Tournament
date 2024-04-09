package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO record to bundle the details of a tournament.
 *
 * @param id id of the tournament
 * @param name name of the tournament
 * @param startDate start date of the tournament
 * @param endDate end date of the tournament
 * @param participants list of participants of the tournament
 */
public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    List<TournamentDetailParticipantDto> participants
) { }
