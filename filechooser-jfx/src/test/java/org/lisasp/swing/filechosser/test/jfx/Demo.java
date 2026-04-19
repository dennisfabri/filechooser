package org.lisasp.swing.filechosser.test.jfx;

import org.lisasp.swing.filechooser.filefilter.SimpleFileFilter;
import org.lisasp.swing.filechooser.jfx.FileChooserJFX;

public class Demo {
    public static void main(String[] args) {
        System.out.println("Demo");
        var fc = new FileChooserJFX();
        String filename = fc.openFile("Demo",
                                      new SimpleFileFilter[]{new SimpleFileFilter("Markdown", "md"), new SimpleFileFilter("PDF", "pdf"), new SimpleFileFilter(
                                              "Text",
                                              "txt")},
                                      ".",
                                      null);
        if (filename == null) {
            System.out.println("No file selected");
        } else {
            System.out.println("Selected file: " + filename);
        }
        System.exit(0);
    }
}
