import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PlayersComponent} from "./players/players.component";
import {WeekComponent} from "./week/week.component";
import {PlayerDetailComponent} from "./player-detail/player-detail.component";
import {TodayComponent} from "./today/today.component";
import { PersonsComponent } from './persons/persons.component';
import { EventsComponent } from './events/events.component';

const routes: Routes = [
  {path: '', redirectTo: '/today', pathMatch: 'full'},
  {path: 'detail/:id', component: PlayerDetailComponent},
  {path: 'today', component: TodayComponent},
  {path: 'week', component: WeekComponent},
  {path: 'players', component: PlayersComponent},
  {path: 'persons', component: PersonsComponent},
  {path: 'events', component: EventsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
