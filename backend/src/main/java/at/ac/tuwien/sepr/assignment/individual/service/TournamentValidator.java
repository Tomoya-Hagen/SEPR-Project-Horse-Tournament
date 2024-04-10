package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  private void validateHelper(TournamentCreateDto tournament, List<String> validationErrors) {
    if (tournament.name() == null) {
      validationErrors.add("No name given");
    }
    if (tournament.name() != null && tournament.name().length() > 100) {
      validationErrors.add("Name too long");
    }
    if (tournament.startDate() == null) {
      validationErrors.add("No date given");
    }
    if (tournament.endDate() == null) {
      validationErrors.add("No date given");
    }
    if ((tournament.startDate() != null && tournament.endDate() != null) && !tournament.startDate().isBefore(tournament.endDate())) {
      validationErrors.add("Start date must be before end date");
    }
    if (tournament.startDate() != null && tournament.startDate().isBefore(LocalDate.of(1970, 1, 1))
        || tournament.endDate() != null && tournament.endDate().isBefore(LocalDate.of(2030, 1, 1))) {
      validationErrors.add("Invalid date given");
    }
    if (tournament.participants() == null || tournament.participants().size() != 8) {
      validationErrors.add("No 8 horses given");
    }

  }

  public void validateForCreate(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    validateHelper(tournament, validationErrors);

    if (!validationErrors.isEmpty()) {
      String errorMessage = "Validation of tournament for create failed: ";
      LOG.warn(errorMessage);
      throw new ValidationException(errorMessage, validationErrors);
    }
  }

  public void validateForStandings(TournamentStandingsDto tournament) throws ValidationException {
    LOG.trace("validateForStandings({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.id() == null) {
      validationErrors.add("No id given");
    }
    if (tournament.name() == null) {
      validationErrors.add("No name given");
    }
    if (tournament.participants() == null || tournament.participants().size() != 8) {
      validationErrors.add("No 8 horses given");
    }
    if (tournament.participants() != null && hasDuplicateParticipants(tournament.tree())) {
      validationErrors.add("Duplicate participants given");
    }
    if (tournament.tree() == null) {
      validationErrors.add("No tree given");
    }
    if (!validationErrors.isEmpty()) {
      String errorMessage = "Validation of tournament for standings failed: " + validationErrors;
      LOG.warn(errorMessage);
      throw new ValidationException(errorMessage, validationErrors);
    }
  }

  private boolean hasDuplicateParticipants(TournamentStandingsTreeDto standingsTree) {
    Set<Long> horseIds = new HashSet<>();
    return hasDuplicateParticipantsRecursive(standingsTree, horseIds);
  }

  private boolean hasDuplicateParticipantsRecursive(TournamentStandingsTreeDto node, Set<Long> horseIds) {
    if (node == null) {
      return false;
    }
    // Check if the current participant's horseId already exists in the first round
    if (node.thisParticipant() != null && node.branches() == null && !horseIds.add(node.thisParticipant().horseId())) {
      return true;
    }

    if (node.branches() != null) {
      for (TournamentStandingsTreeDto branch : node.branches()) {
        if (hasDuplicateParticipantsRecursive(branch, horseIds)) {
          return true;
        }
      }
    }

    return false;
  }
}
