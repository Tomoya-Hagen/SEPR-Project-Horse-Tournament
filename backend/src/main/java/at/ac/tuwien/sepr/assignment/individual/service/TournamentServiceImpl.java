package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final HorseDao horseDao;
  private final HorseTournamentDao horseTournamentDao;
  private final TournamentMapper tournamentMapper;
  private final HorseMapper horseMapper;
  private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao tournamentDao, HorseDao horseDao, HorseTournamentDao horseTournamentDao,
                               TournamentMapper mapper, HorseMapper horseMapper, TournamentValidator validator) {
    this.tournamentDao = tournamentDao;
    this.horseDao = horseDao;
    this.horseTournamentDao = horseTournamentDao;
    this.tournamentMapper = mapper;
    this.horseMapper = horseMapper;
    this.validator = validator;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchParamsDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream()
        .map(tournamentMapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    var createdTournament = tournamentDao.create(tournament);
    var horseTournaments = horseTournamentDao.getHorsesByIDTournament(createdTournament.getId());
    List<TournamentDetailParticipantDto> participantDtos = new ArrayList<>();
    for (HorseSelectionDto participantDto : tournament.participants()) {
      horseTournaments
          .stream()
          .filter(ht -> ht.getHorseId().equals(participantDto.id()))
          .findFirst().ifPresent(horseTournament -> participantDtos.add(tournamentMapper.entityToTournamentDetailParticipantDto(participantDto,
              horseTournament.getEntryNumber(), horseTournament.getRoundReached())));
    }
    return tournamentMapper.entityToDetailDto(createdTournament, participantDtos);
  }

  @Override
  public TournamentStandingsDto getStandingsByTournamentId(Long tournamentId) throws NotFoundException {
    LOG.trace("getStandingsByTournamentId({})", tournamentId);
    Tournament tournament = tournamentDao.getById(tournamentId);
    Collection<HorseTournament> horseTournaments = horseTournamentDao.getHorsesByIDTournament(tournamentId);
    Map<Long, HorseSelectionDto> horseMap = new HashMap<>();
    for (HorseTournament horseTournament : horseTournaments) {
      horseMap.put(horseTournament.getHorseId(), horseMapper.entityToSelectionDto(horseDao.getById(horseTournament.getHorseId())));
    }
    return tournamentMapper.entityToTournamentStandingsDto(tournament, horseTournaments, horseMap);
  }

  @Override
  public TournamentStandingsDto updateStandings(TournamentStandingsDto standings) throws ValidationException, NotFoundException, ConflictException {
    LOG.trace("updateStandings({})", standings);
    validator.validateForStandings(standings);
    List<HorseTournament> horseTournaments = new ArrayList<>();
    for (TournamentDetailParticipantDto horse : standings.participants()) {
      horseTournaments.add(tournamentMapper.tournamentDetailParticipantDtoToHorseTournament(horse, standings.id()));
    }
    fillTree(standings.tree(), horseTournaments, 1);
    for (HorseTournament horse : horseTournaments) {
      horseTournamentDao.updateStandings(horse.getTournamentId(), horse.getHorseId(), horse.getEntryNumber(), horse.getRoundReached());
    }
    return getStandingsByTournamentId(standings.id());
  }

  @Override
  public int fillTree(TournamentStandingsTreeDto standings, List<HorseTournament> horseTournaments, int entryNumber) {
    LOG.trace("fillTree({}, {}, {})", standings, horseTournaments, entryNumber);
    if (standings.thisParticipant() != null) {
      for (HorseTournament horseTournament : horseTournaments) {
        if (horseTournament.getHorseId().equals(standings.thisParticipant().horseId())) {
          horseTournament.setRoundReached(horseTournament.getRoundReached() + 1);
        }
      }
    }
    if (standings.branches() == null) {
      for (HorseTournament horseTournament : horseTournaments) {
        if (standings.thisParticipant() != null
            && horseTournament.getHorseId().equals(standings.thisParticipant().horseId())) {
          horseTournament.setEntryNumber(entryNumber);
        }
      }
      return entryNumber + 1;
    }
    entryNumber = fillTree(standings.branches().get(0), horseTournaments, entryNumber);
    entryNumber = fillTree(standings.branches().get(1), horseTournaments, entryNumber);

    return entryNumber;
  }

  @Override
  public List<TournamentDetailParticipantDto> calculatePointsForHorses(Long tournamentId) throws NotFoundException, ConflictException {
    LOG.trace("calculatePointsForHorses({})", tournamentId);
    Tournament tournament = tournamentDao.getById(tournamentId);
    LocalDate startDate = tournament.getStartDate();
    Collection<HorseTournament> horses = horseTournamentDao.getHorsesByIDTournament(tournamentId);



    List<Tournament> tournaments = tournamentDao.getLast12MonthsTournaments(startDate);
    if (tournaments.size() == 0) {
      List<Horse> horseList = horseDao.getByIds(horses.stream().map(HorseTournament::getHorseId).collect(Collectors.toSet()));
      horseList.sort(Comparator.comparing(Horse::getName));
      TournamentDetailParticipantDto[] results = new TournamentDetailParticipantDto[8];

      for (int i = 0; i < horses.size() / 2; i++) {
        results[2 * i] = horseMapper.entityToTournamentParticipantDto(horseList.get(i), 2 * i + 1);
      }
      int a = 1;
      for (int i = 7; i >= horses.size() / 2; i--) {
        results[a] = horseMapper.entityToTournamentParticipantDto(horseList.get(i), a + 1);
        a = a + 2;
      }

      for (TournamentDetailParticipantDto dto : results) {
        horseTournamentDao.updateStandings(tournamentId, dto.horseId(), dto.entryNumber(), 1);
      }

      return Arrays.asList(results);

    }
    Set<Long> tournamentIds = tournaments.stream().map(Tournament::getId).collect(Collectors.toSet());
    Map<Long, List<HorseTournament>> map = horseTournamentDao.getHorsesByIDsTournaments(tournamentIds); //tournamentId, horseTournaments


    Map<Long, Integer> points = new HashMap<>(); //horseId, points

    for (Tournament t : tournaments) {
      Long currentTournamentId = t.getId();
      List<HorseTournament> horseTournaments = map.get(currentTournamentId);

      for (HorseTournament horse : horses) { //you need to check the horse's points from the past tournaments
        int tournamentPoints = points.getOrDefault(horse.getHorseId(), 0);
        if (containsHorse(horse, horseTournaments)) {
          HorseTournament wanted = new HorseTournament(null, null, 0, 0);
          for (HorseTournament horseTournament : horseTournaments) {
            if (horseTournament.getHorseId().equals(horse.getHorseId())) {
              wanted = horseTournament;
              break;
            }
          }
          if (wanted.getRoundReached() == 4) {
            tournamentPoints += 5;
          }
          if (wanted.getRoundReached() == 3) {
            tournamentPoints += 3;
          }
          if (wanted.getRoundReached() == 2) {
            tournamentPoints += 1;
          }
        }
        points.put(horse.getHorseId(), tournamentPoints);
      }
    }

    List<Map.Entry<Long, Integer>> list = new ArrayList<>(points.entrySet());
    list.sort((entry1, entry2) -> {
      int compareByValue = entry1.getValue().compareTo(entry2.getValue());

      if (compareByValue == 0) {
        try {
          return horseDao.getById(entry1.getKey()).getName().compareTo(horseDao.getById(entry2.getKey()).getName());
        } catch (NotFoundException e) {
          LOG.warn(e.getMessage(), e);
          throw new RuntimeException(e);
        }
      } else {
        return compareByValue;
      }
    });


    List<Horse> participants = new ArrayList<>();
    participants.add(horseDao.getById(list.get(7).getKey())); //1
    participants.add(horseDao.getById(list.get(0).getKey())); //2
    participants.add(horseDao.getById(list.get(6).getKey())); //3
    participants.add(horseDao.getById(list.get(1).getKey()));
    participants.add(horseDao.getById(list.get(5).getKey()));
    participants.add(horseDao.getById(list.get(2).getKey()));
    participants.add(horseDao.getById(list.get(4).getKey()));
    participants.add(horseDao.getById(list.get(3).getKey()));

    List<TournamentDetailParticipantDto> participantDtos = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      participantDtos.add(new TournamentDetailParticipantDto(participants.get(i).getId(), participants.get(i).getName(), participants.get(i).getDateOfBirth(),
          i + 1, 1));
    }

    for (HorseTournament horse : horses) {
      TournamentDetailParticipantDto current = participantDtos.stream().filter(p -> p.horseId().equals(horse.getHorseId())).findFirst().get();
      horseTournamentDao.updateStandings(tournamentId, current.horseId(), current.entryNumber(), current.roundReached());
    }

    return participantDtos;
  }

  @Override
  public TournamentStandingsDto generateFirstRound(TournamentStandingsDto standings) throws NotFoundException, ConflictException {
    LOG.trace("generateFirstRound({})", standings);

    Long tournamentId = standings.id();
    calculatePointsForHorses(tournamentId);

    return getStandingsByTournamentId(standings.id());
  }

  private boolean containsHorse(HorseTournament horseTournament, List<HorseTournament> horseTournaments) {
    LOG.trace("containsHorse({}, {})", horseTournament, horseTournaments);
    for (HorseTournament ht : horseTournaments) {
      if (ht.getHorseId().equals(horseTournament.getHorseId())) {
        return true;
      }
    }
    return false;
  }
}
