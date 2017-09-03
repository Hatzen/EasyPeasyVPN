package de.hartz.vpn.Helper;

import de.hartz.vpn.Utilities.Linux;
import de.hartz.vpn.Utilities.OutputStreamHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Helper Class that contains static methods that are needed in different classes.
 */
public final class Helper {

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static File TEMP_FOLDER;

    /**
     * @returns the platform depended file extension for the config files, to start openvpn.
     */
    public static String getOpenVPNConfigExtension() {
        if (isWindows()) {
            return Statics.OpenVpnValues.CONFIG_EXTENSION_WINDOWS.getValue();
        } else if(isLinux()) {
            return Statics.OpenVpnValues.CONFIG_EXTENSION_LINUX.getValue();
        }
        return null;
    }

    /**
     * Reads a whole file as a String.
     * @param path System path to the file.
     * @param encoding The encoding of the File. Should be UTF-8 in most cases.
     * @returns the String contents of the file.
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * Function to load resources like images from the jar file.
     * @param resourcePath The path to the resource starting from package de.hartz.vpn.
     * @returns a file object representing the file.
     */
    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }
            Path p = Paths.get(resourcePath);
            String fileName = p.getFileName().toString();
            File tempFile = new File(getTempDirectory(), fileName);
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a temporary folder, if not created yet, and returns the path to this folder.
     * https://stackoverflow.com/questions/617414/how-to-create-a-temporary-directory-folder-in-java
     * @return Path to temporary folder.
     * @throws IOException
     */
    public static File getTempDirectory() throws IOException {
        if (Helper.TEMP_FOLDER != null) {
            return Helper.TEMP_FOLDER;
        }
        final File temp = File.createTempFile("easypeasyvpn-", "");
        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        TEMP_FOLDER = temp;
        return temp;
    }

    // TODO: Remove if not needed.
    public static String removeExtensionFromFileString(String file) {
        return file.replaceFirst("[.][^.]+$", "");
    }

    public static boolean deleteTempDirectory() {
        return Helper.TEMP_FOLDER.delete();
    }

    /**
     * Checks if the program was launched with admin permissions.
     * https://stackoverflow.com/questions/4350356/detect-if-java-application-was-run-as-a-windows-admin
     * @return true if program has admin permissions.
     */
    public static boolean isAdmin(){
        Preferences preferences = Preferences.systemRoot();
        PrintStream systemErr = System.err;
        // Better synchronize to avoid problems with other threads that access System.err.
        synchronized(systemErr){
            System.setErr(null);
            try {
                // SecurityException on Windows.
                preferences.put("foo", "bar");
                preferences.remove("foo");

                // BackingStoreException on Linux.
                preferences.flush();
                return true;
            } catch(Exception e) {
                return false;
            } finally{
                System.setErr(systemErr);
            }
        }
    }

    /**
     * Function that indicates whether the system uses apt as packet manager.
     * @return
     */
    @Linux
    public static boolean hasAPT() {
        ProcessBuilder pb = new ProcessBuilder("whereis", "apt");
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
        outputHandler.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (outputHandler.getOutput().toString().length() > 0);
    }

    /**
     * https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
     * @return
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isLinux() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 || isMac());
    }
}
