package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentHorsesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final TournamentHorsesDao tournamentHorsesDao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentHorsesDao tournamentHorsesDao,
                               TournamentMapper mapper, TournamentValidator validator) {
    this.tournamentDao = tournamentDao;
    this.tournamentHorsesDao = tournamentHorsesDao;
    this.mapper = mapper;
    this.validator = validator;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchParamsDto searchParameters) {
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream()
        .map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException, NotFoundException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    var createdTournament = tournamentDao.create(tournament);
    var horseTournaments = tournamentHorsesDao.getHorseByIDTournaments(createdTournament.getId());
    List<TournamentDetailParticipantDto> participantDtos = new ArrayList<>();
    for (HorseSelectionDto participantDto : tournament.participants()) {
      HorseTournament horseTournament = horseTournaments.stream().filter(ht -> ht.horseId().equals(participantDto.id())).findFirst().orElse(null);
      if (horseTournament == null) {
        throw new NotFoundException("Horse with id " + participantDto.id() + " does not exist");
      }
      participantDtos.add(mapper.entityToTournamentDetailParticipantDto(participantDto, horseTournament.entryNumber(), horseTournament.roundReached()));
    }
    return mapper.entityToDetailDto(createdTournament, participantDtos);
  }


}
