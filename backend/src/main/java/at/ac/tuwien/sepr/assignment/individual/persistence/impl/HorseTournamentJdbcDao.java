package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class HorseTournamentJdbcDao implements HorseTournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament_horses";

  private static final String SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS = "SELECT "
      + " horse_id, entry_number, round_reached, tournament_id"
      + " FROM " + TABLE_NAME
      + " WHERE tournament_id = ? ";

  private final JdbcTemplate jdbcTemplate;

  public HorseTournamentJdbcDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  private HorseTournament mapHorseRow(ResultSet rs, int rowNum) throws SQLException {
    return new HorseTournament(
        rs.getLong("horse_id"),
        rs.getLong("tournament_id"),
        rs.getInt("entry_number"),
        rs.getInt("round_reached")
    );
  }

  @Override
  public Collection<HorseTournament> getHorsesByIDTournament(Long tournamentId) {
    LOG.trace("getHorseByIDTournaments({})", tournamentId);
    return jdbcTemplate.query(SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS, this::mapHorseRow, tournamentId);
  }

}
