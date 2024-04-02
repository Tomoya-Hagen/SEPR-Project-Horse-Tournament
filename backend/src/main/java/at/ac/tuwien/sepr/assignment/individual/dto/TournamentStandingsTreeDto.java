package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    List<TournamentStandingsTreeDto> branches
) {
}
