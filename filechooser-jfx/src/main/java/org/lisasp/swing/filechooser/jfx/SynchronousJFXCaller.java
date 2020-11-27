package org.lisasp.swing.filechooser.jfx;

import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A utility class to execute a Callable synchronously on the JavaFX event
 * thread.
 *
 * Source:
 * https://stackoverflow.com/questions/28920758/javafx-filechooser-in-swing
 * 
 * @param <T> the return type of the callable
 */
class SynchronousJFXCaller<T> {
    private final Function<Stage, T> callable;

    /**
     * Constructs a new caller that will execute the provided callable.
     * 
     * The callable is accessed from the JavaFX event thread, so it should either be
     * immutable or at least its state shouldn't be changed randomly while the
     * call() method is in progress.
     * 
     * @param callable the action to execute on the JFX event thread
     */
    public SynchronousJFXCaller(Function<Stage, T> callable) {
        this.callable = callable;
    }

    private JDialog getCheckedParent(Window parent) {
        final JDialog modalBlocker = parent == null ? new JDialog() : new JDialog(parent);
        modalBlocker.setModal(true);
        modalBlocker.setUndecorated(true);
        modalBlocker.setOpacity(0.0f);
        modalBlocker.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        modalBlocker.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                Platform.runLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        Optional<Stage> maybeStage = javafx.stage.Window.getWindows().stream().filter(w -> w instanceof Stage).map(w -> (Stage)w).findFirst();
                        if (maybeStage.isPresent()) {
                            maybeStage.get().toFront();
                        }
                    }
                });
            }
        });
        return modalBlocker;
    }

    /**
     * Executes the Callable.
     * <p>
     * A specialized task is run using Platform.runLater(). The calling thread then
     * waits first for the task to start, then for it to return a result. Any
     * exception thrown by the Callable will be rethrown in the calling thread.
     * </p>
     * 
     * @param startTimeout     time to wait for Platform.runLater() to
     *                         <em>start</em> the dialog-showing task
     * @param startTimeoutUnit the time unit of the startTimeout argument
     * @return whatever the Callable returns
     * @throws IllegalStateException if Platform.runLater() fails to start the task
     *                               within the given timeout
     * @throws InterruptedException  if the calling (this) thread is interrupted
     *                               while waiting for the task to start or to get
     *                               its result (note that the task will still run
     *                               anyway and its result will be ignored)
     */
    public T call(Window parent, long startTimeout, TimeUnit startTimeoutUnit) throws Exception {
        final CountDownLatch taskStarted = new CountDownLatch(1);
        // Can't use volatile boolean here because only finals can be accessed
        // from closures like the lambda expression below.
        final AtomicBoolean taskCancelled = new AtomicBoolean(false);
        // a trick to emulate modality:
        final JDialog modalBlocker = getCheckedParent(parent);
        final CountDownLatch modalityLatch = new CountDownLatch(1);
        final FutureTask<T> task = new FutureTask<>(() -> {
            synchronized (taskStarted) {
                if (taskCancelled.get()) {
                    return null;
                } else {
                    taskStarted.countDown();
                }
            }
            Stage stage = new Stage();
            try {
                Optional<Image[]> icons = new IconConverter().getIcons(parent);
                if (icons.isPresent()) {
                    stage.getIcons().addAll(icons.get());
                } else {
                    stage.getIcons().add(new Image("/org/lisasp/swing/filechooser/jfx/empty.png"));
                }
                stage.setTitle("jfx-filechooser");
                stage.initStyle(StageStyle.UTILITY);
                stage.setOpacity(0);
                stage.show();

                return callable.apply(stage);
            } finally {
                stage.close();                
                
                // Wait until the Swing thread is blocked in setVisible():
                modalityLatch.await();
                // and unblock it:
                SwingUtilities.invokeLater(() -> modalBlocker.setVisible(false));                
            }
        });
        Platform.runLater(task);
        if (!taskStarted.await(startTimeout, startTimeoutUnit)) {
            synchronized (taskStarted) {
                // the last chance, it could have been started just now
                if (!taskStarted.await(0, TimeUnit.MILLISECONDS)) {
                    // Can't use task.cancel() here because it would
                    // interrupt the JavaFX thread, which we don't own.
                    taskCancelled.set(true);
                    throw new IllegalStateException("JavaFX was shut down" + " or is unresponsive");
                }
            }
        }
        // a trick to notify the task AFTER we have been blocked
        // in setVisible()
        SwingUtilities.invokeLater(() -> {
            // notify that we are ready to get the result:
            modalityLatch.countDown();
        });
        modalBlocker.setVisible(true); // blocks
        modalBlocker.dispose(); // release resources
        try {
            return task.get();
        } catch (ExecutionException ex) {
            Throwable ec = ex.getCause();
            if (ec instanceof Exception) {
                throw (Exception) ec;
            } else if (ec instanceof Error) {
                throw (Error) ec;
            } else {
                throw new AssertionError("Unexpected exception type", ec);
            }
        }
    }

}