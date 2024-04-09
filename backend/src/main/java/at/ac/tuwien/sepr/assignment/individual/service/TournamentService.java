package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
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
  Stream<TournamentListDto> search(TournamentSearchParamsDto searchParams);

  /**
   * Create a tournament with the data given.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data given for the tournament is invalid
   */
  TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException;

  /**
   * Get the tournament standings with the given id.
   *
   * @param tournamentId the id of the tournament to get the standings for.
   * @return the standings for the given tournament.
   */
  TournamentStandingsDto getStandingsByTournamentId(Long tournamentId) throws NotFoundException;

  /**
   * Update the standings for the given tournament.
   *
   * @param standings the standings to update.
   * @return the updated standings.
   * @throws ValidationException if the data given for the standings is invalid
   */
  TournamentStandingsDto updateStandings(TournamentStandingsDto standings) throws ValidationException, NotFoundException;

  /**
   * Fill the tournament standings tree.
   *
   * @param standings the standings to fill.
   * @param horseTournaments the horse tournaments to fill the tree with.
   * @param entryNumber the entry number to start filling the tree with.
   * @return the last entry number after filling the tree.
   */
  int fillTree(TournamentStandingsTreeDto standings, List<HorseTournament> horseTournaments, int entryNumber);

  /**
   * Get the horses of the tournaments with the given ids.
   *
   * @param tournamentId the id of the tournament to get the standings for.
   * @return the horses participating in the tournaments
   * @throws NotFoundException if the tournament with the given id or the tournaments from the past 12months could not be found.
   */
  List<TournamentDetailParticipantDto> calculatePointsForHorses(Long tournamentId) throws NotFoundException;

  /**
   * Generate the first round of the standings.
   *
   * @param standings the standings to generate the first round of.
   * @return the standings after the first round has been generated.
   * @throws NotFoundException if the tournament with the given id or the tournaments from the past 12months could not be found.
   */
  TournamentStandingsDto generateFirstRound(TournamentStandingsDto standings) throws NotFoundException;

}
