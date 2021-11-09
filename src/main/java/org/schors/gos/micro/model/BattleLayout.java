package org.schors.gos.micro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleLayout {
    private String attack1;
    private String attack2;
    private String defence1;
    private String defence2;
}
