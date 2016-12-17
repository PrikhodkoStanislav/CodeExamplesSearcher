import org.apache.commons.lang.SystemUtils;

import java.io.File;

/**
 * Created by Станислав on 09.12.2016.
 */
public class AStyleInterface
{
    public static String m(String code) {
        AStyleInterface a = new AStyleInterface();
        return a.formatSource(code, options);
    }
    
    static private String options = "-A2tOP";

    static private String libraryName = "lib/AStyle-2.05.1jd.dll";
    static private String libraryNameForLinux = "lib/libastyle-2.05.1j.so";

    /**
     * Call the AStyleMain function in Artistic Style.
     * @param   textIn   A string containing the source code to be formatted.
     * @param   options  A string of options to Artistic Style.
     * @return  A String containing the formatted source from Artistic Style,
     *         or an empty string on error.
     */
    private String formatSource(String textIn, String options)
    {   // Return the allocated string
        // Memory space is allocated by OnAStyleMemAlloc, a callback function from AStyle
        String textOut = "";
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
    {   String version = "";
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
        if (SystemUtils.IS_OS_LINUX) {
            libraryName = libraryNameForLinux;
        }
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
    
}

