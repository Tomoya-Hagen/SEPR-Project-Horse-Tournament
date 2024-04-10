import {Sex} from './sex';
import {Breed} from "./breed";

/**
 * An interface that represents a horse entity
 *
 * @param id the id of the horse
 * @param name the name of the horse
 * @param sex the sex of the horse
 * @param dateOfBirth the date of birth of the horse
 */
export interface Horse {
  id?: number;
  name: string;
  sex: Sex;
  dateOfBirth: Date | null;
  height: number | null;
  weight: number | null;
  breed?: Breed;
}

/**
 * An interface that represents a horse list dto
 *
 * @param id the id of the horse
 * @param name the name of the horse
 * @param sex the sex of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param breed the breed of the horse
 */
export interface HorseListDto {
  id: number,
  name: string,
  sex: Sex,
  dateOfBirth: Date;
  breed: Breed;
}

/**
 * An interface that represents a horse search dto, null prompts are ignored
 *
 * @param name the name of the horse, can be null
 * @param sex the sex of the horse, can be null
 * @param bornEarliest the earliest date of birth of the horse to search for, can be null
 * @param bornLastest the latest date of birth of the horse to search fo, can be null
 * @param breedName the name of the breed, can be null
 * @param limit the maximum number of horses to return can be null
 */
export interface HorseSearch {
  name?: string;
  sex?: Sex;
  bornEarliest?: Date;
  bornLastest?: Date;
  breedName?: string;
  limit?: number;
}

/**
 * An interface that represents a horse selection dto
 *
 * @param id the id of the horse
 * @param name the name of the horse
 * @param dateOfBirth the date of birth of the horse
 */
export interface HorseSelection {
    id: number;
    name: string;
    dateOfBirth: Date;
}
