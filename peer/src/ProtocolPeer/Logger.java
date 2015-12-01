package ProtocolPeer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Manuel on 2015-11-30.
 *
 * Class used to log events into a folder holding connection information
 */
public class Logger
{
    static Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh.mm.ss");
    PrintWriter logPrinter;


    Logger(String connectionIP, String connectionPort)
    {
        Date date = new Date();
        String timeLog = formatter.format(date);
        File fileLog = new File("./logs/" + timeLog + "_Connection_ " + connectionIP + "." + connectionPort + "_Log.txt");

        try {
            logPrinter = new PrintWriter(fileLog, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void addLog(String logString)
    {
        logPrinter.println(logString);
        logPrinter.flush();
    }

    protected  void close()
    {
        logPrinter.close();
    }

}
