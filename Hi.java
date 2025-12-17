import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.awt.*;
import javax.swing.*;

public class Hi {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int SALT_LENGTH = 16;

    public static void main(String[] args) {
        // Set Nimbus Look-and-Feel for a modern UI appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Encryption & Decryption");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(null); // center the frame

        // Create a custom panel with a gradient background.
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                Color startColor = new Color(70, 73, 75);
                Color endColor = new Color(40, 42, 43);
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        frame.setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLabel = new JLabel("Encryption & Decryption", SwingConstants.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(titleLabel, gbc);

        // Text label and field
        JLabel textLabel = new JLabel("Enter text:");
        textLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        backgroundPanel.add(textLabel, gbc);

        JTextField textField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        backgroundPanel.add(textField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Enter password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        backgroundPanel.add(passwordField, gbc);
        
        // Retrieve the default echo char from UIManager (fallback to '*' if unknown)
        Character uiEchoChar = (Character) UIManager.get("PasswordField.echoChar");
        final char defaultEchoChar = (uiEchoChar != null) ? uiEchoChar : '*';

        // Show Password Checkbox
        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setForeground(Color.WHITE);
        showPasswordCheckbox.setBackground(new Color(70, 73, 75, 0)); // transparent background for checkbox
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        backgroundPanel.add(showPasswordCheckbox, gbc);

        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        // Create a uniform button size:
        Dimension buttonSize = new Dimension(150, 40);

        // Encrypt Button
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.setBackground(new Color(76, 175, 80));
        encryptButton.setForeground(Color.WHITE);
        encryptButton.setPreferredSize(buttonSize);
        encryptButton.setMinimumSize(buttonSize);
        encryptButton.setMaximumSize(buttonSize);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(encryptButton, gbc);

        // Decrypt Button
        JButton decryptButton = new JButton("Decrypt");
        decryptButton.setBackground(new Color(33, 150, 243));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setPreferredSize(buttonSize);
        decryptButton.setMinimumSize(buttonSize);
        decryptButton.setMaximumSize(buttonSize);
        gbc.gridx = 1;
        gbc.gridy = 4;
        backgroundPanel.add(decryptButton, gbc);

        // Result area with scroll pane
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(38, 50, 56));
        resultArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        backgroundPanel.add(scrollPane, gbc);

        // Encrypt button functionality
        encryptButton.addActionListener(e -> {
            try {
                String originalText = textField.getText();
                String password = new String(passwordField.getPassword());
                
                // Validate: Do not process if password is empty.
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                resultArea.setText("Encrypting...\n");
                animate(resultArea);
                String encryptedText = encrypt(originalText, password);
                resultArea.append(encryptedText);
                JOptionPane.showMessageDialog(frame, "Encryption Completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                resultArea.setText("Encryption failed.");
                JOptionPane.showMessageDialog(frame, "Encryption Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Decrypt button functionality
        decryptButton.addActionListener(e -> {
            try {
                String encryptedText = textField.getText();
                String password = new String(passwordField.getPassword());
                
                // Validate: Do not process if password is empty.
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                resultArea.setText("Decrypting...\n");
                animate(resultArea);
                String decryptedText = decrypt(encryptedText, password);
                resultArea.append(decryptedText);
                JOptionPane.showMessageDialog(frame, "Decryption Completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                resultArea.setText("Decryption failed.");
                JOptionPane.showMessageDialog(frame, "Decryption Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static String encrypt(String data, String password) throws Exception {
        byte[] salt = generateSalt();
        SecretKey key = generateKey(password, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedIvAndText = new byte[salt.length + encryptedData.length];
        System.arraycopy(salt, 0, encryptedIvAndText, 0, salt.length);
        System.arraycopy(encryptedData, 0, encryptedIvAndText, salt.length, encryptedData.length);
        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(decodedData, 0, salt, 0, salt.length);
        SecretKey key = generateKey(password, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
        byte[] originalData = cipher.doFinal(decodedData, salt.length, decodedData.length - salt.length);
        return new String(originalData, StandardCharsets.UTF_8);
    }

    private static SecretKey generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Simple animation in the result area
    private static void animate(JTextArea resultArea) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            resultArea.append(".");
            Thread.sleep(300);
        }
        resultArea.append("\n");
    }
}
