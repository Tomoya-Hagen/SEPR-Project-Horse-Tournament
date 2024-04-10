import {HorseSelection} from "./horse";

/**
 * An interface that represents the tournament search parameters
 *
 * @param name the name of the tournament, can be undefined
 * @param startDate the start date of the tournament, can be undefined
 * @param endDate the end date of the tournament, can be undefined
 */
export interface TournamentSearchParams {
  name?: string;
  startDate?: Date;
  endDate?: Date;
}

/**
 * An interface that represents a tournament list dto
 *
 * @param id the id of the tournament
 * @param name the name of the tournament
 * @param startDate the start date of the tournament
 * @param endDate the end date of the tournament
 */
export interface TournamentListDto {
  id: number;
  name: string;
  startDate: Date;
  endDate: Date;
}

/**
 * An interface that represents a tournament create dto
 *
 * @param name the name of the tournament
 * @param startDate the start date of the tournament
 * @param endDate the end date of the tournament
 * @param participants the participants of the tournament (HorseSelection-objects)
 */
export interface TournamentCreateDto {
  name: string;
  startDate: Date;
  endDate: Date;
  participants: HorseSelection[];
}

/**
 * An interface that represents a tournament detail dto
 *
 * @param id the id of the tournament
 * @param name the name of the tournament
 * @param startDate the start date of the tournament
 * @param endDate the end date of the tournament
 * @param participants the participants of the tournament (TournamentDetailParticipantDto-objects)
 */
export interface TournamentDetailDto {
  id: number;
  name: string;
  startDate: Date;
  endDate: Date;
  participants: TournamentDetailParticipantDto[];
}

/**
 * An interface that represents a tournament detail participant dto
 *
 * @param horseId the id of the horse
 * @param name the name of the horse
 * @param dateOfBirth the date of birth of the horse
 * @param entryNumber the entry number of the horse, can be undefined
 * @param roundReached the round reached of the horse, can be undefined
 */
export interface TournamentDetailParticipantDto {
  horseId: number;
  name: string;
  dateOfBirth: Date;
  entryNumber?: number;
  roundReached?: number;
}

/**
 * An interface that represents a binary tree of the tournament standings tree dto
 *
 * @param thisParticipant the participant of the tree
 * @param branches the two branches of the current thisParticipant, can be undefined
 */
export interface TournamentStandingsTreeDto {
  thisParticipant: TournamentDetailParticipantDto | null;
  branches?: TournamentStandingsTreeDto[];
}

/**
 * An interface that represents a tournament standings dto
 *
 * @param id the id of the tournament
 * @param name the name of the tournament
 * @param participants the participants of the tournament (TournamentDetailParticipantDto-objects)
 * @param tree the tree of the tournament standings
 */
export interface TournamentStandingsDto {
  id: number;
  name: string;
  participants: TournamentDetailParticipantDto[];
  tree: TournamentStandingsTreeDto;
}
