package deors.tools.filemanager.timezoneshifter;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker.StateValue;

import deors.core.commons.ActivityListener;
import deors.core.commons.StringToolkit;
import deors.core.sensible.SensibleCheckBox;
import deors.core.sensible.SensibleString;
import deors.core.sensible.SensibleTextField;
import deors.core.sensible.SensibleToolkit;
import deors.tools.filemanager.FileManagerRunner;
import deors.tools.filemanager.Resources;

/**
 * The TimeZoneShifter frame.
 *
 * @author deors
 * @version 1.0
 */
public final class TimeZoneShifterFrame
extends JFrame
implements ActionListener, WindowListener, PropertyChangeListener, ActivityListener {

    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -1623381010486274773L;

    /**
     * The main menu bar.
     */
    private JMenuBar mainMenuBar;

    /**
     * The 'file' menu.
     */
    private JMenu fileMenu;

    /**
     * The 'exit' menu item.
     */
    private JMenuItem exitMenuItem;

    /**
     * The 'help' menu.
     */
    private JMenu helpMenu;

    /**
     * The 'about' menu item.
     */
    private JMenuItem aboutMenuItem;

    /**
     * The main content panel.
     */
    private JPanel mainContentPanel;

    /**
     * The dialog confirmation button.
     */
    private JButton okButton;

    /**
     * The TimeZoneShifterObject instance that will hold the
     * values and take care of control behaviors in this frame.
     */
    private TimeZoneShifterObject timeZoneShifter;

    /**
     * The root directory label control.
     */
    private JLabel rootDirLabel;

    /**
     * The root directory field control.
     */
    private SensibleTextField rootDirField;

    /**
     * The time zone shift label control.
     */
    private JLabel tzShiftLabel;

    /**
     * The time zone shift field control.
     */
    private SensibleTextField tzShiftField;

    /**
     * The apply to files check control.
     */
    private SensibleCheckBox applyToFilesCheck;

    /**
     * The apply to directories check control.
     */
    private SensibleCheckBox applyToDirectoriesCheck;

    /**
     * The recurse subdirectories check control.
     */
    private SensibleCheckBox recurseCheck;

    /**
     * The filter files check control.
     */
    private SensibleCheckBox filterCheck;

    /**
     * The filter expression label control.
     */
    private JLabel filterRegexLabel;

    /**
     * The filter expression field control.
     */
    private SensibleTextField filterRegexField;

    /**
     * The log scroll panel.
     */
    private JScrollPane logScrollPanel;

    /**
     * The log text area control.
     */
    private JTextArea logTextArea;

    /**
     * The scroll lock button.
     */
    private JToggleButton scrollLockButton;

    /**
     * Scroll lock flag.
     */
    private boolean scrollLocked;

    /**
     * The show console button.
     */
    private JToggleButton showConsoleButton;

    /**
     * Whether the process is running.
     */
    private boolean runningProcess;

    /**
     * The process worker.
     */
    private TimeZoneShifterWorker worker;

    /**
     * Default constructor.
     */
    public TimeZoneShifterFrame() {

        super();
        initialize();
    }

    /**
     * This method initializes the dialog.
     */
    private void initialize() {

        final int w = 800;
        final int h = 275;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(Resources.TIMEZONESHIFTER_TITLE);
        setSize(w, h);
        setResizable(false);

        setJMenuBar(getMainMenuBar());
        setContentPane(getMainContentPanel());

        addWindowListener(this);

        List<Image> iconList = new ArrayList<Image>();
        iconList.add(SensibleToolkit.createImageIcon(Resources.IMG_DEORS).getImage());
        setIconImages(iconList);

        if (FileManagerRunner.rootDir != null) {
            getTimeZoneShifter().setRootDir(new SensibleString(FileManagerRunner.rootDir));
        }
    }

    /**
     * This method initializes the main menu bar.
     *
     * @return the main menu bar
     */
    private JMenuBar getMainMenuBar() {

        if (mainMenuBar == null) {

            mainMenuBar = new JMenuBar();
            mainMenuBar.add(getFileMenu());
            mainMenuBar.add(getHelpMenu());
        }
        return mainMenuBar;
    }

    /**
     * This method initializes the 'file' menu.
     *
     * @return the 'file' menu
     */
    private JMenu getFileMenu() {

        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText(Resources.FILE);
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes the 'exit' menu item.
     *
     * @return the 'exit' menu item
     */
    private JMenuItem getExitMenuItem() {

        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText(Resources.EXIT);
            exitMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK, true));
            exitMenuItem.setIcon(SensibleToolkit.createImageIcon(Resources.IMG_EXIT));
            exitMenuItem.addActionListener(this);
        }
        return exitMenuItem;
    }

    /**
     * Method invoked when the exit menu item is pressed.
     */
    private void exitAction() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        EventQueue eventQueue = toolkit.getSystemEventQueue();
        eventQueue.postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This method initializes the 'help' menu.
     *
     * @return the 'help' menu
     */
    private JMenu getHelpMenu() {

        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText(Resources.HELP);
            helpMenu.add(getAboutMenuItem());
        }
        return helpMenu;
    }

    /**
     * This method initializes the 'about' menu item.
     *
     * @return the 'about' menu item
     */
    private JMenuItem getAboutMenuItem() {

        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText(Resources.TIMEZONESHIFTER_ABOUT);
            aboutMenuItem.setIcon(SensibleToolkit.createImageIcon(Resources.IMG_HELP));
            aboutMenuItem.addActionListener(this);
        }
        return aboutMenuItem;
    }

    /**
     * Method invoked when the about menu item is pressed.
     */
    private void aboutAction() {

        JOptionPane.showMessageDialog(
            this,
            Resources.TIMEZONESHIFTER_ABOUT_TEXT,
            Resources.TIMEZONESHIFTER_TITLE,
            JOptionPane.INFORMATION_MESSAGE,
            SensibleToolkit.createImageIcon(Resources.IMG_DEORS));
    }

    /**
     * This method initializes the main content panel.
     *
     * @return the main content panel
     */
    private JPanel getMainContentPanel() {

        if (mainContentPanel == null) {

            mainContentPanel = new JPanel();
            mainContentPanel.setLayout(null);

            mainContentPanel.add(getOkButton(), null);

            mainContentPanel.add(getRootDirLabel(), null);
            mainContentPanel.add(getRootDirField(), null);
            mainContentPanel.add(getTzShiftLabel(), null);
            mainContentPanel.add(getTzShiftField(), null);
            mainContentPanel.add(getApplyToFilesCheck(), null);
            mainContentPanel.add(getApplyToDirectoriesCheck(), null);
            mainContentPanel.add(getRecurseCheck(), null);
            mainContentPanel.add(getFilterCheck(), null);
            mainContentPanel.add(getFilterRegexLabel(), null);
            mainContentPanel.add(getFilterRegexField(), null);

            mainContentPanel.add(getShowConsoleButton(), null);
            mainContentPanel.add(getScrollLockButton(), null);
            mainContentPanel.add(getLogScrollPanel(), null);
        }
        return mainContentPanel;
    }

    /**
     * This method initializes the dialog confirmation button.
     *
     * @return the button
     */
    private JButton getOkButton() {

        final int x = 640;
        final int y = 12;
        final int w = 140;
        final int h = 22;

        if (okButton == null) {
            okButton = new JButton();
            okButton.setBounds(x, y, w, h);
            okButton.setText(Resources.RUN);
            okButton.addActionListener(this);
        }
        return okButton;
    }

    /**
     * Runs the process.
     */
    private void runProcess() {

        if (runningProcess) {
            JOptionPane.showMessageDialog(
                this,
                Resources.WAIT,
                Resources.TIMEZONESHIFTER_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            int ret = JOptionPane.showConfirmDialog(
                this,
                Resources.START,
                Resources.TIMEZONESHIFTER_TITLE,
                JOptionPane.YES_NO_OPTION);

            if (ret == JOptionPane.YES_OPTION) {

                // the log area is cleared
                getLogTextArea().setText(Resources.BLANK);

                worker = new TimeZoneShifterWorker(getTimeZoneShifter());
                worker.addPropertyChangeListener(this);

                worker.getProcess().addActivityListener(this);

                // execute the worker (runs the process)
                worker.execute();

                runningProcess = true;
                enableControls(false);
            }
        }
    }

    /**
     * Enables or disables all window controls.
     *
     * @param flag whether to enable all window controls
     */
    private void enableControls(boolean flag) {

        getRootDirField().setEnabled(flag);
        getTzShiftField().setEnabled(flag);
        getApplyToFilesCheck().setEnabled(flag);
        getApplyToDirectoriesCheck().setEnabled(flag);
        getRecurseCheck().setEnabled(flag);
        getFilterCheck().setEnabled(flag);
        getFilterRegexField().setEnabled(flag);
        getOkButton().setEnabled(flag);
    }

    /**
     * This method initializes the timeZoneShifter bean.
     *
     * @return the timeZoneShifter bean
     */
    private TimeZoneShifterObject getTimeZoneShifter() {

        if (timeZoneShifter == null) {
            timeZoneShifter = new TimeZoneShifterObject();
            timeZoneShifter.addPropertyChangeListener(this);
        }
        return timeZoneShifter;
    }

    /**
     * This method initializes the root directory label control.
     *
     * @return the root directory label
     */
    private JLabel getRootDirLabel() {

        final int x = 15;
        final int y = 15;
        final int w = 150;
        final int h = 16;

        if (rootDirLabel == null) {
            rootDirLabel = new JLabel();
            rootDirLabel.setBounds(x, y, w, h);
            rootDirLabel.setText(Resources.FIELD_ROOTDIR);
        }
        return rootDirLabel;
    }

    /**
     * This method initializes the root directory field control.
     *
     * @return the root directory field
     */
    private SensibleTextField getRootDirField() {

        final int x = 175;
        final int y = 11;
        final int w = 450;
        final int h = 25;

        if (rootDirField == null) {
            rootDirField = new SensibleTextField();
            rootDirField.setBounds(x, y, w, h);
            rootDirField.setData(getTimeZoneShifter().getRootDir());
        }
        return rootDirField;
    }

    /**
     * This method initializes the time zone shift label control.
     *
     * @return the time zone shift label
     */
    private JLabel getTzShiftLabel() {

        final int x = 15;
        final int y = 45;
        final int w = 150;
        final int h = 16;

        if (tzShiftLabel == null) {
            tzShiftLabel = new JLabel();
            tzShiftLabel.setBounds(x, y, w, h);
            tzShiftLabel.setText(Resources.FIELD_TZSHIFT);
        }
        return tzShiftLabel;
    }

    /**
     * This method initializes the time zone shift field control.
     *
     * @return the time zone shift field
     */
    private SensibleTextField getTzShiftField() {

        final int x = 175;
        final int y = 41;
        final int w = 50;
        final int h = 25;

        if (tzShiftField == null) {
            tzShiftField = new SensibleTextField();
            tzShiftField.setBounds(x, y, w, h);
            tzShiftField.setData(getTimeZoneShifter().getTzShift());
        }
        return tzShiftField;
    }

    /**
     * This method initializes the apply to files check control.
     *
     * @return the apply to files check
     */
    private SensibleCheckBox getApplyToFilesCheck() {

        final int x = 10;
        final int y = 72;
        final int w = 155;
        final int h = 22;

        if (applyToFilesCheck == null) {
            applyToFilesCheck = new SensibleCheckBox();
            applyToFilesCheck.setBounds(x, y, w, h);
            applyToFilesCheck.setText(Resources.FIELD_APPLYTOFILES);
            applyToFilesCheck.setData(getTimeZoneShifter().getApplyToFiles());
        }
        return applyToFilesCheck;
    }

    /**
     * This method initializes the apply to directories check control.
     *
     * @return the apply to directories check
     */
    private SensibleCheckBox getApplyToDirectoriesCheck() {

        final int x = 10;
        final int y = 102;
        final int w = 155;
        final int h = 22;

        if (applyToDirectoriesCheck == null) {
            applyToDirectoriesCheck = new SensibleCheckBox();
            applyToDirectoriesCheck.setBounds(x, y, w, h);
            applyToDirectoriesCheck.setText(Resources.FIELD_APPLYTODIRECTORIES);
            applyToDirectoriesCheck.setData(getTimeZoneShifter().getApplyToDirectories());
        }
        return applyToDirectoriesCheck;
    }

    /**
     * This method initializes the recurse subdirectories check control.
     *
     * @return the recurse subdirectories check
     */
    private SensibleCheckBox getRecurseCheck() {

        final int x = 10;
        final int y = 132;
        final int w = 305;
        final int h = 22;

        if (recurseCheck == null) {
            recurseCheck = new SensibleCheckBox();
            recurseCheck.setBounds(x, y, w, h);
            recurseCheck.setText(Resources.FIELD_RECURSE);
            recurseCheck.setData(getTimeZoneShifter().getRecurse());
        }
        return recurseCheck;
    }

    /**
     * This method initializes the filter files check control.
     *
     * @return the filter files check
     */
    private SensibleCheckBox getFilterCheck() {

        final int x = 10;
        final int y = 162;
        final int w = 155;
        final int h = 22;

        if (filterCheck == null) {
            filterCheck = new SensibleCheckBox();
            filterCheck.setBounds(x, y, w, h);
            filterCheck.setText(Resources.FIELD_FILTER);
            filterCheck.setData(getTimeZoneShifter().getFilter());
        }
        return filterCheck;
    }

    /**
     * This method initializes the filter expression label control.
     *
     * @return the filter expression label
     */
    private JLabel getFilterRegexLabel() {

        final int x = 15;
        final int y = 195;
        final int w = 150;
        final int h = 16;

        if (filterRegexLabel == null) {
            filterRegexLabel = new JLabel();
            filterRegexLabel.setBounds(x, y, w, h);
            filterRegexLabel.setText(Resources.FIELD_FILTERREGEX);
        }
        return filterRegexLabel;
    }

    /**
     * This method initializes the filter expression field control.
     *
     * @return the filter expression field
     */
    private SensibleTextField getFilterRegexField() {

        final int x = 175;
        final int y = 191;
        final int w = 450;
        final int h = 25;

        if (filterRegexField == null) {
            filterRegexField = new SensibleTextField();
            filterRegexField.setBounds(x, y, w, h);
            filterRegexField.setData(getTimeZoneShifter().getFilterRegex());
        }
        return filterRegexField;
    }

    /**
     * This method initializes the scroll lock button.
     *
     * @return the scroll lock button
     */
    private JToggleButton getScrollLockButton() {

        final int x = 755;
        final int y = 190;
        final int w = 25;
        final int h = 25;

        if (scrollLockButton == null) {
            scrollLockButton = new JToggleButton();
            scrollLockButton.setBounds(x, y, w, h);
            scrollLockButton.setToolTipText(Resources.PIN);
            scrollLockButton.setIcon(SensibleToolkit.createImageIcon(Resources.IMG_PIN));
            scrollLockButton.addActionListener(this);
        }
        return scrollLockButton;
    }

    /**
     * Method invoked when the scroll lock toggle button is pressed.
     */
    private void scrollLockAction() {

        scrollLocked = getScrollLockButton().isSelected();
    }

    /**
     * This method initializes the show console button.
     *
     * @return the show console button
     */
    private JToggleButton getShowConsoleButton() {

        final int x = 715;
        final int y = 190;
        final int w = 25;
        final int h = 25;

        if (showConsoleButton == null) {
            showConsoleButton = new JToggleButton();
            showConsoleButton.setBounds(x, y, w, h);
            showConsoleButton.setToolTipText(Resources.CONSOLE);
            showConsoleButton.setIcon(SensibleToolkit.createImageIcon(Resources.IMG_CONSOLE));
            showConsoleButton.addActionListener(this);
        }
        return showConsoleButton;
    }

    /**
     * Method invoked when the show console toggle button is pressed.
     */
    private void showConsoleAction() {

        final int h = 215;

        if (getShowConsoleButton().isSelected()) {
            setSize(getWidth(), getHeight() + h);
        } else {
            setSize(getWidth(), getHeight() - h);
        }
    }

    /**
     * This method initializes the log scroll panel.
     *
     * @return the log scroll panel
     */
    private JScrollPane getLogScrollPanel() {

        final int x = 15;
        final int y = 230;
        final int w = 715;
        final int h = 200;

        if (logScrollPanel == null) {
            logScrollPanel = new JScrollPane(getLogTextArea());
            logScrollPanel.setBounds(x, y, w, h);
        }
        return logScrollPanel;
    }

    /**
     * This method initializes the log text area control.
     *
     * @return the log text area control
     */
    JTextArea getLogTextArea() {

        final int w = 715;
        final int h = 200;

        if (logTextArea == null) {
            logTextArea = new JTextArea();
            logTextArea.setSize(w, h);
            logTextArea.setEditable(false);
        }
        return logTextArea;
    }

    /**
     * Handler method for activity log events.
     *
     * @param message the activity message
     */
    public void activityLog(String message) {

        getLogTextArea().append(message);
        getLogTextArea().append(Resources.NEW_LINE);

        if (!scrollLocked) {
            getLogTextArea().setCaretPosition(getLogTextArea().getText().length());
        }
    }

    /**
     * Handler method for action performed events.
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getExitMenuItem()) {
            exitAction();
        } else if (event.getSource() == getAboutMenuItem()) {
            aboutAction();
        } else if (event.getSource() == getOkButton()) {
            runProcess();
        } else if (event.getSource() == getShowConsoleButton()) {
            showConsoleAction();
        } else if (event.getSource() == getScrollLockButton()) {
            scrollLockAction();
        }
    }

    /**
     * Handler for property change events.
     *
     * @param event the property change event
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getSource() == getTimeZoneShifter()) {
            getOkButton().setEnabled(getTimeZoneShifter().isDataComplete());
        } else if (event.getSource() == worker
            && Resources.STATE.equals(event.getPropertyName())
            && StateValue.DONE.equals(event.getNewValue())) {

            runningProcess = false;
            enableControls(true);

            int errors = 0;
            try {
                errors = worker.get();
            } catch (InterruptedException ie) {
                finishedInError(ie);
            } catch (ExecutionException ee) {
                finishedInError(ee);
            }

            if (errors == 0) {
                JOptionPane.showMessageDialog(
                    this,
                    Resources.FINISHED_OK,
                    Resources.TIMEZONESHIFTER_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    StringToolkit.replace(Resources.FINISHED_ERROR, Integer.toString(errors)),
                    Resources.TIMEZONESHIFTER_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Shows a dialog when the process finished in error.
     *
     * @param t the Throwable object captured in the process execution
     */
    private void finishedInError(Throwable t) {

        t.printStackTrace(System.err);

        JOptionPane.showMessageDialog(
            this,
            StringToolkit.replace(Resources.EXCEPTION, t.getMessage()),
            Resources.TIMEZONESHIFTER_TITLE,
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Empty handler for window activated events.
     *
     * @param event the window event
     */
    public void windowActivated(WindowEvent event) {
    }

    /**
     * Empty handler for window closed events.
     *
     * @param event the window event
     */
    public void windowClosed(WindowEvent event) {
    }

    /**
     * Handler for window closing events.
     *
     * @param event the window event
     */
    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public void windowClosing(WindowEvent event) {

        if (runningProcess) {

            int ret = JOptionPane.showConfirmDialog(
                this,
                Resources.CONFIRM_ABORT,
                Resources.TIMEZONESHIFTER_TITLE,
                JOptionPane.YES_NO_OPTION);

            // exit is forced so the process is aborted
            if (ret == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        } else {
            int ret = JOptionPane.showConfirmDialog(
                this,
                Resources.SURE,
                Resources.TIMEZONESHIFTER_TITLE,
                JOptionPane.YES_NO_OPTION);

            if (ret == JOptionPane.YES_OPTION) {
                dispose();
            }
        }
    }

    /**
     * Empty handler for window deactivated events.
     *
     * @param event the window event
     */
    public void windowDeactivated(WindowEvent event) {
    }

    /**
     * Empty handler for window deiconified events.
     *
     * @param event the window event
     */
    public void windowDeiconified(WindowEvent event) {
    }

    /**
     * Empty handler for window iconified events.
     *
     * @param event the window event
     */
    public void windowIconified(WindowEvent event) {
    }

    /**
     * Empty handler for window opened events.
     *
     * @param event the window event
     */
    public void windowOpened(WindowEvent event) {
    }
}
