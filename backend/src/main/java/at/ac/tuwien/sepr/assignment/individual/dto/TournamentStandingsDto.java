package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

/**
 * DTO to bundle the tournament standings.
 *
 * @param id the ID of the tournament
 * @param name the name of the tournament
 * @param participants the participants of the tournament
 * @param tree the standings tree of the tournament
 */
public record TournamentStandingsDto(
    Long id,
    String name,
    List<TournamentDetailParticipantDto> participants,
    TournamentStandingsTreeDto tree
) {
}
