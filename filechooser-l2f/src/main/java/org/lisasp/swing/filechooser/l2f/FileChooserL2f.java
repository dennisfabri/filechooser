package org.lisasp.swing.filechooser.l2f;

import java.awt.Window;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;

import org.lisasp.swing.filechooser.FileChooser;
import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

import com.l2fprod.common.directorychooser.JDirectoryChooser;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Fabri
 */
public class FileChooserL2f implements FileChooser {

    private synchronized String[] openFiles(final String title, String button, SimpleFileFilter[] ff,
            final boolean singleSelection, String dir, final Window parent) {
        JFileChooser jFC = new JFileChooser();
        if (dir != null) {
            jFC.setCurrentDirectory(new File(dir));
        }
        if (ff != null && ff.length == 0) {
            ff = null;
        }
        if (ff != null) {
            for (SimpleFileFilter aFf : ff) {
                if (aFf != null) {
                    jFC.addChoosableFileFilter(aFf);
                }
            }
            jFC.setFileFilter(ff[0]);
        }
        jFC.setAcceptAllFileFilterUsed(ff == null);
        jFC.setMultiSelectionEnabled(!singleSelection);
        jFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFC.setDialogTitle(title);
        int wert = jFC.showDialog(parent, button);
        if (wert == JFileChooser.APPROVE_OPTION) {
            File[] files = null;
            if (singleSelection) {
                File f = jFC.getSelectedFile();
                if (f == null) {
                    return null;
                }
                files = new File[] { f };
            } else {
                files = jFC.getSelectedFiles();
            }

            if ((files == null) || (files.length == 0)) {
                return null;
            }
            String[] names = new String[files.length];
            for (int x = 0; x < files.length; x++) {
                names[x] = jFC.getCurrentDirectory().getPath() + java.io.File.separator + files[x].getName();
                if (ff != null) {
                    boolean ends = false;
                    String[] suffixes = null;
                    try {
                        suffixes = ((SimpleFileFilter) jFC.getFileFilter()).getSuffixes();
                    } catch (NullPointerException npe) {
                        LinkedList<String> suf = new LinkedList<String>();
                        for (SimpleFileFilter aFf : ff) {
                            String[] temp = aFf.getSuffixes();
                            for (String aTemp : temp) {
                                suf.addLast(aTemp);
                            }
                        }
                        suffixes = suf.toArray(new String[suf.size()]);
                    }
                    for (String suffixe : suffixes) {
                        if (names[x].toLowerCase().endsWith(suffixe.toLowerCase())) {
                            ends = true;
                        }
                    }
                    if (!ends) {
                        names[x] += suffixes[0];
                    }
                }
            }
            return names;
        }
        // Benutzer hat abgebrochen, also machen wir nichts...
        return null;
    }

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param title  Titel
     * @param button Buttonbeschriftung
     * @param parent UEbergeordnetes Fenster
     * @return Verzeichnisname
     */
    @Override
    public synchronized String chooseDirectory(String dir, final Window parent) {
        JDirectoryChooser chooser = new JDirectoryChooser(dir);
        int choice = chooser.showOpenDialog(parent);
        if (choice == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return chooser.getSelectedFile().getAbsolutePath();
    }

    @Override
    public String[] openFiles(String title, SimpleFileFilter[] ff, String dir, Window parent) {
        return openFiles(title, "Open", ff, false, dir, parent);
    }

    @Override
    public String openFile(String title, SimpleFileFilter[] ff, String dir, Window parent) {
        String[] result = openFiles(title, "Open", ff, true, dir, parent);
        if (result == null || result.length == 0) {
            return null;
        }
        return result[0];
    }

    @Override
    public String saveFile(String title, SimpleFileFilter[] ff, String dir, Window parent) {
        String[] result = openFiles(title, "Save", ff, true, dir, parent);
        if (result == null || result.length == 0) {
            return null;
        }
        return result[0];
    }
}