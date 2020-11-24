package org.lisasp.swing.filechooser.jfx;

import java.awt.Window;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.lisasp.swing.filechooser.FileChooser;
import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;

import javafx.application.Platform;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Fabri
 */
public class FileChooserJFX implements FileChooser {

    static {
        try {
            final CountDownLatch latch = new CountDownLatch(1);

            Platform.setImplicitExit(false);
            Platform.startup(new Runnable() {
                @Override
                public void run() {
                    latch.countDown();
                }
            });
            if (!latch.await(5L, TimeUnit.SECONDS)) {
                throw new ExceptionInInitializerError();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String dir = "";

    protected String getCurrentDirectory() {
        return dir;
    }

    @Override
    public synchronized boolean setBaseDir(String directory) {
        File d = new File(directory);
        if (!d.exists()) {
            return false;
        }
        if (!d.isDirectory()) {
            return false;
        }
        dir = directory;
        return true;
    }

    @Override
    public synchronized String[] openFiles(final String title, SimpleFileFilter[] ff, final Window parent) {
        List<File> files = createFileChooser(title, ff).showDialog(ch -> ch.showOpenMultipleDialog(null));

        if (files == null || files.isEmpty()) {
            return new String[0];
        }
        return files.stream().map(f -> f.getAbsolutePath()).toArray(String[]::new);
    }

    private ExtensionFilter[] getExtensionFilters(SimpleFileFilter[] ff) {
        if (ff == null || ff.length == 0) {
            return new ExtensionFilter[0];
        }
        return Arrays.stream(ff).map(f -> getExtensionFilter(f)).toArray(ExtensionFilter[]::new);
    }

    private ExtensionFilter getExtensionFilter(SimpleFileFilter f) {
        return new ExtensionFilter(f.getName(),
                Arrays.stream(f.getSuffixes()).map(fx -> "*" + fx).toArray(String[]::new));
    }

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param parent Uebergeordnetes Fenster
     * @return Verzeichnisname
     */
    @Override
    public synchronized String chooseDirectory(final Window parent) {
        SynchronousJFXDirectoryChooser chooser = new SynchronousJFXDirectoryChooser(() -> {
            javafx.stage.DirectoryChooser ch = new javafx.stage.DirectoryChooser();
            if (dir != null && !dir.isBlank()) {
                try {
                    ch.setInitialDirectory(new File(dir));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return ch;
        });

        File file = chooser.showDialog(ch -> ch.showDialog(null));
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    @Override
    public synchronized String openFile(final String title, SimpleFileFilter[] ff, final Window parent) {
        File file = createFileChooser(title, ff).showDialog(ch -> ch.showOpenDialog(null));

        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    @Override
    public synchronized String saveFile(final String title, SimpleFileFilter[] ff, final Window parent) {
        File file = createFileChooser(title, ff).showDialog(ch -> ch.showSaveDialog(null));

        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    private SynchronousJFXFileChooser createFileChooser(final String title, SimpleFileFilter[] ff) {
        return new SynchronousJFXFileChooser(() -> {
            javafx.stage.FileChooser ch = new javafx.stage.FileChooser();
            if (title != null && !title.isBlank()) {
                ch.setTitle(title);
            }
            if (dir != null && !dir.isBlank()) {
                try {
                    ch.setInitialDirectory(new File(dir));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (ff != null && ff.length > 0) {
                ch.getExtensionFilters().addAll(getExtensionFilters(ff));
            }
            return ch;
        });
    }
}