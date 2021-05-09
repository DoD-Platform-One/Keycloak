package dod.p1.keycloak.common;

import java.util.List;

import org.keycloak.models.GroupModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class YAMLConfigEmailAutoJoin {

    private String description;
    private List<String> groups;
    private List<String> domains;
    private List<GroupModel> groupModels;
}
