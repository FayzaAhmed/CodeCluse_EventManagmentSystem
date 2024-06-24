import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    private LoginCallback loginCallback;

    public LoginFrame(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
        userDAO = new UserDAO();

        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 160, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(180, 80, 100, 25);
        panel.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                dispose();
                loginCallback.onLoginSuccess(user);
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.");
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Please enter both username and password.");
                return;
            }
            if (userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Username already exists. Please choose a different one.");
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userDAO.createUser(user);
            JOptionPane.showMessageDialog(LoginFrame.this, "Registration successful. You can now login.");
        });

        setVisible(true);
    }

}
