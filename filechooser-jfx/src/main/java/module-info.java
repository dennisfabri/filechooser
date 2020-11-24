module org.lisasp.swing.filechooser.jfx {
    exports org.lisasp.swing.filechooser.jfx;

    requires transitive org.lisasp.swing.filechooser;
    requires transitive java.desktop;
    requires javafx.base;
    requires javafx.graphics;
}