package org.schors.gos.micro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekPlan {
  private String date;
  private Map<BattleType, Map<Integer, String>> damage;
}
