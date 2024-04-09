package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

/**
 * DTO class for the tournament tree.
 */
public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    List<TournamentStandingsTreeDto> branches
) {
}
