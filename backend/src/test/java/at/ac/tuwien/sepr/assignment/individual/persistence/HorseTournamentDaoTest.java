package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseTournamentDaoTest extends TestBase {

  @Autowired
  private HorseTournamentDao horseTournamentDao;

  @Test
  public void getHorsesByIDTournamentReturnsCorrectData() {
    Long tournamentId = -1L;
    Collection<HorseTournament> horses = horseTournamentDao.getHorsesByIDTournament(tournamentId);

    assertEquals(8, horses.size());

    horses.forEach(horse -> {
      assertEquals(horse.getTournamentId(), -1);
      assertThat(horse.getRoundReached()).isNotNegative(); //the round reached default value is 0
      assertThat(horse.getHorseId()).isGreaterThan(-20); //is at least -19
      assertThat(horse.getEntryNumber()).isGreaterThan(-2); //entry no default = -1
      assertThat(horse.getEntryNumber()).isLessThan(9); // is at most 8
    });
  }

  @Test
  public void updateStandingsThrowsConflictExceptionForNonExistingTournament() {
    Long nonExistingTournamentId = -999L;
    Long horseId = 1L;
    int entryNumber = 1;
    int roundReached = 2;

    assertThrows(ConflictException.class, () -> horseTournamentDao.updateStandings(nonExistingTournamentId, horseId, entryNumber, roundReached));
  }




}
