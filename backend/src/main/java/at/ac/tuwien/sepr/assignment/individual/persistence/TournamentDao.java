package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;

import java.util.Collection;

public interface TournamentDao {

  /**
   * Return the tournaments that match with the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParams the parameters to use in searching.
   * @return the tournaments where all given parameters match.
   */
  Collection<Tournament> search(TournamentSearchParamsDto searchParams);


  /**
   * Create a new tournament with the given data.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   */
  Tournament create(TournamentCreateDto tournament);


  Tournament getById(Long tournamentId);

  void updateStandings(Long tournamentId, Long horseId, int entryNumber, int roundReached);
}
