package starmap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("serial")
class AnimatedBackgroundPanel1 extends JPanel implements ActionListener {
    private final int STAR_COUNT = 1000;
    private final List<Point> stars = new ArrayList<>();
    private final Timer timer;
    private final Image rocket;
    private int rocketX = 100;
    private int rocketY = 100;
    String Question_String = null;
    private String Correct_A = null;
    String OptA = null;
    String OptB = null;
    String OptC = null;
    String OptD = null;
    private String Answer_clicked = null;
    @SuppressWarnings("unused")
	private void playMusic() {
        try {
            File file = new File("/Users/shaurya_mac/dbs_final/src/checker/videoplayback.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AnimatedBackgroundPanel1(int userID, String username) {
        initializeStars();
        String imagePath = "file/vecteezy_ufo-spaceship-concept-clipart-design-illustration_9356452.png";
        ImageIcon icon = new ImageIcon(imagePath);
        Image originalImage = icon.getImage();
        Image newimg = originalImage.getScaledInstance(300, 250, Image.SCALE_SMOOTH);
        rocket = new ImageIcon(newimg).getImage();
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
        updateRocketPosition();
        moveStars();
        repaint();
    }

    private void updateRocketPosition() {
        rocketX += 10;
        if (rocketX > getWidth()) {
            rocketX = -50;
            rocketY = new Random().nextInt(Math.max(getHeight() - 50, 100));
        }
    }

    private void moveStars() {
        for (int i = 0; i < stars.size(); i++) {
            Point star = stars.get(i);
            star.x -= 1;
            if (star.x < 0) {
                star.x = getWidth();
                star.y = new Random().nextInt(getHeight());
            }
        }
    }

    public void setA(int U_ID, String username) {
        Answer_clicked = "A";
        checkAnswer(U_ID,username);
    }

    public void setB(int U_ID, String username) {
        Answer_clicked = "B";
        checkAnswer(U_ID,username);
    }

    public void setC(int U_ID, String username) {
        Answer_clicked = "C";
        checkAnswer(U_ID,username);
    }

    public void setD(int U_ID, String username) {
        Answer_clicked = "D";
        checkAnswer(U_ID,username);
    }

    private void checkAnswer(int U_ID, String username) {
        if (Answer_clicked != null && Correct_A != null) {
            if (Answer_clicked.equals(Correct_A)) {
                JOptionPane.showMessageDialog(this, "Correct Answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect Answer!", "Result", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an answer!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        SwingUtilities.getWindowAncestor(this).dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenu starField = new MainMenu(username, U_ID);
            starField.setVisible(true);
            starField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            starField.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }

    public void setQuestion(String question, String correctAnswer, String optA, String optB, String optC, String optD) {
        Question_String = question;
        Correct_A = correctAnswer;
        OptA = optA;
        OptB = optB;
        OptC = optC;
        OptD = optD;
    }
}

@SuppressWarnings("serial")
public class QuizPage extends JFrame {
    private final AnimatedBackgroundPanel1 backgroundPanel;

    public QuizPage(int U_ID, String username) {
        super("Animated Intro Page with Moving Stars");
        setSize(getMaximumSize());
        backgroundPanel = new AnimatedBackgroundPanel1(U_ID, username);
        backgroundPanel.setSize(getMaximumSize());
        backgroundPanel.setLayout(new GridLayout(7, 5));

        String query = "SELECT * FROM quiz ORDER BY RAND() LIMIT 15";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/stars500", "root", "1104");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                backgroundPanel.setQuestion(
                        resultSet.getString("Q_STR"),
                        resultSet.getString("C_ANS"),
                        resultSet.getString("OA"),
                        resultSet.getString("OB"),
                        resultSet.getString("OC"),
                        resultSet.getString("OD")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel label = new JLabel("<html>"+ backgroundPanel.Question_String+ "</html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 48));

        Color bleh = new Color(45, 45, 45);
        JButton OA = new JButton(backgroundPanel.OptA);
        OA.setOpaque(false);
        OA.setContentAreaFilled(false);
        OA.setBorderPainted(false);
        OA.setForeground(Color.green);
        OA.setFont(new Font("Arial", Font.BOLD, 24));
        OA.addActionListener(e -> backgroundPanel.setA(U_ID,username));
        OA.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (OA.getBackground() != Color.YELLOW) {
                    OA.setOpaque(true);
                    OA.setBackground(bleh);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (OA.getBackground() == bleh) {
                    OA.setOpaque(false);
                }
            }

            public void mouseClicked(MouseEvent e) {
                OA.setOpaque(true);
            }
        });

        JButton OB = new JButton(backgroundPanel.OptB);
        OB.setOpaque(false);
        OB.setContentAreaFilled(false);
        OB.setBorderPainted(false);
        OB.setForeground(Color.green);
        OB.setFont(new Font("Arial", Font.BOLD, 24));
        OB.addActionListener(e -> backgroundPanel.setB(U_ID,username));
        OB.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (OB.getBackground() != Color.YELLOW) {
                    OB.setOpaque(true);
                    OB.setBackground(bleh);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (OB.getBackground() == bleh) {
                    OB.setOpaque(false);
                }
            }

            public void mouseClicked(MouseEvent e) {
                OB.setOpaque(true);
            }
        });

        JButton OC = new JButton(backgroundPanel.OptC);
        OC.setOpaque(false);
        OC.setContentAreaFilled(false);
        OC.setBorderPainted(false);
        OC.setForeground(Color.green);
        OC.setFont(new Font("Arial", Font.BOLD, 24));
        OC.addActionListener(e -> backgroundPanel.setC(U_ID,username));
        OC.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (OC.getBackground() != Color.YELLOW) {
                    OC.setOpaque(true);
                    OC.setBackground(bleh);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (OC.getBackground() == bleh) {
                    OC.setOpaque(false);
                }
            }

            public void mouseClicked(MouseEvent e) {
                OC.setOpaque(true);
            }
        });

        JButton OD = new JButton(backgroundPanel.OptD);
        OD.setOpaque(false);
        OD.setContentAreaFilled(false);
        OD.setBorderPainted(false);
        OD.setForeground(Color.green);
        OD.setFont(new Font("Arial", Font.BOLD, 24));
        OD.addActionListener(e -> backgroundPanel.setD(U_ID,username));
        OD.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (OD.getBackground() != Color.YELLOW) {
                    OD.setOpaque(true);
                    OD.setBackground(bleh);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (OD.getBackground() == bleh) {
                    OD.setOpaque(false);
                }
            }

            public void mouseClicked(MouseEvent e) {
                OD.setOpaque(true);
            }
        });

        backgroundPanel.add(label);
        backgroundPanel.add(new JLabel());
        backgroundPanel.add(OA);
        backgroundPanel.add(OB);
        backgroundPanel.add(OC);
        backgroundPanel.add(OD);

        add(backgroundPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}

