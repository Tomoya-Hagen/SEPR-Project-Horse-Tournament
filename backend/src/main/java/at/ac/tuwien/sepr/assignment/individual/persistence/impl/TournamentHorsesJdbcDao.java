package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentHorsesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class TournamentHorsesJdbcDao implements TournamentHorsesDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament_horses";

  private static final String SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS = "SELECT "
      + " horse_id, entry_number, round_reached, tournament_id"
      + " FROM " + TABLE_NAME
      + " WHERE tournament_id = ? ";

  private static final String SQL_INSERT_HORSE_TOURNAMENT = "INSERT INTO "
      + TABLE_NAME + "(horse_id, tournament_id, entry_number, round_reached) "
      + "VALUES (?, ?, ?, ?)";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentHorsesJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
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
  public Collection<HorseTournament> getHorseByIDTournaments(Long id) {
    LOG.trace("getHorseByIDTournaments({})", id);
    return jdbcTemplate.query(SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS, this::mapHorseRow, id);
  }

}
