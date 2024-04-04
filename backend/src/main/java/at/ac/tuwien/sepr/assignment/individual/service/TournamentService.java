package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service for working with tournaments.
 */
public interface TournamentService {

  /**
   * Search for tournaments in the persistent data store matching all provided fields.
   *
   * @param searchParams the search parameters to use in filtering.
   * @return the tournaments where the given fields match.
   */
  Stream<TournamentListDto> search(TournamentSearchParamsDto searchParams) throws NotFoundException;

  /**
   * Create a tournament with the data given.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data given for the tournament is in itself incorrect
   * @throws NotFoundException if a horse with the given id does not exist
   */
  TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException, NotFoundException;

  TournamentStandingsDto getStandingsByTournamentId(Long tournamentId) throws NotFoundException;

  TournamentStandingsDto updateStandings(TournamentStandingsDto standings) throws NotFoundException, ValidationException;

  int fillTree(TournamentStandingsTreeDto standings, List<HorseTournament> horseTournaments, int entryNumber);
}
