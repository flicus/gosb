import {Player} from "./player";
import {BattleLayout} from "./battleLayout";

export interface PlayerLayout {
  player: Player;
  b2: BattleLayout;
  b3: BattleLayout;
  b5: BattleLayout;
  b7: BattleLayout;
}
