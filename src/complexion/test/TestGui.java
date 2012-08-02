package complexion.test;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

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

    final DialogLayout loginPanel;
    final Button btnLogin;
    
    boolean quit;

    public TestGui() {

        loginPanel = new DialogLayout();
        loginPanel.setTheme("login-panel");
        loginPanel.setInnerSize(200, 200);
        
        loginPanel.setHorizontalGroup(loginPanel.createParallelGroup());
        loginPanel.setVerticalGroup(loginPanel.createSequentialGroup());
        
        btnLogin = new Button("LOGIN");
        btnLogin.addCallback(new Runnable() {
            public void run() {
                System.out.println("Button clicked!");
            }
        });
        
        add(btnLogin);
        add(loginPanel);
    }

    @Override
    protected void layout() {
    	btnLogin.adjustSize();
    	btnLogin.setPosition(10, 10);
    	
        // login panel is centered
        loginPanel.setPosition(
                getInnerX() + (getInnerWidth() - loginPanel.getWidth())/2,
                getInnerY() + (getInnerHeight() - loginPanel.getHeight())/2);
        loginPanel.setMinSize(200, 200);
        
    }
}
