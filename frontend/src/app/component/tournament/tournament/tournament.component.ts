import { Component } from '@angular/core';
import { TournamentListDto, TournamentSearchParams } from 'src/app/dto/tournament';
import { TournamentService } from 'src/app/service/tournament.service';
import { ToastrService } from 'ngx-toastr';
import { Subject } from 'rxjs';
import {debounceTime} from 'rxjs/operators';

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

  reloadTournaments() {
    this.service.search(this.searchParams)
      .subscribe({
        next: (data: TournamentListDto[]) => {
          this.tournaments = data;
          this.sortTournaments();
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.notification.error(error.message.message, 'Could Not Fetch Tournaments');
        }
      });
  }

  searchChanged() {
    this.searchChangedObservable.next();
  }

  sortTournaments(): void {
    this.tournaments.sort((a, b) => {
      return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
    });
  }

  loadTournaments(): void {
    this.service.getAll()
      .subscribe((tournaments: TournamentListDto[]) => {
        this.tournaments = tournaments;
        this.sortTournaments();
      }, error => {
        console.error('Error fetching tournaments', error);
        this.notification.error(error.message.message, 'Could Not Fetch Tournaments');
      });
  }

}
