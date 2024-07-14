package starmap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
class AnimatedBackgroundPanel2 extends JPanel implements ActionListener {
    private final int STAR_COUNT = 1000;
    private final List<Point> stars = new ArrayList<>();
    private final Timer timer;
    private final Image rocket;
    private int rocketX = 100;
    private int rocketY = 100;

    private JButton backButton;
    private JButton signOutButton;
    @SuppressWarnings("unused")
	private JButton achievementsButton;

    private JTable leaderboardTable;
    private JTable favoritesTable;
    private JTable achievementsTable;

    private int userID;
    @SuppressWarnings("unused")
	private String username;
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

    public AnimatedBackgroundPanel2(int userID, String username) {
        playMusic();
        initializeStars();
        String imagePath = "file/vecteezy_ufo-spaceship-concept-clipart-design-illustration_9356452.png";
        ImageIcon icon = new ImageIcon(imagePath);
        Image originalImage = icon.getImage();
        Image newimg = originalImage.getScaledInstance(1920, 1080, java.awt.Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(newimg);
        rocket = newImageIcon.getImage();

        timer = new Timer(40, this);
        timer.start();
        setPreferredSize(new Dimension(800, 600));

        setLayout(null);

        backButton = new JButton("Back");
        backButton.setForeground(Color.GREEN);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(AnimatedBackgroundPanel2.this);
                if (window != null) {
                    window.setVisible(false);
                }
                SwingUtilities.invokeLater(() -> {
                    JFrame MainMenuFrame = new MainMenu(username, userID);
                    MainMenuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    MainMenuFrame.setVisible(true);
                });
            }
        });
        backButton.setBounds(960, 800, 100, 30);
        add(backButton);

        signOutButton = new JButton("Sign Out");
        signOutButton.setForeground(Color.RED);
        signOutButton.setContentAreaFilled(false);
        signOutButton.setBorderPainted(false);
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(AnimatedBackgroundPanel2.this);
                if (window != null) {
                    window.setVisible(false);
                }
                SwingUtilities.invokeLater(() -> {
                    JFrame loginFrame = new IntroPage3(1);
                    loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    loginFrame.setVisible(true);
                });
            }
        });
        signOutButton.setBounds(850, 800, 100, 30);
        add(signOutButton);

        setLayout(null);

        JLabel leaderboardLabel = new JLabel("LEADERBOARD");
        leaderboardLabel.setForeground(Color.GREEN);
        leaderboardLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        leaderboardLabel.setBounds(880, 325, 150, 30);
        add(leaderboardLabel);

        JLabel favoritesLabel = new JLabel("FAVORITES");
        favoritesLabel.setForeground(Color.GREEN);
        favoritesLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        favoritesLabel.setBounds(1380, 325, 150, 30);
        add(favoritesLabel);

        JLabel achievementsLabel = new JLabel("ACHIEVEMENTS");
        achievementsLabel.setForeground(Color.GREEN);
        achievementsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        achievementsLabel.setBounds(380, 325, 150, 30);
        add(achievementsLabel);

        this.userID = userID;
        this.username = username;
        fetchAndDisplayLeaderboard();
        fetchAndDisplayFavorites();
        fetchAndDisplayAchievements();
    }

    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Point(new Random().nextInt(1920), new Random().nextInt(1080)));
        }
    }

    private void fetchAndDisplayLeaderboard() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
            Statement statement = connection.createStatement();
            String leaderboardQuery = "SELECT username, score FROM user ORDER BY score DESC";
            ResultSet leaderboardResult = statement.executeQuery(leaderboardQuery);
            String[] leaderboardColumns = {"Username", "Score"};
            DefaultTableModel leaderboardModel = new DefaultTableModel(leaderboardColumns, 0);
            while (leaderboardResult.next()) {
                String username = leaderboardResult.getString("username");
                int score = leaderboardResult.getInt("score");
                Object[] rowData = {username, score};
                leaderboardModel.addRow(rowData);
            }
            leaderboardTable = new JTable(leaderboardModel);
            customizeTable(leaderboardTable);
            JScrollPane leaderboardScrollPane = new JScrollPane(leaderboardTable);
            leaderboardScrollPane.setOpaque(false);
            leaderboardScrollPane.getViewport().setOpaque(false);
            leaderboardScrollPane.setBorder(null);
            leaderboardScrollPane.setBounds(750, 350, 400, 200);
            add(leaderboardScrollPane);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayFavorites() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
            Statement statement = connection.createStatement();
            String favoritesQuery = "SELECT favstar.S_ID, star.C_NAME FROM favstar INNER JOIN star ON favstar.S_ID = star.S_ID WHERE favstar.U_ID = " + userID;
            ResultSet favoritesResult = statement.executeQuery(favoritesQuery);
            String[] favoritesColumns = {"Star ID", "Star Name"};
            DefaultTableModel favoritesModel = new DefaultTableModel(favoritesColumns, 0);
            while (favoritesResult.next()) {
                int starID = favoritesResult.getInt("S_ID");
                String starName = favoritesResult.getString("C_NAME");
                Object[] rowData = {starID, starName};
                favoritesModel.addRow(rowData);
            }
            favoritesTable = new JTable(favoritesModel);
            customizeTable(favoritesTable);
            JScrollPane favoritesScrollPane = new JScrollPane(favoritesTable);
            favoritesScrollPane.setOpaque(false);
            favoritesScrollPane.getViewport().setOpaque(false);
            favoritesScrollPane.setBounds(1250, 350, 400, 200);
            add(favoritesScrollPane);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayAchievements() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
            Statement statement = connection.createStatement();
            String achievementsQuery = "SELECT usrach.A_ID, achievement.ACH_Name FROM usrach INNER JOIN achievement ON usrach.A_ID = achievement.A_ID WHERE usrach.U_ID = " + userID + " AND usrach.IF_GOT = true";
            ResultSet achievementsResult = statement.executeQuery(achievementsQuery);
            String[] achievementsColumns = {"Achievement ID", "Achievement Description"};
            DefaultTableModel achievementsModel = new DefaultTableModel(achievementsColumns, 0);
            while (achievementsResult.next()) {
                int achievementID = achievementsResult.getInt("A_ID");
                String achievementName = achievementsResult.getString("ACH_DESC");
                Object[] rowData = {achievementID, achievementName};
                achievementsModel.addRow(rowData);
            }
            achievementsTable = new JTable(achievementsModel);
            customizeTable(achievementsTable);
            JScrollPane achievementsScrollPane = new JScrollPane(achievementsTable);
            achievementsScrollPane.setOpaque(false);
            achievementsScrollPane.getViewport().setOpaque(false);
            achievementsScrollPane.setBounds(250, 350, 400, 200);
            add(achievementsScrollPane);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void customizeTable(JTable table) {
        table.setOpaque(false);
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(false);
        table.setForeground(Color.GREEN);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JComponent) c).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                c.setBackground(new Color(0, 0, 0, 0));
                c.setForeground(Color.GREEN);
                return c;
            }
        });
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setOpaque(false);
        table.getTableHeader().setBackground(new Color(0, 0, 0, 0));
        table.getTableHeader().setForeground(Color.GREEN);
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
}

@SuppressWarnings("serial")
public class UserPage extends JFrame {
    public UserPage(int userID, String username) {
        super("Animated Intro Page with Moving Stars");
        AnimatedBackgroundPanel2 backgroundPanel = new AnimatedBackgroundPanel2(userID, username);
        setContentPane(backgroundPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserPage(123, "example_username"));
    }
}

