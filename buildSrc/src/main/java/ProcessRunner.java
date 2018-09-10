import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.LogLevel;

import javax.inject.Inject;

/**
 * This class executes a process with a given parameters.
 */
public class ProcessRunner extends Thread {
    private String[] args;
    private String cwd;
  	private Logger logger;

    private boolean running;
    private Process process;

    @Inject
    public ProcessRunner(String[] args, String cwd) {
        this.args = args;
        this.cwd = cwd;
      	this.logger = ParallelRealtimeProcess.logger;
        start();
    }

    @Override
    public void run() {
        logger.log( LogLevel.ERROR, "Start " + Arrays.toString(args));
        running = true;
        
        ProcessBuilder pb = new ProcessBuilder( args );
        //pb.redirectErrorStream(true);
        pb.directory(new File(cwd));
      
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream(), logger, LogLevel.INFO);
        outputHandler.start();
        OutputStreamHandler errorHandler = new OutputStreamHandler(process.getErrorStream(), logger, LogLevel.ERROR);
        errorHandler.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //int exitValue = process.exitValue();
      
        running = false;
        logger.log( LogLevel.ERROR, "End"  + Arrays.toString(args));
    }

    public void exitProcess() {
        if(process != null)
            process.destroy();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}