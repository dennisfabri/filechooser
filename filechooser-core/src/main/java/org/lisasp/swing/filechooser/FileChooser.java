package org.lisasp.swing.filechooser;

import java.awt.Window;

import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

public interface FileChooser {

    boolean setBaseDir(String directory);

    /*
     * Erzeugt einen Dateirequester
     * 
     * @param title Titel
     * 
     * @param button Buttonname
     * 
     * @param ff Dateiendungsfilter
     * 
     * @param parent UEbergeordnetes Fenster
     * 
     * @return Dateiname oder null
     */
    String[] openFiles(String title, SimpleFileFilter[] ff, Window parent);

    String openFile(String title, SimpleFileFilter[] ff, Window parent);

    String saveFile(String title, SimpleFileFilter[] ff, Window parent);

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param window parent window
     * @return Verzeichnisname
     */
    String chooseDirectory(Window parent);
}