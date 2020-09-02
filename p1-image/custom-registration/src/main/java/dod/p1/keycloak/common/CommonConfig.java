package dod.p1.keycloak.common;

import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.exit;
import static jdk.nashorn.internal.runtime.Context.err;
import static org.keycloak.models.utils.KeycloakModelUtils.findGroupByPath;

public final class CommonConfig {

    private static CommonConfig INSTANCE;

    private final YAMLConfig config;
    private final List<GroupModel> autoJoinGroupX509;
    private final List<GroupModel> noEmailMatchAutoJoinGroup;

    private CommonConfig(RealmModel realm) {

        config = loadConfigFile();

        autoJoinGroupX509 = convertPathsToGroupModels(realm, config.getX509().getAutoJoinGroup());
        noEmailMatchAutoJoinGroup = convertPathsToGroupModels(realm, config.getNoEmailMatchAutoJoinGroup());

        config.getEmailMatchAutoJoinGroup().forEach(match -> {
            match.setGroupModels(convertPathsToGroupModels(realm, match.getGroups()));
        });
    }

    public static CommonConfig getInstance(RealmModel realm) {
        if (INSTANCE == null) {
            INSTANCE = new CommonConfig(realm);
        }

        return INSTANCE;
    }

    private YAMLConfig loadConfigFile() {
        String configFilePath = System.getenv("CUSTOM_REGISTRATION_CONFIG");
        File file = new File(configFilePath);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            err("Invalid or missing YAML Config, aborting.");
            exit(1);
            return null;
        }

        Yaml yaml = new Yaml(new Constructor(YAMLConfig.class));
        return yaml.load(fileInputStream);
    }

    private List<GroupModel> convertPathsToGroupModels(RealmModel realm, List<String> paths) {
        return paths
                .stream()
                .map(group -> findGroupByPath(realm, group))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Stream<YAMLConfigEmailAutoJoin> getEmailMatchAutoJoinGroup() {
        return config.getEmailMatchAutoJoinGroup().stream();
    }

    public String getUserIdentityAttribute() {
        return config.getX509().getUserIdentityAttribute();
    }

    public String getUserActive509Attribute() {
        return config.getX509().getUserActive509Attribute();
    }

    public Stream<GroupModel> getAutoJoinGroupX509() {
        return autoJoinGroupX509.stream();
    }

    public Stream<String> getRequiredCertificatePolicies() {
        return config.getX509().getRequiredCertificatePolicies().stream();
    }

    public Stream<GroupModel> getNoEmailMatchAutoJoinGroup() {
        return noEmailMatchAutoJoinGroup.stream();
    }

    public List<String> getIgnoredGroupProtectionClients() {
        return config.getGroupProtectionIgnoreClients();
    }

}