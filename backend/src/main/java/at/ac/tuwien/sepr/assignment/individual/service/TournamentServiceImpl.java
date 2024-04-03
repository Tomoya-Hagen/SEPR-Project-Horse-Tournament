package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream()
        .map(tournamentMapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException, NotFoundException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    var createdTournament = tournamentDao.create(tournament);
    var horseTournaments = horseTournamentDao.getHorsesByIDTournament(createdTournament.getId());
    List<TournamentDetailParticipantDto> participantDtos = new ArrayList<>();
    for (HorseSelectionDto participantDto : tournament.participants()) {
      HorseTournament horseTournament = horseTournaments.stream().filter(ht -> ht.getHorseId().equals(participantDto.id())).findFirst().orElse(null);
      if (horseTournament == null) {
        throw new NotFoundException("Horse with id " + participantDto.id() + " does not exist");
      }
      participantDtos.add(tournamentMapper.entityToTournamentDetailParticipantDto(participantDto,
          horseTournament.getEntryNumber(), horseTournament.getRoundReached()));
    }
    return tournamentMapper.entityToDetailDto(createdTournament, participantDtos);
  }

  @Override
  public TournamentStandingsDto getStandingsByTournamentId(Long tournamentId) throws NotFoundException {
    LOG.trace("getStandingsByTournamentId({})", tournamentId);
    Tournament tournament = tournamentDao.getById(tournamentId);
    if (tournamentId == null || tournament == null) {
      throw new NotFoundException("Tournament with id " + tournamentId + " does not exist");
    }
    Collection<HorseTournament> horseTournaments = horseTournamentDao.getHorsesByIDTournament(tournamentId);
    Map<Long, HorseSelectionDto> horseMap = new HashMap<>();
    for (HorseTournament horseTournament : horseTournaments) {
      horseMap.put(horseTournament.getHorseId(), horseMapper.entityToSelectionDto(horseDao.getById(horseTournament.getHorseId())));
    }
    return tournamentMapper.entityToTournamentStandingsDto(tournament, horseTournaments, horseMap);
  }

  @Override
  public TournamentStandingsDto updateStandings(TournamentStandingsDto standings) throws NotFoundException, ValidationException {
    LOG.trace("updateStandings({})", standings);
    validator.validateForStandings(standings);
    List<HorseTournament> horseTournaments = new ArrayList<>();
    for (TournamentDetailParticipantDto horse : standings.participants()) {
      horseTournaments.add(tournamentMapper.tournamentDetailParticipantDtoToHorseTournament(horse, standings.id()));
    }
    fillTree(standings.tree(), horseTournaments, 0);
    for (HorseTournament horse : horseTournaments) {
      tournamentDao.updateStandings(standings.id(), horse.getHorseId(), horse.getEntryNumber(), horse.getRoundReached());
    }
    return getStandingsByTournamentId(standings.id());
  }

  @Override
  public int fillTree(TournamentStandingsTreeDto standings, List<HorseTournament> horseTournaments, int entryNumber) {
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


}
