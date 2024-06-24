import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame(user -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        }));
    }
}
