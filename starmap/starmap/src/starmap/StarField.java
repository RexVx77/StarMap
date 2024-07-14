package starmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.*;

@SuppressWarnings("serial")
class Star extends Point {
    int thick;
    String Name;
    double CI;
    BigDecimal dist;
    int S_ID;

    public Star(int sid,String Nx, int sx, int sy, int tx, double cx, BigDecimal dx) {
        super(sx, sy);
        S_ID = sid;
        thick = tx;
        Name = Nx;
        CI = cx;
        dist = dx;
    }

    public Star(Point p, int tx) {
        super(p);
        thick = tx;
    }

    public static int getRandomThick() {
        Random r = new Random();
        return r.nextInt(5) + 1;
    }
}

@SuppressWarnings("serial")
public class StarField extends JFrame {

    private List<Star> stars = new ArrayList<>();
    private JDialog dialog;
    int length = 1920;
    int width = 1280;
    int U_ID;
    String username;
    private Clip clip;

    public StarField(int U_ID, String username) {
        this.U_ID = U_ID;
        this.username = username;
        setTitle("Star Field");
        setSize(length, width);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        DatFile d = new DatFile();
        stars = d.stars;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStars(g);
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stars.add(new Star(e.getPoint(), Star.getRandomThick()));
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = e.getPoint();
                for (Star star : stars) {
                    if (isMouseOverStar(star, mousePoint)) {
                        showStarInfoDialog(star, mousePoint);
                        return;
                    }
                }
                hideStarInfoDialog();
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                    }
                    JFrame mainMenuFrame = new MainMenu(username, U_ID);
                    mainMenuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    mainMenuFrame.setVisible(true);
                    dispose();
                });
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
//        playMusic(); // Start playing background music
    }

    @SuppressWarnings("unused")
	private void playMusic() {
        try {
            File file = new File("file/videoplayback.wav"); // Replace this with the path to your audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawStars(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (Star star : stars) {
            g.setColor(getStarColor(star.CI));
            g.fillOval(star.x, star.y, star.thick, star.thick);
        }
    }

    private Color getStarColor(double CI) {
        if (CI > 1.4) {
            return new Color(255, 196, 197);
        } else if (CI < 1.4 && CI > 0.8) {
            return new Color(255, 212, 181);
        } else if (CI < 0.8 && CI > 0.6) {
            return new Color(255, 255, 153);
        } else if (CI < 0.6 && CI > 0.3) {
            return new Color(211, 255, 209);
        } else if (CI < 0.3 && CI > 0) {
            return Color.WHITE;
        } else {
            return new Color(205, 255, 255);
        }
    }

    private boolean isMouseOverStar(Star star, Point mousePoint) {
        int k = 3;
        double distance = Math.sqrt(Math.pow(star.x - mousePoint.x, 2) + Math.pow(star.y - mousePoint.y, 2));
        if (star.Name != null && !star.Name.isEmpty()) {
            k = 20;
        }
        return distance <= k;
    }

    private void showStarInfoDialog(Star star, Point mousePoint) {
        if (dialog == null) {
            dialog = new JDialog();
            dialog.setTitle("Star Information");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(300, 200);
            dialog.getContentPane().setBackground(Color.BLACK);
            dialog.setUndecorated(true);
            dialog.setOpacity(0.7f);
            dialog.setLocation((int) (getLocation().getX() + star.x + 10), (int) (getLocation().getY() + star.y + 10));
            dialog.setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(Color.BLACK);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JLabel headerLabel = new JLabel("Star Information");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(Color.BLACK);
            infoPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            String starName = (star.Name != null && !star.Name.isEmpty()) ? star.Name : "Unnamed";
            float distp = star.dist.floatValue();
            String infoText = "Name: " + starName + "\n" + "Distance of Star: " + distp + " Parsecs";
            JTextArea label = new JTextArea(infoText);
            label.setOpaque(false);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setAlignmentY(Component.TOP_ALIGNMENT);
            infoPanel.add(label, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.BLACK);
            JButton addToFavoritesButton = new JButton("Add to Favorites");
            addToFavoritesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addStarToFavorites(star);
                }
            });
            buttonPanel.add(addToFavoritesButton);

            if (star.Name != null && !star.Name.isEmpty()) {
                JButton achievementButton = new JButton("Get this Achievement");
                achievementButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleAchievement(star);
                    }
                });
                buttonPanel.add(achievementButton);
            }

            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(infoPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } else {
            dialog.setLocation((int) (getLocation().getX() + star.x + 10), (int) (getLocation().getY() + star.y + 10));
        }
    }

    @SuppressWarnings("unused")
	private void addStarToFavorites(Star star) {
        String url = "jdbc:mysql://localhost:3306/stars500";
        String username = "root";
        String password = "1104";
        String tableName = "favstar";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sqlQuery = "INSERT INTO favstar (S_ID,U_ID) VALUES ("+ Integer.toString(star.S_ID)+  "," +Integer.toString(U_ID)+");";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            int rowsinserted = statement.executeUpdate();
            if(rowsinserted>0) {
                System.out.println("YES");
            } else {
                System.out.println("NOOOO");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void handleAchievement(Star star) {
        String url = "jdbc:mysql://localhost:3306/stars500";
        String username = "root";
        String password = "1104";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String fetchAchievementQuery = "SELECT A_ID, ACH_SCORE FROM achievement WHERE ACH_NAME = ?";
            try (PreparedStatement fetchStatement = connection.prepareStatement(fetchAchievementQuery)) {
                fetchStatement.setString(1, star.Name);
                try (ResultSet resultSet = fetchStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int A_ID = resultSet.getInt("A_ID");
                        int achScore = resultSet.getInt("ACH_SCORE");
                        String updateQuery = "UPDATE usrach SET IF_GOT = ? WHERE U_ID = ? AND A_ID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setBoolean(1, true);
                            updateStatement.setInt(2, U_ID);
                            updateStatement.setInt(3, A_ID);
                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(null, "Achievement obtained successfully.", "Achievement Success", JOptionPane.INFORMATION_MESSAGE);
                                String updateScoreQuery = "UPDATE user SET Score = Score + ? WHERE U_ID = ?";
                                try (PreparedStatement updateScoreStatement = connection.prepareStatement(updateScoreQuery)) {
                                    updateScoreStatement.setInt(1, achScore);
                                    updateScoreStatement.setInt(2, U_ID);
                                    int scoreUpdated = updateScoreStatement.executeUpdate();
                                    if (scoreUpdated > 0) {
                                        JOptionPane.showMessageDialog(null, "User score updated successfully.", "Score Update", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Failed to update user score.", "Score Update Failed", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to update achievement status.", "Achievement Failed", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Achievement not found.", "Achievement Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the achievement.", "Achievement Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hideStarInfoDialog() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StarField starField = new StarField(2, "dfg");
            starField.setVisible(true);
        });
    }
}

