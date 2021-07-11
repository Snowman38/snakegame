package logging;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * A custom logger to print formatted information to the console
 */
public class Logger {

    private static boolean enableLogging = true;

    private Logger() {

    }

    /**
     * Formats an error and prints it to the console (In red)
     *
     * @param message the error message
     * @author Daniel Batchford
     */
    public static void error(String message) {

        String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        toConsole(Status.ERROR, fileName, className, methodName, message);
    }

    public static void error(ReadOnlyBooleanProperty errorProperty) {

        error(errorProperty.toString());
    }

    /**
     * Formats a debug message and prints it to the console (In white)
     *
     * @param message the debug message
     * @author Daniel Batchford
     */
    public static void debug(String message) {

        String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        toConsole(Status.DEBUG, fileName, className, methodName, message);
    }

    /**
     * Enables logging to the console
     *
     * @author Daniel Batchford
     */
    public static void enableLogging() {

        enableLogging = true;
    }

    /**
     * Disables logging to the console
     *
     * @author Daniel Batchford
     */
    public static void disableLogging() {

        enableLogging = false;
    }

    /**
     * Toggles logging to the console
     *
     * @param status Whether to log to the console or not
     * @author Daniel Batchford
     */
    public static void setLogging(boolean status) {

        enableLogging = status;
    }

    // Formats the message and prints it, with the file, class and method names appended
    private static void toConsole(Status status, String fileName, String className, String methodName, String message) {

        if (!enableLogging) {
            return;
        }

        String colorCode = switch (status) {
            case DEBUG -> "\u001B[30m";
            case ERROR -> "\u001B[35m";
        };

        String resetCode = "\u001B[0m";

        String formattedFileName = fileName.substring(0, fileName.length() - 5);
        String header = colorCode + "[" + formattedFileName + "] [" + className + "] [" + methodName + "]";
        int bufferSize = 80 - header.length();
        String spaces = String.format("%" + bufferSize + "s", "");
        System.out.println(header + spaces + message + resetCode);
    }

    private enum Status {
        ERROR, DEBUG
    }
}
