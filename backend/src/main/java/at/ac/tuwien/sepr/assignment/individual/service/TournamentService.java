package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
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
   * @throws ValidationException if the data given for the tournament is in itself incorrect
   */
  TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException, NotFoundException;
}
