package ru.compscicenter.practice.searcher.codeformatter;

import java.io.File;

/**
 * Created by Станислав on 09.12.2016.
 */
public class AStyleInterface
{   static private String libraryName = "lib/AStyle-2.05.1jd.dll";

    /**
     * Call the AStyleMain function in Artistic Style.
     * @param   textIn   A string containing the source code to be formatted.
     * @param   options  A string of options to Artistic Style.
     * @return  A String containing the formatted source from Artistic Style,
     *         or an empty string on error.
     */
    public String formatSource(String textIn, String options)
    {   // Return the allocated string
        // Memory space is allocated by OnAStyleMemAlloc, a callback function from AStyle
        String textOut = new String("");
        try
        {   textOut = AStyleMain(textIn, options);
        }
        catch (UnsatisfiedLinkError e)
        {   //~ System.out.println(e.getMessage());
            error("Cannot call the Java AStyleMain function");
        }
        return textOut;
    }

    /**
     * Call the AStyleGetVersion function in Artistic Style.
     * @return  A String containing the version number from Artistic Style.
     */
    public String getVersion()
    {   String version = new String();
        try
        {   version = AStyleGetVersion();
        }
        catch (UnsatisfiedLinkError e)
        {   //~ System.out.println(e.getMessage());
            error("Cannot call the Java AStyleGetVersion function");
        }
        return version;
    }

    /**
     * Error message function for this example.
     */
    private static void error(String message)
    {   System.out.println(message);
        System.out.println("The program has terminated!");
        System.exit(1);
    }

    // methods to load Artistic Style -----------------------------------------

    /**
     * Static constructor to load the native Artistic Style library.
     * Does not need to terminate if the shared library fails to load.
     * But the exception must be handled when a function is called.
     */
    static
    {   // load shared library from the classpath
        String astyleDirectory = System.getProperty("user.dir");
        String astyleName = getLibraryName(astyleDirectory);
        String astylePath = astyleDirectory
                + System.getProperty("file.separator")
                + astyleName;

        System.out.println(astylePath);

        try
        {   System.load(astylePath);
        }
        catch (UnsatisfiedLinkError e)
        {   System.out.println(e.getMessage());
            error("Cannot load native library " + astylePath);
        }
    }

    /**
     * Called by static constructor to get the shared library name.
     * This will get any version of the library in the classpath.
     * Usually a specific version would be obtained, in which case a constant
     * could be used for the library name.
     * @param  astyleDirectory  The directory containing the shared library.
     * @return  The name of the shared library found in the directory.
     */
    static private String getLibraryName(String astyleDirectory)
    {   // get the shared library extension for the platform
        String fileExt = System.mapLibraryName("");
        int dot = fileExt.indexOf(".");
        fileExt = fileExt.substring(dot);
        // get a library name in the classpath
        File directory = new File(astyleDirectory);
        for (File filePath : directory.listFiles())
        {   String fileName = filePath.getName().toLowerCase();
            if (filePath.isFile()
                    && fileName.endsWith(fileExt)
                    && (fileName.startsWith("astyle")
                    || fileName.startsWith("libastyle"))
                    &&  fileName.contains("j"))
            {   libraryName = filePath.getName();
                break;
            }
        }
        if (libraryName == null)
        {   error("Cannot find astyle native library in "
                + astyleDirectory
                + System.getProperty("file.separator"));
        }
        return libraryName;
    }

    // methods to call Artistic Style -----------------------------------------

    /**
     * Calls the Java AStyleMain function in Artistic Style.
     * The function name is constructed from method names in this program.
     * @param   textIn   A string containing the source code to be formatted.
     * @param   options  A string of options to Artistic Style.
     * @return  A String containing the formatted source from Artistic Style.
     */
    public native String AStyleMain(String textIn, String options);

    /**
     * Calls the Java AStyleGetVersion function in Artistic Style.
     * The function name is constructed from method names in this program.
     *
     * @return    A String containing the version number of Artistic Style.
     */
    public native String AStyleGetVersion();

    /**
     * Error handler for messages from Artistic Style.
     * This method is called only if there are errors when AStyleMain is called.
     * This is for debugging and there should be no errors when the calling
     * parameters are correct.
     * Changing the method name requires changing Artistic Style.
     * Signature: (ILjava/lang/String;)V.
     *  @param  errorNumber   The error number from Artistic Style.
     *  @param  errorMessage  The error message from Artistic Style.
     */
    private void ErrorHandler(int errorNumber, String errorMessage)
    {   System.out.println("AStyle error "
            + String.valueOf(errorNumber)
            + " - " + errorMessage);
    }

}

