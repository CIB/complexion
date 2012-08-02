package complexion.test;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.EditField.Callback;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * A simple login panel
 * 
 * @author Matthias Mann
 */
public class TestGui extends Widget {
    
    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            Display.setTitle("TWL Login Panel Demo");
            Display.setVSyncEnabled(true);

            TestGui demo = new TestGui();
            
            LWJGLRenderer renderer = new LWJGLRenderer();
            GUI gui = new GUI(demo, renderer);

            demo.efName.requestKeyboardFocus();
            
            ThemeManager theme = ThemeManager.createThemeManager(
            		TestGui.class.getResource("login.xml"), renderer);
            gui.applyTheme(theme);

            while(!Display.isCloseRequested() && !demo.quit) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                gui.update();
                Display.update();
            }

            gui.destroy();
            theme.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Display.destroy();
    }

    final FPSCounter fpsCounter;
    final DialogLayout loginPanel;
    final EditField efName;
    final EditField efPassword;
    final Button btnLogin;
    
    boolean quit;

    public TestGui() {
        fpsCounter = new FPSCounter();
        
        loginPanel = new DialogLayout();
        loginPanel.setTheme("login-panel");
        
        efName = new EditField();
        efName.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    efPassword.requestKeyboardFocus();
                }
            }
        });
        
        efPassword = new EditField();
        efPassword.setPasswordMasking(true);
        efPassword.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    emulateLogin();
                }
            }
        });
        
        Label lName = new Label("Name");
        lName.setLabelFor(efName);
        
        Label lPassword = new Label("Password");
        lPassword.setLabelFor(efPassword);
        
        btnLogin = new Button("LOGIN");
        btnLogin.addCallback(new Runnable() {
            public void run() {
                emulateLogin();
            }
        });
        
        DialogLayout.Group hLabels = loginPanel.createParallelGroup(lName, lPassword);
        DialogLayout.Group hFields = loginPanel.createParallelGroup(efName, efPassword);
        DialogLayout.Group hBtn = loginPanel.createSequentialGroup()
                .addGap()   // right align the button by using a variable gap
                .addWidget(btnLogin);
        
        loginPanel.setHorizontalGroup(loginPanel.createParallelGroup()
                .addGroup(loginPanel.createSequentialGroup(hLabels, hFields))
                .addGroup(hBtn));
        loginPanel.setVerticalGroup(loginPanel.createSequentialGroup()
                .addGroup(loginPanel.createParallelGroup(lName, efName))
                .addGroup(loginPanel.createParallelGroup(lPassword, efPassword))
                .addWidget(btnLogin));
        
        add(fpsCounter);
        add(loginPanel);
    }

    @Override
    protected void layout() {
        // fpsCounter is bottom right
        fpsCounter.adjustSize();
        fpsCounter.setPosition(
                getInnerRight() - fpsCounter.getWidth(),
                getInnerBottom() - fpsCounter.getHeight());
        
        // login panel is centered
        loginPanel.adjustSize();
        loginPanel.setPosition(
                getInnerX() + (getInnerWidth() - loginPanel.getWidth())/2,
                getInnerY() + (getInnerHeight() - loginPanel.getHeight())/2);
    }
    
    void emulateLogin() {
        GUI gui = getGUI();
        if(gui != null) {
            // step 1: disable all controls
            efName.setEnabled(false);
            efPassword.setEnabled(false);
            btnLogin.setEnabled(false);
            
            // step 2: get name & password from UI
            String name = efName.getText();
            String pasword = efPassword.getText();
            System.out.println("Name: " + name + " with a " + pasword.length() + " character password");
            
            // step 3: start a timer to simulate the process of talking to a remote server
            Timer timer = gui.createTimer();
            timer.setCallback(new Runnable() {
                public void run() {
                    // once the timer fired re-enable the controls and clear the password
                    efName.setEnabled(true);
                    efPassword.setEnabled(true);
                    efPassword.setText("");
                    efPassword.requestKeyboardFocus();
                    btnLogin.setEnabled(true);
                }
            });
            timer.setDelay(2500);
            timer.start();
            
            /* NOTE: in a real app you would need to keep a reference to the timer object
             * to cancel it if the user closes the dialog which uses the timer.
             * @see Widget#beforeRemoveFromGUI(de.matthiasmann.twl.GUI)
             */
        }
    }
}
