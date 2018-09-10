import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.LogLevel;

/**
 * Class that handles the output of a process or similar.
 * The term of input and output switches from this perspective.
 */
public class OutputStreamHandler extends Thread {
    private InputStream inputStream;
    private Logger logger;
    private LogLevel level;
    private StringBuilder output = new StringBuilder();

    public OutputStreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStreamHandler(InputStream inputStream, Logger logger, LogLevel level) {
        this.logger = logger;
        this.level = level;
        this.inputStream = inputStream;
    }

    public void run() {
        try (Scanner br = new Scanner(new InputStreamReader(inputStream))) {
            String line;
            while (br.hasNextLine()) {
                line = br.nextLine();
                output.append(line).append(System.getProperty("line.separator"));
                if (logger != null)
                    logger.log(level, line);
            }
        }
    }

    public StringBuilder getOutput() {
        return output;
    }
}