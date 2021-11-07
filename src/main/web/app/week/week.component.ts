import {Component, OnInit} from '@angular/core';
import {Week} from "../model/week";
import {BattleService} from "../battle.service";
import {PlayerService} from "../player.service";
import {Player} from "../model/player";
import {PlayerLayout} from "../model/playerLayout";
import {BattleLayout} from "../model/battleLayout";
import {WeekTotal} from "../model/weekTotal";
import {from, Observable} from "rxjs";
import {reduce} from "rxjs/operators";

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

  // @ts-ignore
  draggedElement: BattleLayout = null;
  // @ts-ignore
  draggedField: string = null;

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
          .subscribe(value => {
            this.week = value
            this.calculateWeekTotal();
          });
      });
  }

  isWeak(power: string): boolean {
    return this.s2d(power) < 1_000_000_000;
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
      b2: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b3: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b5: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b7: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"}
    }

    this.battleService.addPlayerLayout(playerLayout)
      .subscribe(value => {
        this.week.playerLayouts.push(playerLayout);
        this.calculateWeekTotal();
      });

  }

  updateWeek(): void {
    this.battleService.updateWeek(this.week)
      .subscribe(value => this.calculateWeekTotal());
  }

  calculateWeekTotal(): void {
    this.getReducedBattle()
      .subscribe(value => {
        this.weekTotal.attackB2 = value.b2.attack1;
        this.weekTotal.defenceB2 = value.b2.defence1;
        this.weekTotal.attackB3 = value.b3.attack1;
        this.weekTotal.defenceB3 = value.b3.defence1;
        this.weekTotal.attackB5 = value.b5.attack1;
        this.weekTotal.defenceB5 = value.b5.defence1;
        this.weekTotal.attackB7 = value.b7.attack1;
        this.weekTotal.defenceB7 = value.b7.defence1;
      });
  }

  getReducedBattle(): Observable<PlayerLayout> {
    let seed: PlayerLayout = {
      player: {id: "", name: ""},
      b2: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b3: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b5: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"},
      b7: {attack1: "0", attack2: "0", defence1: "0", defence2: "0"}
    }

    return from(this.week.playerLayouts)
      .pipe(reduce((acc, one) => {
        acc.b2.attack1 = this.accumulateAttacks(acc.b2.attack1, one.b2.attack1, one.b2.attack2);
        acc.b2.defence1 = this.accumulateDefence(acc.b2.defence1, one.b2.defence1, one.b2.defence2);
        acc.b3.attack1 = this.accumulateAttacks(acc.b3.attack1, one.b3.attack1, one.b3.attack2);
        acc.b3.defence1 = this.accumulateDefence(acc.b3.defence1, one.b3.defence1, one.b3.defence2);
        acc.b5.attack1 = this.accumulateAttacks(acc.b5.attack1, one.b5.attack1, one.b5.attack2);
        acc.b5.defence1 = this.accumulateDefence(acc.b5.defence1, one.b5.defence1, one.b5.defence2);
        acc.b7.attack1 = this.accumulateAttacks(acc.b7.attack1, one.b7.attack1, one.b7.attack2);
        acc.b7.defence1 = this.accumulateDefence(acc.b7.defence1, one.b7.defence1, one.b7.defence2);
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

  onDragStart(battleLayout: BattleLayout, field: string) {
    this.draggedElement = battleLayout;
    this.draggedField = field;

  }

  onDragEnd() {
    this.draggedElement = null as any;
    this.draggedField = null as any;
  }

  onDrop(battleLayout: BattleLayout, field: string) {
    if (this.draggedElement) {
      // @ts-ignore
      let value: string = battleLayout[field];
      // @ts-ignore
      battleLayout[field] = this.draggedElement[this.draggedField];
      // @ts-ignore
      this.draggedElement[this.draggedField] = value;
      this.calculateWeekTotal();
    }

  }
}
