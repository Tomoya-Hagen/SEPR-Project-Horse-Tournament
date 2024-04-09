package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO record of breed for search.
 */
public record BreedSearchDto(
    String name,
    Integer limit
) {
}
