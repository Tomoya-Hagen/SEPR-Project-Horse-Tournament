import {Component, OnInit} from '@angular/core';
import {TournamentStandingsDto} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

/**
 * Component for displaying the standings of a tournament
 */
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

  /**
   * Loads the tournament standings
   */
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

  /**
   * Creates new tournament standings on submitting
   *
   * @param form the NgForm to validate
   */
  public submit(form: NgForm) {
    if (form.invalid) {
      return;
    } else {
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
  }

  /**
   * Generates the matches of the first round of this tournament, if no participants are set yet.
   *
   */
  public generateFirstRound() {
    if (!this.standings) {
      return;
    }
    for (const participant of this.standings.participants) {
      if (participant.roundReached !== 0) {
        console.error("Cannot generate first round: there are already entries of rounds");
        this.notification.error("Cannot generate first round: there are already entries of rounds", "Cannot Generate First Round");
        return;
      }
    }
    this.service.generateFirstRound(this.standings)
      .subscribe({
        next: data => {
          this.standings = data;
          this.notification.success(`First round generated`, "First round generated successfully");
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
        },
        error: error => {
          console.error(error.message, error);
          this.notification.error(this.errorFormatter.format(error), "Could Not Generate First Round", {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
  }
}
