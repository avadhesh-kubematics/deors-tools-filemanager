package deors.tools.filemanager.dateshifter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import deors.core.sensible.SensibleToolkit;

/**
 * Runnable class used to run the application.
 *
 * @author deors
 * @version 1.0
 */
public final class DateShifterRunner implements Runnable {

    /**
     * The GUI look and feel - Substance Creme Coffee.
     */
    private static final String LOOK_AND_FEEL = "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel"; //$NON-NLS-1$

    /**
     * Default constructor.
     */
    public DateShifterRunner() {
        super();
    }

    /**
     * Main method.
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new DateShifterRunner());
    }

    /**
     * Runs the application by creating, locating and showing
     * its main window.
     */
    public void run() {

        if (!SensibleToolkit.setLookAndFeel(LOOK_AND_FEEL)) {
            SensibleToolkit.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        DateShifterFrame application = new DateShifterFrame();
        SensibleToolkit.centerWindow(application);
        application.setVisible(true);
    }
}
