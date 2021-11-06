import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PlayersComponent} from "./players/players.component";
import {WeekComponent} from "./week/week.component";
import {PlayerDetailComponent} from "./player-detail/player-detail.component";
import {TodayComponent} from "./today/today.component";

const routes: Routes = [
  {path: '', redirectTo: '/today', pathMatch: 'full'},
  {path: 'detail/:id', component: PlayerDetailComponent},
  {path: 'today', component: TodayComponent},
  {path: 'week', component: WeekComponent},
  {path: 'players', component: PlayersComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
