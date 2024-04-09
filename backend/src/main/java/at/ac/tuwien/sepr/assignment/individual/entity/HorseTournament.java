package at.ac.tuwien.sepr.assignment.individual.entity;


/**
 * Represents a horse participating in a specific tournament in the persistent data store.
 */
public class HorseTournament {
  private final Long horseId;
  private final Long tournamentId;
  private int entryNumber;
  private int roundReached;

  public HorseTournament(Long horseId, Long tournamentId, int entryNumber, int roundReached) {
    this.horseId = horseId;
    this.tournamentId = tournamentId;
    this.entryNumber = entryNumber;
    this.roundReached = roundReached;
  }

  public Long getHorseId() {
    return horseId;
  }

  public int getEntryNumber() {
    return entryNumber;
  }

  public int getRoundReached() {
    return roundReached;
  }

  public void setEntryNumber(int entryNumber) {
    this.entryNumber = entryNumber;
  }

  public void setRoundReached(int roundReached) {
    this.roundReached = roundReached;
  }
  public Long getTournamentId() {
    return tournamentId;
  }

}
