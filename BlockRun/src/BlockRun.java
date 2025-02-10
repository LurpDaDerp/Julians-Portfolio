import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class BlockRun {

    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static int windowWidth;
    public static int windowHeight;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Block Run");
        GamePanel gamePanel = new GamePanel(frame);

        try {
            // Load the icon image
            Image icon = ImageIO.read(new File("resources/gameicon.png")); // Adjust the path as needed
            
            // Set the window icon (Windows/Linux)
            frame.setIconImage(icon);

            // Use Taskbar API for macOS dock icon (Java 9+)
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(icon); // macOS dock icon
            } catch (UnsupportedOperationException e) {
                System.out.println("Taskbar API is not supported on this system: " + e.getMessage());
            }

            // Change app name in dock (macOS specific)
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                try {
                    Class<?> appClass = Class.forName("com.apple.eawt.Application");
                    Object application = appClass.getMethod("getApplication").invoke(null);
                    appClass.getMethod("setDockMenu", String.class).invoke(application, "Block Run"); // Change dock name here
                } catch (Exception e) {
                    System.out.println("Unable to set macOS dock name: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Icon image not found: " + e.getMessage());
        }

        // Set up the window size
        windowWidth = screenSize.width;
        windowHeight = screenSize.height;

        // Add the game panel and set window properties
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(windowWidth, windowHeight);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start the game
        gamePanel.homeScreen();
    }
}
