package me.taborda.gocd.deployer;

import java.util.stream.Stream;

import com.thoughtworks.go.plugin.api.task.TaskConfig;

public enum Configs {

    CONFIG("Config", "--config"),
    COMPONENT("Component", "--component");

    private final String property;
    private final String flag;

    Configs(String property, String flag) {
        this.property = property;
        this.flag = flag;
    }

    public String getProperty() {
        return property;
    }

    public static Stream<Configs> all() {
        return Stream.of(values());
    }

    public boolean isActive(TaskConfig tc) {
        String v = tc.getValue(property);
        return v != null && !v.trim().isEmpty();
    }

    public String getCommand(TaskConfig tc) {
        return isActive(tc) ? String.format("%s %s", flag, tc.getValue(property)) : "";
    }
}
