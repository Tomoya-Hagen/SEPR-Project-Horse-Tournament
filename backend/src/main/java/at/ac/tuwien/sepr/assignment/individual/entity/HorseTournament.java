package at.ac.tuwien.sepr.assignment.individual.entity;

public record HorseTournament(
    Long horseId,
    Long tournamentId,
    int entryNumber,
    int roundReached
) { }
