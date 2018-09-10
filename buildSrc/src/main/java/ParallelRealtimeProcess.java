// https://guides.gradle.org/using-the-worker-api/

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;
import org.gradle.workers.*;
import org.gradle.api.logging.Logger;

import javax.inject.Inject;
import java.io.File;

class ParallelRealtimeProcess extends DefaultTask {
    private final WorkerExecutor workerExecutor; 

  	@Input
    String[][] args;
    @Input
    String cwd;
    @Input
    static Logger logger;

    @Inject
    public ParallelRealtimeProcess(WorkerExecutor workerExecutor) { 
        super();
        this.workerExecutor = workerExecutor;
    }

    @TaskAction
    public void startProcesses() {
        for (int i = 0; i < args.length; ++i) {
            final int index = i;
            workerExecutor.submit(ProcessRunner.class, new Action<WorkerConfiguration>() {
                @Override
                public void execute(WorkerConfiguration config) {
                    System.err.println("" + index);
                    config.setIsolationMode(IsolationMode.NONE);
                    config.params(args[index], cwd);
                }
            });
        }
        workerExecutor.await();
    }
}