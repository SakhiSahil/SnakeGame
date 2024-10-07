/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;

import javax.swing.JFrame;

/**
 *
 * @author Herat Afghan Asia
 */
public class SnakeGame extends JFrame {

    GamePanel gamePanel;

    public SnakeGame() {

        //Load GamePanel
        gamePanel = new GamePanel();
        add(gamePanel);
        pack();
        gamePanel.setFocusable(true);

        setTitle("SnakeGame - Ghulam Sakhi Sahil");
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setSize(615, 638);
        setLayout(null);
        setLocationRelativeTo(null);
        
    }

    public static void main(String[] args) {

        //Editor UI
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SnakeGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //Edtor UI
        

        java.awt.EventQueue.invokeLater(() -> {

            new SnakeGame().setVisible(true);
        });
    }

}
