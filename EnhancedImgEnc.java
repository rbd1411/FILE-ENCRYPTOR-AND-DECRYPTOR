import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public class EnhancedImgEnc {
    private static Map<String, Integer> fileEncryptionKeys = new HashMap<>();
    private static Map<String, Boolean> fileEncryptionStatus = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("File Encryptor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.GREEN);

        JLabel titleLabel = new JLabel("File Encryptor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        keyPanel.setBackground(Color.WHITE);

        JTextField keyField = new JTextField(10);
        keyField.setFont(new Font("Arial", Font.PLAIN, 14));

        keyPanel.add(new JLabel("Enter Key:"));
        keyPanel.add(keyField);

        centerPanel.add(keyPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton encryptButton = new JButton("Encrypt");
        styleButton(encryptButton);

        JButton decryptButton = new JButton("Decrypt");
        styleButton(decryptButton);

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(centerPanel, BorderLayout.CENTER);

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFile(true, keyField.getText(), frame);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processFile(false, keyField.getText(), frame);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
            }
        });
    }

    public static void processFile(boolean isEncrypt, String keyText, JFrame frame) {
        try {
            int key = Integer.parseInt(keyText);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();

                for (File file : selectedFiles) {
                    if (isEncrypt) {
                        if (fileEncryptionStatus.containsKey(file.getAbsolutePath()) && fileEncryptionStatus.get(file.getAbsolutePath())) {
                            JOptionPane.showMessageDialog(frame, "File is already encrypted: " + file.getName());
                        } else {
                            fileEncryptionKeys.put(file.getAbsolutePath(), key);
                            encryptFile(file, key);
                            fileEncryptionStatus.put(file.getAbsolutePath(), true);
                        }
                    } else {
                        if (!fileEncryptionKeys.containsKey(file.getAbsolutePath())) {
                            JOptionPane.showMessageDialog(frame, "File is not encrypted: " + file.getName());
                        } else if (fileEncryptionKeys.get(file.getAbsolutePath()) != key) {
                            JOptionPane.showMessageDialog(frame, "Invalid Key: Cannot decrypt with a different key.");
                        } else {
                            decryptFile(file, key);
                            fileEncryptionKeys.remove(file.getAbsolutePath());
                            fileEncryptionStatus.put(file.getAbsolutePath(), false);
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid Key: Please enter an integer.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage());
        }
    }

    public static void encryptFile(File file, int key) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ key);
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        
        JOptionPane.showMessageDialog(null, "Encryption Successful: " + file.getName());
    }

    public static void decryptFile(File file, int key) throws IOException {
        encryptFile(file, key); // Decryption is the same as encryption with the same key
        JOptionPane.showMessageDialog(null, "Decryption Successful");
    }
}
