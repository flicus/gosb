import {Component, Input, OnInit} from '@angular/core';
import {Player} from '../model/player';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {PlayerService} from "../player.service";

@Component({
  selector: 'app-player-detail',
  templateUrl: './player-detail.component.html',
  styleUrls: ['./player-detail.component.css']
})
export class PlayerDetailComponent implements OnInit {

  @Input() player?: Player;

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService,
    private location: Location
  ) {
  }

  ngOnInit(): void {
    this.getPlayer();
  }

  getPlayer(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id != null) {
      this.playerService.getPlayer(id)
        .subscribe(player => this.player = player);
    }
  }

  goBack(): void {
    this.location.back();
  }

}
