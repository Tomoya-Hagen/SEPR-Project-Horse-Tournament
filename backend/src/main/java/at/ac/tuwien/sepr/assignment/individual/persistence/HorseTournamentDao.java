package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for DAO for working with horses in a tournament.
 * Implements access functionality to the application's persistent data store regarding horses in a tournament.'
 */
public interface HorseTournamentDao {

  /**
  * Get the horses participating in the tournament with the given id.
  *
  * @param tournamentId the id of the tournament
  * @return A collection of the horses participating in the tournament
  */
  Collection<HorseTournament> getHorsesByIDTournament(Long tournamentId);

  /**
   * Update the tournament with the given data.
   *
   * @param tournamentId the id of the tournament to update
   * @param horseId the id of the horse to update
   * @param entryNumber the entry number of the horse to update
   * @param roundReached the number of the round reached by the horse to update
   */
  void updateStandings(Long tournamentId, Long horseId, int entryNumber, int roundReached) throws ConflictException;

  /**
  * Get the horses of the tournaments with the given ids.
  *
  * @param tournamentIds the ids of the tournament to get the standings for.
  * @return a mapping of tournamentIds and the corresponding horses participating in the tournaments.
  */
  Map<Long, List<HorseTournament>> getHorsesByIDsTournaments(Set<Long> tournamentIds);

  /**
   * Checks if the horse with the given id participates in any tournament
   *
   * @param horseId the id of the horse to check
   * @return the HorseTournament object (-s) with the given id if it participates in any tournament, or else null
   */
  List<HorseTournament> getParticipatingHorse(Long horseId);

}
