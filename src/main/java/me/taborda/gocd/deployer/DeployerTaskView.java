package me.taborda.gocd.deployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.thoughtworks.go.plugin.api.task.TaskView;

public class DeployerTaskView implements TaskView {

    @Override
    public String displayValue() {
        return "Deploy";
    }

    @Override
    public String template() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/views/deployertask.template.html")))) {
            return reader.lines().reduce("", (acc, s) -> acc + s);
        } catch (IOException e) {
            return "Failed to find template: " + e.getMessage();
        }
    }

}
