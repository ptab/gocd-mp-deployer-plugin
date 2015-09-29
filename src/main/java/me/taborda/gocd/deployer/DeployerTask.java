package me.taborda.gocd.deployer;

import static java.util.stream.Collectors.toList;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import com.thoughtworks.go.plugin.api.task.TaskView;

@Extension
public class DeployerTask implements Task {

    private static final Logger LOGGER = Logger.getLoggerFor(DeployerTask.class);

    @Override
    public TaskConfig config() {
        TaskConfig config = new TaskConfig();
        Configs.all().forEach(c -> config.addProperty(c.getProperty()));
        Flags.all().forEach(f -> config.addProperty(f.getProperty()).withDefault(Boolean.toString(f.isEnabledByDefault())));
        return config;
    }

    @Override
    public TaskExecutor executor() {
        return new DeployerTaskExecutor();
    }

    @Override
    public TaskView view() {
        return new DeployerTaskView();
    }

    @Override
    public ValidationResult validate(TaskConfig tc) {
        StringBuilder builder = new StringBuilder();
        Flags.active(tc).forEach(f -> builder.append(f.getCommand(tc)).append(" "));
        Configs.all().forEach(f -> builder.append(f.getCommand(tc)).append(" "));
        LOGGER.info("Saved configuration: deploy.py " + builder.toString());

        ValidationResult result = new ValidationResult();
        result.addErrors(Configs.all().filter(c -> !c.isActive(tc)).map(c -> new ValidationError(c.getProperty(), "Required field")).collect(toList()));
        return result;
    }
}
