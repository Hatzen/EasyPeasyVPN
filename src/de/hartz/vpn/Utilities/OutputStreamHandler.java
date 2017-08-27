package de.hartz.vpn.Utilities;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by kaiha on 03.06.2017.
 * Class that handles the output of a process or similar.
 * The term of input and output switches from this perspective.
 */
public class OutputStreamHandler extends Thread {
    private InputStream inputStream;
    private Logger listener;
    private StringBuilder output = new StringBuilder();

    public OutputStreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStreamHandler(InputStream inputStream, Logger listener) {
        this.listener = listener;
        this.inputStream = inputStream;
    }

    public void run() {
        try (Scanner br = new Scanner(new InputStreamReader(inputStream))) {
            String line;
            while (br.hasNextLine()) {
                line = br.nextLine();
                output.append(line).append(System.getProperty("line.separator"));
                if (listener != null)
                    listener.addLogLine(line);
            }
        }
    }

    public StringBuilder getOutput() {
        return output;
    }
}