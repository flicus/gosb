import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {PlayersComponent} from './players/players.component';
import {FormsModule} from '@angular/forms';
import {PlayerDetailComponent} from './player-detail/player-detail.component';
import {MessagesComponent} from './messages/messages.component';
import {WeekComponent} from './week/week.component';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ButtonModule} from "primeng/button";
import {TabMenuModule} from 'primeng/tabmenu';
import {DataViewModule} from 'primeng/dataview';
import {AvatarModule} from 'primeng/avatar';
import {InputTextModule} from 'primeng/inputtext';
import {InplaceModule} from 'primeng/inplace';
import {CardModule} from "primeng/card";
import {TableModule} from "primeng/table";
import {ChipModule} from "primeng/chip";
import {TodayComponent} from './today/today.component';
import {TagModule} from "primeng/tag";
import {DragDropModule} from "primeng/dragdrop";
import {ScrollTopModule} from "primeng/scrolltop";
import { PersonsComponent } from './persons/persons.component';


@NgModule({
  declarations: [
    AppComponent,
    PlayersComponent,
    PlayerDetailComponent,
    MessagesComponent,
    WeekComponent,
    TodayComponent,
    PersonsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ButtonModule,
    TabMenuModule,
    DataViewModule,
    AvatarModule,
    InputTextModule,
    InplaceModule,
    CardModule,
    TableModule,
    ChipModule,
    TagModule,
    DragDropModule,
    ScrollTopModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
