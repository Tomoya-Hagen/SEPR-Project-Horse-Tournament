import { Component } from '@angular/core';
import { TournamentListDto, TournamentSearchParams } from 'src/app/dto/tournament';
import { TournamentService } from 'src/app/service/tournament.service';
import { ToastrService } from 'ngx-toastr';
import { Subject } from 'rxjs';
import {debounceTime} from 'rxjs/operators';

/**
 * Component for handling operations on tournaments.
 */
@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrls: ['./tournament.component.scss']
})
export class TournamentComponent {
  searchParams: TournamentSearchParams = {};
  tournaments: TournamentListDto[] = [];
  searchStartingEarliest: string | null = null;
  searchStartingLatest: string | null = null;
  searchChangedObservable = new Subject<void>();


  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.loadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe(() => {
        this.reloadTournaments();
      });
  }

  /**
   * Reloads the tournaments
   */
  reloadTournaments() {
    if (this.searchStartingEarliest == null || this.searchStartingEarliest === "") {
      delete this.searchParams.startDate;
    } else {
      this.searchParams.startDate = new Date(this.searchStartingEarliest);
    }
    if (this.searchStartingLatest == null || this.searchStartingLatest === "") {
      delete this.searchParams.endDate;
    } else {
      this.searchParams.endDate = new Date(this.searchStartingLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: (data: TournamentListDto[]) => {
          this.tournaments = data;
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.notification.error(error.message.message, 'Could Not Fetch Tournaments');
        }
      });
  }

  /**
   * notifies the subscribers that the search criteria has changed
   */
  searchChanged() {
    this.searchChangedObservable.next();
  }

  /**
   * Loads the tournaments
   */
  loadTournaments(): void {
    this.service.getAll()
      .subscribe((tournaments: TournamentListDto[]) => {
        this.tournaments = tournaments;
      }, error => {
        console.error('Error fetching tournaments', error);
        this.notification.error(error.message.message, 'Could Not Fetch Tournaments');
      });
  }

}
