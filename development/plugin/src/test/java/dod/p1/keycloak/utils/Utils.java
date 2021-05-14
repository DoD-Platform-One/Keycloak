package dod.p1.keycloak.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.keycloak.authentication.FormContext;
import org.powermock.api.mockito.PowerMockito;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import dod.p1.keycloak.common.YAMLConfig;
import dod.p1.keycloak.registration.X509Tools;

public class Utils {

    public static void setupX509Mocks() {

        PowerMockito.mockStatic(X509Tools.class);
        PowerMockito.when(X509Tools.getX509Username(any(FormContext.class))).thenReturn("thing");

    }

    public static void setupFileMocks() throws Exception {

        final String fileContent = "x509:\n" +
                "  userIdentityAttribute: \"usercertificate\"\n" +
                "  userActive509Attribute: \"activecac\"\n" +
                "  autoJoinGroup:\n" +
                "    - \"/test-group\"\n" +
                "  requiredCertificatePolicies:\n" +
                "    - \"2.16.840.1.101.2.1.11.36\"\n" +
                "groupProtectionIgnoreClients:\n" +
                "  - \"test-client\"\n" +
                "noEmailMatchAutoJoinGroup:\n" +
                "  - \"/randos-test-group\"\n" +
                "emailMatchAutoJoinGroup:\n" +
                "  - description: Test thing 1\n" +
                "    groups:\n" +
                "      - \"/test-group-1-a\"\n" +
                "      - \"/test-group-1-b\"\n" +
                "    domains:\n" +
                "      - \".gov\"\n" +
                "      - \".mil\"\n" +
                "      - \"@afit.edu\"\n" +
                "  - description: Test thing 2\n" +
                "    groups:\n" +
                "      - \"/test-group-2-a\"\n" +
                "    domains:\n" +
                "      - \"@unicorns.com\"\n" +
                "      - \"@merica.test\"";

        final File fileMock = PowerMockito.mock(File.class);
        final FileInputStream fileInputStreamMock = PowerMockito.mock(FileInputStream.class);

        InputStream stream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileMock);
        PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStreamMock);

        Yaml yaml = new Yaml(new Constructor(YAMLConfig.class));
        YAMLConfig yamlConfig = yaml.load(stream);

        final Yaml yamlMock = PowerMockito.mock(Yaml.class);
        PowerMockito.whenNew(Yaml.class).withAnyArguments().thenReturn(yamlMock);

        when(yamlMock.load(any(InputStream.class))).thenReturn(yamlConfig);
    }

}
