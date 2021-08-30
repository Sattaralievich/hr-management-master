package uz.pdp.hrManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WatcherDto {
    @NotNull
    private UUID watcherId;
    private Integer watcherStatusId;
}
