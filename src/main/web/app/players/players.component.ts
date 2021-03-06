import {Component, OnInit} from '@angular/core';
import {Player} from "../model/player";
import {PlayerService} from "../player.service";
import {MessageService} from '../message.service';

@Component({
  selector: 'app-players',
  templateUrl: './players.component.html',
  styleUrls: ['./players.component.scss']
})
export class PlayersComponent implements OnInit {

  players: Player[] = []

  constructor(private playerService: PlayerService, private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.getPlayers();
  }

  getPlayers(): void {
    this.playerService.getPlayers()
      .subscribe(players => this.players = players);
  }

  addPlayer(element: HTMLInputElement): void {
    let name: string = element.value.trim();
    if (!name) {
      return;
    }
    this.playerService.addPlayer({name} as Player)
      .subscribe(player => {
        console.error(player);
        this.players.push(player);
        element.value = '';
      });
  }

  checkAndAdd(event: KeyboardEvent, element: HTMLInputElement): void {
    if (event.key == "Enter") {
      this.addPlayer(element);
    }
  }

  deletePlayer(id: string): void {
    id = id.trim();
    if (!id) {
      return;
    }
    this.playerService.deletePlayer(id)
      .subscribe(value => {
        if (value) {
          this.players = this.players.filter(p => {
            return p.id != id
          });
        }
      })
  }
}
