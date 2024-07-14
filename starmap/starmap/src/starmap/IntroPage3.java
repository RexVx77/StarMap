package starmap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
class AnimatedBackgroundPanel extends JPanel implements ActionListener {
    private final int STAR_COUNT = 1000;
    private final List<Point> stars = new ArrayList<>();
    private final Timer timer;
    private final Image rocket;
    @SuppressWarnings("unused")
	private int flag;
    private int rocketX = 100; 
    private int rocketY = 100; 
    private Clip clip; 

    private void playMusic() {
        try {
           
            File file = new File("file/videoplayback.wav"); 
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

            
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AnimatedBackgroundPanel(int flag) {
        if(flag==0){
            playMusic();
            flag=1;
        }

        
        initializeStars();

        
        String imagePath ="file/vecteezy_ufo-spaceship-concept-clipart-design-illustration_9356452.png";
        ImageIcon icon = new ImageIcon(imagePath);
        Image originalImage = icon.getImage();
        Image newimg = originalImage.getScaledInstance(1920, 1080, java.awt.Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(newimg);
        rocket = newImageIcon.getImage();


       
        timer = new Timer(40, this);
        timer.start();
        setPreferredSize(new Dimension(800, 600)); 
    }

   
    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Point(new Random().nextInt(1920), new Random().nextInt(1080)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawRocket(g);
        drawText(g);
    }

    private void drawBackground(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        for (Point star : stars) {
            g.fillOval(star.x, star.y, 2, 2);
        }
    }

    private void drawRocket(Graphics g) {
        g.drawImage(rocket, rocketX, rocketY, 250, 250, this);
    }

    private void drawText(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("STARMAP", 20, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        rocketX += 10;
        if (rocketX > getWidth()) {
            rocketX = -50; 
            rocketY = new Random().nextInt(getHeight() - 50); 
        }

       
        for (int i = 0; i < stars.size(); i++) {
            Point star = stars.get(i);
            star.x -= 1; 
            if (star.x < 0) {
                star.x = getWidth();
                star.y = new Random().nextInt(getHeight());
            }
        }

        repaint(); 
    }

    
    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}

@SuppressWarnings("serial")
class CustomComboBoxRenderer extends DefaultListCellRenderer {
    private Color backgroundColor;
    private Color foregroundColor;

    public CustomComboBoxRenderer(Color backgroundColor, Color foregroundColor) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBackground(backgroundColor);
        label.setForeground(foregroundColor);
        return label;
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(backgroundColor);
        super.paintComponent(g);
    }
}

@SuppressWarnings("serial")
public class IntroPage3 extends JFrame {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> locationDropdown;
    private AnimatedBackgroundPanel backgroundPanel;

    public IntroPage3(int flag) {
        super("Animated Intro Page with Moving Stars");

        backgroundPanel = new AnimatedBackgroundPanel(flag);
        backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 200));

        setContentPane(backgroundPanel);

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");

        loginButton.addActionListener(e -> openLoginPage()); 
        createAccountButton.addActionListener(e -> openCreateAccountPage()); 

        backgroundPanel.add(loginButton);
        backgroundPanel.add(createAccountButton);

        pack(); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backgroundPanel.stopMusic();
            }
        });

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(5, 2, 10, 10)); 

       
        loginPanel.setBackground(new Color(0, 0, 0, 100)); 

       
        Color greenColor = new Color(0, 255, 0); 

        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(greenColor); 
        loginPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBackground(new Color(50, 50, 50)); 
        nameField.setForeground(greenColor);
        loginPanel.add(nameField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(greenColor); 
        loginPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBackground(new Color(50, 50, 50)); 
        usernameField.setForeground(greenColor); 
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(greenColor); 
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(50, 50, 50)); 
        passwordField.setForeground(greenColor);
        loginPanel.add(passwordField);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setForeground(greenColor); 
        loginPanel.add(locationLabel);

        locationDropdown = new JComboBox<>(new String[]{"Pune", "Bangalore", "Hyderabad"});
        locationDropdown.setRenderer(new CustomComboBoxRenderer(new Color(50, 50, 50), Color.GREEN));
        locationDropdown.setBackground(new Color(50, 50, 50));
        locationDropdown.setForeground(Color.GREEN);
        loginPanel.add(locationDropdown);

        return loginPanel;
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("Back"); 
        backButton.addActionListener(e -> {
            backgroundPanel.removeAll(); 
            JButton loginButton = new JButton("Login");
            JButton createAccountButton = new JButton("Create Account");
            loginButton.addActionListener(e1 -> openLoginPage());
            createAccountButton.addActionListener(e1 -> openCreateAccountPage()); 
            backgroundPanel.add(loginButton); 
            backgroundPanel.add(createAccountButton); 
            backgroundPanel.revalidate(); 
        });
        return backButton;
    }

    @SuppressWarnings("unused")
	private void displayErrorMessage(JPanel loginPanel) {
        JLabel errorMessage = new JLabel("Login Unsuccessful! Try Again!");
        errorMessage.setForeground(Color.RED); 
        loginPanel.add(errorMessage);
        loginPanel.revalidate(); 
    }

    private void openLoginPage() {
        backgroundPanel.removeAll(); 
        JPanel loginPanel = createLoginPanel();

        JButton backButton = createBackButton();

        
        JButton loginBtn = new JButton("Login"); 
        loginBtn.addActionListener(new ActionListener() {
            @SuppressWarnings("unused")
			@Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                String location = (String) locationDropdown.getSelectedItem();

                
                Object[] result = verifyLogin(username, password);
                boolean loginStatus = (boolean) result[0];
                int userID = (int) result[1];

                if (loginStatus) {
                    System.out.println("Login successful!");
                    SwingUtilities.invokeLater(() -> {
                       
                        JFrame quizFrame = new QuizPage(userID, username);

                        
                        dispose();
                    }); 
                } else {
                    System.out.println("Invalid username or password!");
                    JOptionPane.showMessageDialog(IntroPage3.this, "Login Unsuccessful! Try Again!", "Error", JOptionPane.INFORMATION_MESSAGE);
                  
                }
            }
        });

        loginPanel.add(loginBtn);
        loginPanel.add(backButton);
        backgroundPanel.add(loginPanel, BorderLayout.CENTER);
        backgroundPanel.revalidate(); 
    }


    private void openCreateAccountPage() {
        backgroundPanel.removeAll(); 
        JPanel createAccountPanel = createLoginPanel();

        JButton backButton = createBackButton();

       
        JButton createAccountBtn = new JButton("Create Account"); 
        createAccountBtn.addActionListener(e -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            String location = (String) locationDropdown.getSelectedItem();

           
            if (createAccount(name, username, password, location)) {
                System.out.println("Account created successfully!");
               
            } else {
                System.out.println("Failed to create account!");
          
            }
        });

        createAccountPanel.add(createAccountBtn);
        createAccountPanel.add(backButton);
        backgroundPanel.add(createAccountPanel, BorderLayout.CENTER);
        backgroundPanel.revalidate();
    }

    private Object[] verifyLogin(String username, String password) {
        try {
            
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");

          
            String query = "SELECT U_ID FROM user WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

    
            ResultSet resultSet = statement.executeQuery();

            
            if (resultSet.next()) {
                int userID = resultSet.getInt("U_ID");
               
                return new Object[]{true, userID};
            } else {
               
                return new Object[]{false, null};
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return new Object[]{false, null};
        }
    }

    private boolean createAccount(String name, String username, String password, String location) {
        try {
          
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");

   
            String sql = "SELECT COUNT(*) AS row_count FROM user";
            int rowCount = 0;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

           
                if (rs.next()) {
                    rowCount = rs.getInt("row_count");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            String query = "INSERT INTO user (U_ID, username, password, Score) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, rowCount + 1);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setInt(4, 0);
          
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
         
        SwingUtilities.invokeLater(() -> new IntroPage3(0));
    }
}
