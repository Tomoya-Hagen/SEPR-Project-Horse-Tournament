import {Component, OnInit} from '@angular/core';
import {TournamentStandingsDto} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;

  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  public ngOnInit() {
    this.service.getStandings(this.route.snapshot.params['id'])
      .subscribe({
        next: data => {
          this.standings = data;
        },
        error: error => {
          console.error(error.message, error);
          this.notification.error(this.errorFormatter.format(error), "Could Not Get Tournament Standings", {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
  }

  public submit(form: NgForm) {
    if (form.invalid)
      return;
    else
      this.service.saveStandings(this.standings!)
        .subscribe({
          next: data => {
            this.notification.success(`Tournament standings saved`, "Tournament standings saved successfully");
            this.location.back();
          },
          error: error => {
            console.error(error.message, error);
            this.notification.error(this.errorFormatter.format(error), "Could Not Save Tournament Standings", {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        });
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    // TODO implement
  }
}
