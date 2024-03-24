package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournaments";
  private static final String SQL_SELECT_SEARCH = "SELECT "
      + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\", t.end_date as \"end_date\""
      + " FROM " + TABLE_NAME + " t"
      + " WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%'))"
      + "  AND (:startEarliest IS NULL OR :startEarliest <= t.start_date)"
      + "  AND (:startLatest IS NULL OR :startLatest >= t.start_date)";
  //waiting to be verified
  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(
      JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }


  private Tournament mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Tournament()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setStartDate(rs.getDate("start_date").toLocalDate())
        .setEndDate(rs.getDate("end_date").toLocalDate())
        ;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    String query = SQL_SELECT_SEARCH;
    if (searchParams.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParams);
    params.registerSqlType("name", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);
  }
}
