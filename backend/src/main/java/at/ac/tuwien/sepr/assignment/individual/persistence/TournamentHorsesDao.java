package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;

import java.util.Collection;

public interface TournamentHorsesDao {

  /**
  * Get the horses participating in the tournament with the given id.
  *
  * @param id the id of the tournament
  * @return A collection of the horses participating in the tournament
  */
  Collection<HorseTournament> getHorseByIDTournaments(Long id);
}
