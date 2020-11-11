package dod.p1.keycloak.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.models.GroupModel;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class YAMLConfigEmailAutoJoin {

    private String description;
    private List<String> groups;
    private List<String> domains;
    private List<GroupModel> groupModels;
}
