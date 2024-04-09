package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO record of breed for search.
 *
 * @param name name of the breed
 * @param limit the limit of the results
 */
public record BreedSearchDto(
    String name,
    Integer limit
) {
}
