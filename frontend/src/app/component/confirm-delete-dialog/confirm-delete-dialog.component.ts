import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
/**
 * A dialog that asks the user if he really wants to delete a horse.
 */
export class ConfirmDeleteDialogComponent implements OnInit {

  @Input() deleteWhat = '?';
  @Output() confirm = new EventEmitter<void>();

  @HostBinding('class') cssClass = 'modal fade';

  ngOnInit(): void {
  }

}
