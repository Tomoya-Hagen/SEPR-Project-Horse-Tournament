package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

/**
 * DTO to bundle the tournament standings.
 */
public record TournamentStandingsDto(
    Long id,
    String name,
    List<TournamentDetailParticipantDto> participants,
    TournamentStandingsTreeDto tree
) {
}
