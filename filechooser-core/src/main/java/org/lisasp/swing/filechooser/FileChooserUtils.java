package org.lisasp.swing.filechooser;

import java.awt.Window;

import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Fabri
 */
public final class FileChooserUtils {

    private static FileChooser instance;

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
        return instance.setBaseDir(directory);
    }

    public static synchronized String openFile(final Window parent, SimpleFileFilter... ff) {
        return instance.openFile(null, ff, parent);
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
        return instance.openFile(title, ff, parent);
    }

    public static synchronized String saveFile(final Window parent, SimpleFileFilter... ff) {
        return instance.saveFile(null, ff, parent);
    }

    public static synchronized String saveFile(final Window parent, final String title, SimpleFileFilter... ff) {
        return instance.saveFile(title, ff, parent);
    }

    public static synchronized String[] openFiles(final Window parent, final SimpleFileFilter... ff
            ) {
        return instance.openFiles(null, ff, parent);
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
    public static synchronized String[] openFiles(final Window parent, final String title, final SimpleFileFilter... ff
            ) {
        return instance.openFiles(title, ff, parent);
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
        return instance.chooseDirectory(parent);
    }
}