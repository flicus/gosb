import {Component, OnInit} from '@angular/core';
import {Week} from "../model/week";
import {BattleService} from "../battle.service";
import {PlayerService} from "../player.service";
import {Player} from "../model/player";
import {PlayerLayout} from "../model/playerLayout";
import {BattleLayout} from "../model/battleLayout";
import {WeekTotal} from "../model/weekTotal";
import {from, Observable} from "rxjs";
import {concatMap, filter, reduce} from "rxjs/operators";

function round(value: number, decimals: number) {
  // return Number(Math.round(value+'e'+decimals)+'e-'+decimals);
  return value.toFixed(decimals);
}

@Component({
  selector: 'app-week',
  templateUrl: './week.component.html',
  styleUrls: ['./week.component.scss']
})
export class WeekComponent implements OnInit {

  week: Week = {date: "", playerLayouts: []};
  players: Player[] = [];
  weekTotal: WeekTotal = {
    attackB2: "",
    attackB3: "",
    attackB5: "",
    attackB7: "",
    defenceB2: "",
    defenceB3: "",
    defenceB5: "",
    defenceB7: ""
  }

  constructor(private battleService: BattleService, private playerService: PlayerService) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(): void {
    this.playerService.getPlayers()
      .subscribe(value => {
        this.players = value;
        this.battleService.getWeek()
          .subscribe(value => this.week = value);
      });
  }

  filter(): Player[] {
    return this.players.filter(value => !this.week.playerLayouts
      .map(pl => pl.player)
      .find(p => p.id == value.id)
    );
  }

  addToBattle(player: Player) {
    let playerLayout: PlayerLayout = {
      player: player,
      battleLayouts: [
        {battleType: "BATTLE2", attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
        {battleType: "BATTLE3", attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
        {battleType: "BATTLE5", attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
        {battleType: "BATTLE7", attack1: "0", attack2: "0", defence1: "0", defence2: "0"}
      ]
    }
    this.week.playerLayouts.push(playerLayout);
    this.calculateWeekTotal();
  }

  getBattle(battleType: string, battles: BattleLayout[]): BattleLayout {
    return battles.filter(value => value.battleType == battleType)[0];
  }

  calculateWeekTotal(): void {
    this
      .getReducedBattle("BATTLE2")
      .subscribe(value => {
        this.weekTotal.attackB2 = value.attack1;
        this.weekTotal.defenceB2 = value.defence1;
      });
    this
      .getReducedBattle("BATTLE3")
      .subscribe(value => {
        this.weekTotal.attackB3 = value.attack1;
        this.weekTotal.defenceB3 = value.defence1;
      });
    this
      .getReducedBattle("BATTLE5")
      .subscribe(value => {
        this.weekTotal.attackB5 = value.attack1;
        this.weekTotal.defenceB5 = value.defence1;
      });
    this
      .getReducedBattle("BATTLE7")
      .subscribe(value => {
        this.weekTotal.attackB7 = value.attack1;
        this.weekTotal.defenceB7 = value.defence1;
      });
  }

  getReducedBattle(battleType: string): Observable<BattleLayout> {
    let seed: BattleLayout = {
      attack1: "0", attack2: "0", battleType: "", defence1: "0", defence2: "0"
    }

    return from(this.week.playerLayouts)
      .pipe(concatMap(value => from(value.battleLayouts)))
      .pipe(filter(value => value.battleType == battleType))
      .pipe(reduce((acc, one) => {
        acc.attack1 = this.accumulateAttacks(acc.attack1, one.attack1, one.attack2);
        acc.defence1 = this.accumulateDefence(acc.defence1, one.defence1, one.defence2);
        return acc;
      }, seed))
  }

  accumulateAttacks(accumulator: string, attack1: string, attack2: string): string {
    return this.d2s(this.s2d(accumulator) + (this.s2d(attack1) + this.s2d(attack2)) * 17);
  }

  accumulateDefence(accumulator: string, defence1: string, defence2: string): string {
    return this.d2s(this.s2d(accumulator) + this.s2d(defence1) + this.s2d(defence2));
  }

  s2d(s: string): number {
    if (s.endsWith("K") || s.endsWith("k")) {
      s = s.slice(0, -1);
      return Number(s) * 1_000
    }
    if (s.endsWith("M") || s.endsWith("m")) {
      s = s.slice(0, -1);
      return Number(s) * 1_000_000
    }
    if (s.endsWith("B") || s.endsWith("b")) {
      s = s.slice(0, -1);
      return Number(s) * 1_000_000_000;
    }
    if (s.endsWith("T") || s.endsWith("t")) {
      s = s.slice(0, -1);
      return Number(s) * 1_000_000_000_000;
    }
    return Number(s);
  }

  d2s(d: number) {
    if (d > 999_999_999_999) {
      return "" + round(d / 1_000_000_000_000, 2) + "T"
    }
    if (d > 999_999_999) {
      return "" + round(d / 1_000_000_000, 2) + "B"
    }
    if (d > 999_999) {
      return "" + round(d / 1_000_000, 2) + "M"
    }
    if (d > 999) {
      return "" + round(d / 1_000, 2) + "K"
    }
    return "" + round(d, 2);
  }

  onDragStart(event) {

  }

  onDragEnd() {

  }

  onDrop() {

  }
}
