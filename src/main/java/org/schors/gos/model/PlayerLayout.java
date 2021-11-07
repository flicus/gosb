package org.schors.gos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLayout {
  private Player player;
  private BattleLayout b2;
  private BattleLayout b3;
  private BattleLayout b5;
  private BattleLayout b7;

  public String readable() {
    StringBuilder sb = new StringBuilder();
    sb.append("Вторник:\n")
      .append("Защита: ").append(b2.getDefence1().trim()).append(", ").append(b2.getDefence2().trim()).append("\n")
      .append("Атака: ").append(b2.getAttack1().trim()).append(", ").append(b2.getAttack2().trim()).append("\n")
      .append("Среда: \n")
      .append("Защита: ").append(b3.getDefence1().trim()).append(", ").append(b3.getDefence2().trim()).append("\n")
      .append("Атака: ").append(b3.getAttack1().trim()).append(", ").append(b3.getAttack2().trim()).append("\n")
      .append("Пятница: \n")
      .append("Защита: ").append(b5.getDefence1().trim()).append(", ").append(b5.getDefence2().trim()).append("\n")
      .append("Атака: ").append(b5.getAttack1().trim()).append(", ").append(b5.getAttack2().trim()).append("\n")
      .append("Воскресенье: \n")
      .append("Защита: ").append(b7.getDefence1().trim()).append(", ").append(b7.getDefence2().trim()).append("\n")
      .append("Атака: ").append(b7.getAttack1().trim()).append(", ").append(b7.getAttack2().trim()).append("\n");

    return sb.toString();
  }
}
