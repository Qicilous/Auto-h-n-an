import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

// ================= AUTO HỒN ĐAN =================
class AutoHonDan {
    private boolean running = false;
    private Robot robot;

    private int delayBetweenClicks;  // delay giữa các click
    private int startDelay;          // delay trước khi bắt đầu
    private int repeat;              // số lần lặp trước khi bấm số
    private int keyToPress;          // phím số cần bấm (KeyEvent.VK_1 ... VK_9)

    public AutoHonDan(int delayBetweenClicks, int startDelay, int repeat, int keyToPress) {
        this.delayBetweenClicks = delayBetweenClicks;
        this.startDelay = startDelay;
        this.repeat = repeat;
        this.keyToPress = keyToPress;
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                Thread.sleep(startDelay * 1000); // chờ trước khi bắt đầu

                while (running) {
                    int count = 0;
                    while (running && (repeat <= 0 || count < repeat)) {
                        // click CHUỘT PHẢI
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

                        System.out.println("Click chuột phải #" + (count+1));

                        count++;
                        Thread.sleep(delayBetweenClicks);
                    }

                    if (!running) break;

                    // Sau khi đủ repeat thì bấm phím số
                    if (keyToPress != -1) {
                        robot.keyPress(keyToPress);
                        robot.keyRelease(keyToPress);
                        System.out.println("Bấm phím: " + KeyEvent.getKeyText(keyToPress));
                    }

                    // vòng lặp lại từ đầu
                }

                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}

// ================= MAIN UI =================
 class MainUI extends JFrame {
    private AutoHonDan autoHonDan;

    public MainUI() {
        setTitle("Auto Tool - Hồn Đan");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // giữa màn hình
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel delayLabel = new JLabel("Delay giữa các click (ms):");
        JTextField delayField = new JTextField("1000");

        JLabel startDelayLabel = new JLabel("Delay trước khi bắt đầu (s):");
        JTextField startDelayField = new JTextField("3");

        JLabel repeatLabel = new JLabel("Repeat (số lần click trước khi bấm số):");
        JTextField repeatField = new JTextField("10");

        JLabel keyLabel = new JLabel("Phím số sau khi lặp (0-9, bỏ trống = không):");
        JTextField keyField = new JTextField("1");

        JButton toggleBtn = new JButton("Start");
        JLabel statusLabel = new JLabel("Trạng thái: Đang dừng");

        // Thêm vào UI
        gbc.gridx = 0; gbc.gridy = 0; add(delayLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(delayField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(startDelayLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(startDelayField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(repeatLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(repeatField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(keyLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(keyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(toggleBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(statusLabel, gbc);

        // ====== Sự kiện ======
        toggleBtn.addActionListener(e -> {
            if (autoHonDan == null || !autoHonDan.isRunning()) {
                int delay = Integer.parseInt(delayField.getText());
                int startDelay = Integer.parseInt(startDelayField.getText());
                int repeat = Integer.parseInt(repeatField.getText());

                int keyToPress = -1;
                String keyText = keyField.getText().trim();
                if (!keyText.isEmpty()) {
                    char c = keyText.charAt(0);
                    if (Character.isDigit(c)) {
                        keyToPress = KeyEvent.VK_0 + (c - '0'); // từ VK_0 đến VK_9
                    }
                }

                autoHonDan = new AutoHonDan(delay, startDelay, repeat, keyToPress);
                autoHonDan.start();
                toggleBtn.setText("Stop");
                statusLabel.setText("Trạng thái: Đang chạy...");
                System.out.println("Auto Hồn Đan START");
            } else {
                autoHonDan.stop();
                toggleBtn.setText("Start");
                statusLabel.setText("Trạng thái: Đang dừng");
                System.out.println("Auto Hồn Đan STOP");
            }
        });
    }

    public static void main(String[] args) {
        try {
            // Kích hoạt Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }
}
