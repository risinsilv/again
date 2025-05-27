package edu.curtin.app.output;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class to setup and provide a centralized application logger.
 */
public class LoggerSetup
{
    private static Logger logger;

    private LoggerSetup() {}

    public static Logger getLogger()
    {
        if (logger == null)
        {
            synchronized (LoggerSetup.class)
            {
                if (logger == null)
                {
                    logger = Logger.getLogger("SimulationLogger");
                    try
                    {
                        FileHandler fh = new FileHandler("simulation.log", true);
                        fh.setFormatter(new SimpleFormatter());
                        logger.addHandler(fh);
                        logger.setLevel(Level.ALL);
                        logger.setUseParentHandlers(false); // Only log to file
                    }
                    catch (IOException e)
                    {
                        System.err.println("Failed to set up logger: " + e.getMessage());
                    }
                }
            }
        }
        return logger;
    }
}
