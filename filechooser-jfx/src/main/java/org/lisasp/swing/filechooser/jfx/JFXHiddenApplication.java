package org.lisasp.swing.filechooser.jfx;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class JFXHiddenApplication extends Application {
    
    private static Stage primaryStage;
   
    public static void launchApplication() {
        new Thread(JFXHiddenApplication::launch).start();
    }
   
    @Override
    public void start(Stage primaryStage) throws Exception {
        JFXHiddenApplication.primaryStage = primaryStage;
   
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(new Pane(), 1, 1));
        primaryStage.show();
    }
   
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
   
    public static void showJavaFXDialog(Stage fxDialog, JFrame swingParent) {
        fxDialog.setOpacity(0d);
        fxDialog.show();
        fxDialog.hide();
        fxDialog.setOpacity(1d);
   
        fxDialog.setX(swingParent.getBounds().getCenterX() - (fxDialog.getWidth() / 2));
        fxDialog.setY(swingParent.getBounds().getCenterY() - (fxDialog.getHeight() / 2));
   
        fakeModalDialog(fxDialog, swingParent);
   
        fxDialog.setAlwaysOnTop(true);
        fxDialog.showAndWait();
    }
   
    private static void fakeModalDialog(Stage fxDialog, JFrame swingParent) {
   
        fxDialog.setOnShown(e -> {
            SwingUtilities.invokeLater(() -> {
                swingParent.setEnabled(false);
                if (swingParent.getJMenuBar() != null) {
                    swingParent.getJMenuBar().setEnabled(false);
                }
            });
        });
   
        fxDialog.setOnHidden(e -> {
            SwingUtilities.invokeLater(() -> {
                swingParent.setEnabled(true);
                if (swingParent.getJMenuBar() != null) {
                    swingParent.getJMenuBar().setEnabled(true);
                }
                swingParent.toFront();
            });
        });
    }
  }