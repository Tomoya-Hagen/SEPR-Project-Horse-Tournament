package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO record to bundle the details of a tournament.
 */
public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    List<TournamentDetailParticipantDto> participants
) { }
