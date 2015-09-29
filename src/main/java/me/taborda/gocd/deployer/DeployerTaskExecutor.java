package me.taborda.gocd.deployer;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;

public class DeployerTaskExecutor implements TaskExecutor {

    private static final Logger LOGGER = Logger.getLoggerFor(DeployerTaskExecutor.class);

    private static final String ENVIRONMENT_VAR = "DEPLOYER_HOME";
    private static final String DEPLOYER_COMMAND = "deploy.py";

    @Override
    public ExecutionResult execute(TaskConfig tc, TaskExecutionContext tec) {
        Console console = tec.console();
        ProcessBuilder deploy = createCommand(tc, tec);

        try {
            Process process = deploy.start();
            console.readErrorOf(process.getErrorStream());
            console.readOutputOf(process.getInputStream());

            int exitCode = process.waitFor();
            process.destroy();
            if (exitCode != 0) {
                return ExecutionResult.failure("Build failure");
            }
        } catch (Exception t) {
            console.printLine(t.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            console.printLine(sw.toString());
            return ExecutionResult.failure("Build failure: " + t.getMessage(), t);
        }

        return ExecutionResult.success("Build success");
    }

    private ProcessBuilder createCommand(TaskConfig tc, TaskExecutionContext tec) {
        List<String> command = new ArrayList<>();

        Map<String, String> env = tec.environment().asMap();
        if (env.containsKey(ENVIRONMENT_VAR)) {
            command.add(new File(env.get(ENVIRONMENT_VAR), DEPLOYER_COMMAND).getPath());
        } else {
            command.add(DEPLOYER_COMMAND);
        }

        command.addAll(Configs.all().map(f -> f.getCommand(tc)).collect(toList()));
        command.addAll(Flags.active(tc).map(f -> f.getCommand(tc)).collect(toList()));
        LOGGER.info("Will execute command: " + command);

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.environment().putAll(tec.environment().asMap());
        builder.directory(new File(tec.workingDir()));
        return builder;
    }

}
