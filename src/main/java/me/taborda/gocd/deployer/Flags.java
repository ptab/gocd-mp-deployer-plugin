package me.taborda.gocd.deployer;

import java.util.stream.Stream;

import com.thoughtworks.go.plugin.api.task.TaskConfig;

public enum Flags {

    REDEPLOY("Redeploy", "--redeploy", true);

    private final String property;
    private final String flag;
    private final boolean enabledByDefault;

    Flags(String property, String flag, boolean enabledByDefault) {
        this.property = property;
        this.flag = flag;
        this.enabledByDefault = enabledByDefault;
    }

    public String getProperty() {
        return property;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public static Stream<Flags> all() {
        return Stream.of(values());
    }

    public static Stream<Flags> active(TaskConfig tc) {
        return all().filter(f -> f.isActive(tc));
    }

    public boolean isActive(TaskConfig tc) {
        return "true".equalsIgnoreCase(tc.getValue(property));
    }

    public String getCommand(TaskConfig tc) {
        return isActive(tc) ? flag : "";
    }
}
