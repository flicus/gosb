package org.schors.gos.micro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Week {
    private String date;
    private List<PlayerLayout> playerLayouts;
}
