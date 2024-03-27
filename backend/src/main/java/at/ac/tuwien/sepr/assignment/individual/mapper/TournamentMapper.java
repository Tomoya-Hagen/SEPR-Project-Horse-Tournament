package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class TournamentMapper {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a tournament entity object to a {@link TournamentListDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentListDto}
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate()
    );
  }

  /**
   * Convert a tournament entity object to a {@link TournamentDetailDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentDetailDto entityToDetailDto(Tournament tournament, List<TournamentDetailParticipantDto> participants) {
    LOG.trace("entityToDetailDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate(),
        participants
    );
  }

  public TournamentDetailParticipantDto entityToTournamentDetailParticipantDto(HorseSelectionDto horseSelectionDto,
                                                                               int entryNo, int roundReached) {
    LOG.trace("entityToTournamentDetailParticipantDto({})", horseSelectionDto);
    if (horseSelectionDto == null) {
      return null;
    }

    return new TournamentDetailParticipantDto(
        horseSelectionDto.id(),
        horseSelectionDto.name(),
        horseSelectionDto.dateOfBirth(),
        entryNo,
        roundReached
    );
  }

}
