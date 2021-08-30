package uz.pdp.hrManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    @NotNull
    private String name;
    @NotNull
    private String comment;
    private Date expireDate;
    private Set<UUID> users;
}
