package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

  public TournamentStandingsDto entityToTournamentStandingsDto(Tournament tournament, Collection<HorseTournament> horseTournaments,
                                                               Map<Long, HorseSelectionDto> horses) {
    LOG.trace("entityToTournamentStandingsDto({})", tournament);
    if (tournament == null || horseTournaments == null || horses == null) {
      return null;
    }
    List<TournamentDetailParticipantDto> participants = new ArrayList<>();
    for (HorseTournament horseTournament : horseTournaments) {
      HorseSelectionDto horse = horses.get(horseTournament.getHorseId());
      participants.add(entityToTournamentDetailParticipantDto((new HorseSelectionDto(horse.id(), horse.name(),
          horse.dateOfBirth())), horseTournament.getEntryNumber(), horseTournament.getRoundReached()));
    }
    TournamentStandingsTreeDto root = generateTree(null, 1);
    int number = 0;
    for (HorseTournament participant : horseTournaments) {
      if (participant.getEntryNumber() == 0) {
        number++;
      }
    }
    if (number == 8) {
      return new TournamentStandingsDto(
          tournament.getId(),
          tournament.getName(),
          participants,
          root
      );
    }
    List<TournamentDetailParticipantDto> participantsSortedByEntryNumber = new ArrayList<>(participants);
    participantsSortedByEntryNumber.sort(Comparator.comparingInt(TournamentDetailParticipantDto::entryNumber));
    root = fillStandingsTree(root, participantsSortedByEntryNumber, 4);

    return new TournamentStandingsDto(
        tournament.getId(),
        tournament.getName(),
        participants,
        root
    );
  }

  private TournamentStandingsTreeDto generateTree(TournamentStandingsTreeDto root, int i) {
    LOG.trace("createTree({})", root);
    if (i <= 3) {
      if (root == null) {
        root = new TournamentStandingsTreeDto(null, new ArrayList<>());
      }
      root.branches().add(generateTree(null, i + 1));
      root.branches().add(generateTree(null, i + 1));
    } else {
      root = new TournamentStandingsTreeDto(null, null);
    }
    return root;
  }
  private TournamentStandingsTreeDto fillStandingsTree(TournamentStandingsTreeDto root, List<TournamentDetailParticipantDto> participants, int depth) {
    LOG.trace("fillStandingsTree({})", root);
    if (participants.size() == 1) {
      root = new TournamentStandingsTreeDto(participants.get(0), root.branches());
      return root;
    }
    int maxRound = 0;
    TournamentDetailParticipantDto maxRoundParticipant = null;
    for (TournamentDetailParticipantDto participantDto : participants) {
      if (participantDto.roundReached() > maxRound && participantDto.roundReached() >= depth) {
        maxRound = participantDto.roundReached();
        maxRoundParticipant = participantDto;
      }
    }
    root = new TournamentStandingsTreeDto(maxRoundParticipant, root.branches());

    if (root.branches() != null && root.branches().size() == 2) {
      root.branches().set(0, fillStandingsTree(root.branches().get(0), participants.subList(0, participants.size() / 2), depth - 1));
      root.branches().set(1, fillStandingsTree(root.branches().get(1), participants.subList(participants.size() / 2,
          participants.size()), depth - 1));
    }
    return root;
  }

  public HorseTournament tournamentDetailParticipantDtoToHorseTournament(TournamentDetailParticipantDto horse, Long tournamentId) {
    LOG.trace("tournamentDetailParticipantDtoToEntity({})", horse);
    if (horse == null) {
      return null;
    }
    return new HorseTournament(
        horse.horseId(),
        tournamentId,
        0,
        0
    );

  }
}
