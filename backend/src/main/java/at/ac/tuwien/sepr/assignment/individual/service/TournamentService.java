package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;

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
  Stream<TournamentListDto> search(TournamentSearchDto searchParams);
}
