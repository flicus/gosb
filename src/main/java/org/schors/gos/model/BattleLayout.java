package org.schors.gos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleLayout {
    private BattleType battleType;
    private String attack1;
    private String attack2;
    private String defence1;
    private String defence2;
}
