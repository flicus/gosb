import {Player} from "./player";
import {BattleLayout} from "./battleLayout";

export interface PlayerLayout {
  player: Player;
  battleLayouts: BattleLayout[];
}
