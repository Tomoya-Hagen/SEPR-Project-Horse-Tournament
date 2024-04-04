package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private void validateHelper(TournamentCreateDto tournament, List<String> validationErrors) {
    if (tournament.name() == null) {
      validationErrors.add("No name given");
    }
    if (tournament.startDate() == null) {
      validationErrors.add("No date given");
    }
    if (tournament.endDate() == null) {
      validationErrors.add("No date given");
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
    if (tournament.tree() == null) {
      validationErrors.add("No tree given");
    }
    if (!validationErrors.isEmpty()) {
      String errorMessage = "Validation of tournament for standings failed: " + validationErrors;
      throw new ValidationException(errorMessage, validationErrors);
    }
  }
}
