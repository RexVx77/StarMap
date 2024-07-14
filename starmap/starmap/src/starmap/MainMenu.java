package starmap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
class AnimatedBackgroundPanel3 extends JPanel implements ActionListener {
    private final int STAR_COUNT = 1000;
    private final List<Point> stars = new ArrayList<>();
    private final Timer timer;
    private final Image rocket;
    private final Image profilePicture; 
    private int rocketX = 100; 
    private int rocketY = 100; 
    private final String username;
    private final int U_ID;
    private boolean isUsernameClicked = false;
    @SuppressWarnings("unused")
	private void playMusic() {
        try {
            File file = new File("file/videoplayback.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AnimatedBackgroundPanel3(String username, int U_ID) {
        this.username = username;
        this.U_ID = U_ID;
        initializeStars();
        setLayout(null);
        String rocketImagePath = "file/vecteezy_ufo-spaceship-concept-clipart-design-illustration_9356452.png";
        ImageIcon rocketIcon = new ImageIcon(rocketImagePath);
        Image originalRocketImage = rocketIcon.getImage();
        Image newRocketImage = originalRocketImage.getScaledInstance(300, 250, Image.SCALE_SMOOTH);
        rocket = new ImageIcon(newRocketImage).getImage();
        String profileImagePath = "file/vecteezy_astronaut-3d-profession-avatars-illustrations_28029007.png";
        ImageIcon profileIcon = new ImageIcon(profileImagePath);
        Image originalProfileImage = profileIcon.getImage();
        Image newProfileImage = originalProfileImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        profilePicture = new ImageIcon(newProfileImage).getImage();
        timer = new Timer(40, this);
        timer.start();
        setPreferredSize(new Dimension(800, 600));

        JButton startButton = new JButton("<html><span style='color:green;'>START STARGAZING</span></html>");
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setBounds(900, 800, 200, 50);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JFrame FieldFrame = new StarField(U_ID, username);
                    FieldFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    FieldFrame.setVisible(true);
                    closeParentWindow();
                });
            }
        });
        add(startButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int profileX = 1500;
                int profileY = 20;
                FontMetrics metrics = getFontMetrics(new Font("SansSerif", Font.BOLD, 18));
                int textWidth = metrics.stringWidth("codesmith")+20;
                int textX = profileX + (profilePicture.getWidth(AnimatedBackgroundPanel3.this) - textWidth) / 2;
                int textY = profileY + profilePicture.getHeight(AnimatedBackgroundPanel3.this) + 20;
                Rectangle usernameBounds = new Rectangle(textX, textY - metrics.getAscent(), textWidth+100, metrics.getHeight()+100);
                if (usernameBounds.contains(e.getPoint())) {
                    isUsernameClicked = true;
                    repaint();
                    Window window = SwingUtilities.getWindowAncestor(AnimatedBackgroundPanel3.this);
                    if (window != null) {
                        window.setVisible(false);
                    }
                    SwingUtilities.invokeLater(() -> {
                        JFrame userPageFrame = new UserPage(U_ID,username);
                        userPageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        userPageFrame.setVisible(true);
                    });
                }
            }
        });
    }

    private void initializeStars() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = (int) screenSize.getWidth();
        int maxHeight = (int) screenSize.getHeight();

        stars.clear();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Point(new Random().nextInt(maxWidth), new Random().nextInt(maxHeight)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawRocket(g);
        drawProfilePicture(g);
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
        g.drawImage(rocket, rocketX, rocketY, this);
    }

    @SuppressWarnings("unused")
	private void drawProfilePicture(Graphics g) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = (int) screenSize.getWidth();
        int maxHeight = (int) screenSize.getHeight();
        int profileX = maxWidth - 200;
        int profileY = 20;
        g.drawImage(profilePicture, profileX, profileY, this);
        g.setColor(isUsernameClicked ? Color.RED : Color.GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(username);
        int textX = profileX + (profilePicture.getWidth(this) - textWidth) / 2;
        int textY = profileY + profilePicture.getHeight(this) + 20;
        g.drawString(username, textX, textY);
        g.setColor(Color.GREEN);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textWidth = metrics.stringWidth("USER ID: " + U_ID);
        textX = profileX + (profilePicture.getWidth(this) - textWidth) / 2;
        textY = textY + metrics.getHeight() + 5;
        g.drawString("USER ID: " + U_ID, textX+25, textY);
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
            if (star.x < 0)

 {
                star.x = getWidth();
                star.y = new Random().nextInt(getHeight());
            }
        }

        repaint();
    }

    public void closeParentWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}

@SuppressWarnings("serial")
public class MainMenu extends JFrame {
    public MainMenu(String username, int U_ID) {
        super("Animated Intro Page with Moving Stars");

        AnimatedBackgroundPanel3 backgroundPanel = new AnimatedBackgroundPanel3(username, U_ID);
        backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 200));

        setContentPane(backgroundPanel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        String username = "YourUsername";
        int U_ID = 3;
        SwingUtilities.invokeLater(() -> new MainMenu(username, U_ID));
    }
}

