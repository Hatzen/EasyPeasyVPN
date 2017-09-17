package de.hartz.vpn.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
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
        /*
        TODO: Check if this works always.
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return output;
    }
}