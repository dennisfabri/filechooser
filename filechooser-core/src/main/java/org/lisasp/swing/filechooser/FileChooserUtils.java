package org.lisasp.swing.filechooser;

import java.awt.Window;
import java.io.File;
import java.util.Arrays;

import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Fabri
 */
public final class FileChooserUtils {

    private static FileChooser instance;
    
    private static String directory = null;

    public static void initialize(FileChooser newInstance) {
        if (newInstance != null) {
            FileChooserUtils.instance = newInstance;
        }
    }

    /**
     * Constructor is not needed. Hide it!
     */
    private FileChooserUtils() {
        // Never used
    }
    
    public static synchronized boolean setBaseDir(String directory) {
        if (!directoryExists(directory)) {
            return false;
        }
        FileChooserUtils.directory = directory;
        return true;
    }
    
    private static boolean directoryExists(String directory) {
        File d = new File(directory);
        if (!d.exists()) {
            return false;
        }
        if (!d.isDirectory()) {
            return false;
        }
        return true;
    }

    private static void validateDirectory() {
        if (!directoryExists(directory)) {
            directory = null;
        }
    }

    public static synchronized boolean setBaseDirFromFile(String filename) {
        try {
            String directory = new File(filename).getParent();
            if (new File(directory).isDirectory()) {
                return setBaseDir(directory);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static synchronized String openFile(final Window parent, SimpleFileFilter... ff) {
        validateDirectory();
        String filename = instance.openFile(null, ff,directory, parent);
        if (filename != null) {
            setBaseDirFromFile(filename);
        }
        return appendSuffixIfNecessary(filename, ff);
    }

    /**
     * Erzeugt einen Dateirequester
     * 
     * @param title  Titel
     * @param button Buttonname
     * @param ff     Dateiendungsfilter
     * @param parent UEbergeordnetes Fenster
     * @return Dateiname oder null
     */
    public static synchronized String openFile(final Window parent, final String title, SimpleFileFilter... ff) {
        validateDirectory();
        String filename = instance.openFile(title, ff,directory, parent);
        if (filename != null) {
            setBaseDirFromFile(filename);
        }
        return appendSuffixIfNecessary(filename, ff);
    }

    public static synchronized String saveFile(final Window parent, SimpleFileFilter... ff) {
        validateDirectory();
        String filename = instance.saveFile(null, ff,directory, parent);
        if (filename != null) {
            setBaseDirFromFile(filename);
        }
        return appendSuffixIfNecessary(filename, ff);
    }

    public static synchronized String saveFile(final Window parent, final String title, SimpleFileFilter... ff) {
        validateDirectory();
        String filename = instance.saveFile(title, ff,directory, parent);
        if (filename != null) {
            setBaseDirFromFile(filename);
        }
        return appendSuffixIfNecessary(filename, ff);
    }

    public static synchronized String[] openFiles(final Window parent, final SimpleFileFilter... ff) {
        validateDirectory();
        String[] filenames = instance.openFiles(null, ff,directory, parent);
        if (filenames != null && filenames.length > 0) {
            setBaseDirFromFile(filenames[0]);
        }
        return appendSuffixIfNecessary(filenames, ff);
    }

    /**
     * /** Erzeugt einen Dateirequester
     * 
     * @param title  Titel
     * @param button Buttonname
     * @param ff     Dateiendungsfilter
     * @param parent UEbergeordnetes Fenster
     * @return Dateiname oder null
     */
    public static synchronized String[] openFiles(final Window parent, final String title,
            final SimpleFileFilter... ff) {
        validateDirectory();
        String[] filenames = instance.openFiles(title, ff,directory, parent);
        if (filenames != null && filenames.length > 0) {
            setBaseDirFromFile(filenames[0]);
        }
        return appendSuffixIfNecessary(filenames, ff);
    }

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param title  Titel
     * @param button Buttonbeschriftung
     * @param parent UEbergeordnetes Fenster
     * @return Verzeichnisname
     */
    public static synchronized String chooseDirectory(final Window parent) {
        validateDirectory();
        String dir = instance.chooseDirectory(directory,parent);
        if (dir != null) {
            setBaseDir(dir);
        }
        return dir;
    }
    
    private static String[] appendSuffixIfNecessary(String[] filenames, SimpleFileFilter[] filters) {
        return Arrays.stream(filenames).map(filename -> appendSuffixIfNecessary(filename, filters)).toArray(String[]::new);
    }
    
    private static String appendSuffixIfNecessary(String filename, SimpleFileFilter[] filters) {
        if (filename == null || filters == null || filters.length == 0) {
            return filename;
        }
        if (Arrays.stream(filters).noneMatch(filter -> filter.accept(new File(filename)))) {
            return String.format("%s%s", filename, filters[0].getSuffixes()[0]);
        }
        return filename;
    }
}