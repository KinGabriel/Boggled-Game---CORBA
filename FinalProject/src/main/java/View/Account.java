package View;

import Client.Controller;

import javax.swing.*;

/**
 *
 * @author chloeleesanmiguel
 */
public class Account extends javax.swing.JFrame {

    /**
     * Creates new form Account
     */
    public Account() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        confirmPasswordInput = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        changePasswordInput = new javax.swing.JTextField();
        usernameInput = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        kuromi = new javax.swing.JButton();
        spiderman = new javax.swing.JButton();
        stitch = new javax.swing.JButton();
        bubbles = new javax.swing.JButton();
        captainAmerica = new javax.swing.JButton();
        grinch = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();
//        uploadButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 18)); // NOI18N
        jLabel1.setText("USERNAME:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 146, 30));

        jLabel2.setFont(new java.awt.Font("Luminari", 1, 36)); // NOI18N
        jLabel2.setText("PERSONAL DETAILS");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 410, 30));

        jLabel3.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 18)); // NOI18N
        jLabel3.setText("CHANGE PASSWORD:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 250, 30));

        confirmPasswordInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmPasswordInputActionPerformed(evt);
            }
        });
        jPanel1.add(confirmPasswordInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 290, 190, -1));

        jLabel4.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 18)); // NOI18N
        jLabel4.setText("CONFIRM PASSWORD:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 290, 220, 30));

        changePasswordInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordInputActionPerformed(evt);
            }
        });
        jPanel1.add(changePasswordInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 230, 190, -1));

        usernameInput.setEditable(false);
        usernameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameInputActionPerformed(evt);
            }
        });
        jPanel1.add(usernameInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 270, -1));

        jLabel5.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 18)); // NOI18N
        jLabel5.setText("CHOOSE YOUR AVATAR:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 360, 230, 30));

        backButton.setBackground(new java.awt.Color(103,111,95));
        backButton.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 13)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        jPanel1.add(backButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 670, 80, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/alaga.gif"))); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 240, 170));

        jLabel6.setFont(new java.awt.Font("Katari", 0, 13)); // NOI18N
        jLabel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "AVATAR", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Kannada MN", 1, 10))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 430, 60, 60));
        jLabel6.setBounds(10, 20, 50, 50);

        jPanel2.setBackground(new java.awt.Color(129, 145, 113));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 1, true));

        kuromi.setBackground(new java.awt.Color(102, 102, 102));
        kuromi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kuromi.png"))); // NOI18N
        kuromi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kuromiActionPerformed(evt);
            }
        });

        spiderman.setBackground(new java.awt.Color(102, 102, 102));
        spiderman.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spiderman.png"))); // NOI18N
        spiderman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spidermanActionPerformed(evt);
            }
        });

        stitch.setBackground(new java.awt.Color(102, 102, 102));
        stitch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stitch.png"))); // NOI18N
        stitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stitchActionPerformed(evt);
            }
        });

        bubbles.setBackground(new java.awt.Color(102, 102, 102));
        bubbles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bubbles.png"))); // NOI18N
        bubbles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubblesActionPerformed(evt);
            }
        });

        captainAmerica.setBackground(new java.awt.Color(102, 102, 102));
        captainAmerica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/captainAmerica.png"))); // NOI18N
        captainAmerica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                captainAmericaActionPerformed(evt);
            }
        });

        grinch.setBackground(new java.awt.Color(102, 102, 102));
        grinch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/grinch.png"))); // NOI18N
        grinch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grinchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(kuromi)
                                        .addComponent(bubbles))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spiderman)
                                        .addComponent(captainAmerica))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(stitch)
                                        .addComponent(grinch))
                                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap(17, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(kuromi, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(spiderman, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(stitch, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(captainAmerica)
                                        .addComponent(bubbles)
                                        .addComponent(grinch))
                                .addGap(15, 15, 15))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 400, 280, 160));

        submitButton.setBackground(new java.awt.Color(129, 139, 119));
        submitButton.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 13)); // NOI18N
        submitButton.setForeground(new java.awt.Color(255, 255, 255));
        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, 80, -1));

//        uploadButton1.setBackground(new java.awt.Color(129, 139, 119));
//        uploadButton1.setFont(new java.awt.Font("Kohinoor Devanagari", 1, 13)); // NOI18N
//        uploadButton1.setForeground(new java.awt.Color(255, 255, 255));
//        uploadButton1.setText("Upload");
//        uploadButton1.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                uploadButton1ActionPerformed(evt);
//            }
//        });
//        jPanel1.add(uploadButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 570, 80, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bgplain.jpg"))); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 490, 720));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    private void usernameInputActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void changePasswordInputActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void confirmPasswordInputActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void kuromiActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Controller.getInstance().updateAvatar(1);
            }
        });
    }

    private void spidermanActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Controller.getInstance().updateAvatar(2);
            }
        });
    }

    private void stitchActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Controller.getInstance().updateAvatar(4);
            }
        });
    }

    private void bubblesActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Controller.getInstance().updateAvatar(5);
            }
        });
    }

    private void captainAmericaActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Controller.getInstance().updateAvatar(1);
            }
        });
    }

    private void grinchActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to change the avatar?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
               Controller.getInstance().updateAvatar(3);
            }
        });
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
           Controller.getInstance().showHomepage();
        });
    }

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            String username = usernameInput.getText();
            String password = changePasswordInput.getText();
            String retypePassword = confirmPasswordInput.getText();
            Controller.getInstance().updateProfile(username, password, retypePassword);
        });
    }

    private void uploadButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {

        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Account.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Account.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Account.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Account.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Account().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton backButton;
    private javax.swing.JButton bubbles;
    private javax.swing.JButton captainAmerica;
    private javax.swing.JTextField changePasswordInput;
    private javax.swing.JTextField confirmPasswordInput;
    private javax.swing.JButton grinch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton kuromi;
    private javax.swing.JButton spiderman;
    private javax.swing.JButton stitch;
    private javax.swing.JButton submitButton;
//    private javax.swing.JButton uploadButton1;
    public javax.swing.JTextField usernameInput;
    // End of variables declaration
}


