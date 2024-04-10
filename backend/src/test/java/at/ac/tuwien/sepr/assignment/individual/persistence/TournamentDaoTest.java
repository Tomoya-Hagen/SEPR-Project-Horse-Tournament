package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class TournamentDaoTest extends TestBase {

  @Autowired
  private TournamentDao tournamentDao;

  @Test
  public void createTournamentSuccessfully() throws ConflictException {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(7);
    List<HorseSelectionDto> participantDtoList = new ArrayList<>();
    participantDtoList.add(new HorseSelectionDto(-30L, "Rosie", LocalDate.of(2016, 6, 28)));
    participantDtoList.add(new HorseSelectionDto(-29L, "Cody", LocalDate.of(2019, 11, 30)));
    participantDtoList.add(new HorseSelectionDto(-28L, "Molly", LocalDate.of(2014, 4, 3)));
    participantDtoList.add(new HorseSelectionDto(-27L, "Buddy", LocalDate.of(2016, 9, 14)));
    participantDtoList.add(new HorseSelectionDto(-26L, "Daisy", LocalDate.of(2017, 12, 1)));
    participantDtoList.add(new HorseSelectionDto(-25L, "Lucky", LocalDate.of(2019, 5, 25)));
    participantDtoList.add(new HorseSelectionDto(-24L, "Rocky", LocalDate.of(2018, 8, 19)));
    participantDtoList.add(new HorseSelectionDto(-23L, "Misty", LocalDate.of(2015, 3, 12)));

    TournamentCreateDto tournamentDto = new TournamentCreateDto("Test Tournament", startDate, endDate, participantDtoList);

    Tournament createdTournament = tournamentDao.create(tournamentDto);

    assertNotNull(createdTournament.getId());
    assertNotNull(createdTournament.getName());
    assertNotNull(createdTournament.getStartDate());
    assertNotNull(createdTournament.getEndDate());
    assertEquals("Test Tournament", createdTournament.getName());
    assertEquals(startDate, createdTournament.getStartDate());
    assertEquals(endDate, createdTournament.getEndDate());
    assertEquals(createdTournament.getEndDate(), createdTournament.getStartDate().plusDays(7));

    participantDtoList.forEach(participantDto -> {
      assertTrue(participantDto.id() < -22 && participantDto.id() > -31);
    });

  }

}
