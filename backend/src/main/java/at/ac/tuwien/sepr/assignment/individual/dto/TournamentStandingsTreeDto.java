package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

/**
 * DTO class for the tournament tree.
 *
 * @param thisParticipant the current participant in the standings tree
 * @param branches the branches of the standings tree
 */
public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    List<TournamentStandingsTreeDto> branches
) {
}
