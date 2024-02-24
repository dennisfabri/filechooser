package org.lisasp.swing.filechooser;

import java.awt.Window;

import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

public interface FileChooser {
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
    String[] openFiles(String title, SimpleFileFilter[] ff, String directory, Window parent);

    String openFile(String title, SimpleFileFilter[] ff, String directory, Window parent);

    String saveFile(String title, SimpleFileFilter[] ff, String directory, Window parent);

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param window parent window
     * @return Verzeichnisname
     */
    String chooseDirectory(String directory, Window parent);
}