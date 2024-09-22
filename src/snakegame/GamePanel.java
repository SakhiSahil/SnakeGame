/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package snakegame;

/**
 *
 * @author Herat Afghan Asia
 */
import javax.swing.*; // Import Swing components for GUI
import java.awt.Color; // Import Color class for coloring components
import java.awt.*; // Import AWT classes for graphics and event handling
import java.awt.event.ActionEvent; // Import for action events (used by Timer)
import java.awt.event.ActionListener; // Import for handling action events (e.g., timer ticks)
import java.awt.event.KeyAdapter; // Import for handling keyboard events
import java.awt.event.KeyEvent; // Import for specific key events (like arrow keys)

public class GamePanel extends JPanel implements ActionListener {

    private final int TILE_SIZE = 10; // Size of each tile on the grid
    private final int BOARD_WIDTH = 500; // Total width of the game board
    private final int BOARD_HEIGHT = 500; // Total height of the game board
    private final int ALL_TILES = (BOARD_WIDTH * BOARD_HEIGHT) / (TILE_SIZE * TILE_SIZE); // Maximum number of tiles on the board
    private final int DELAY = 150; // Delay between game updates

    private final int x[] = new int[ALL_TILES]; // x-coordinates of the snake's body parts
    private final int y[] = new int[ALL_TILES]; // y-coordinates of the snake's body parts

    private int bodyParts; // Current size of the snake
    private int foodX; // x-coordinate of the food
    private int foodY; // y-coordinate of the food
    private int score; // Score

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer; // Timer for game loop
    private JButton tryAgainButton; // Button to restart the game

    public GamePanel() {
        setBackground(Color.BLACK); // Set the background color to black
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)); // Set the preferred size of the panel
        setFocusable(true); // Allow panel to capture keyboard input
        addKeyListener(new TAdapter()); // Add key listener for arrow keys

        initializeTryAgainButton(); // Initialize the "Try Again" button
        startGame(); // Start the game
    }

    public void startGame() {
        bodyParts = 3; // Start with 3 body parts
        score = 0; // Reset score to 0
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * TILE_SIZE; // Position the snake horizontally
            y[i] = 50;
        }
        placeFood(); // Place the food randomly on the board
        inGame = true; // Game is active
        hideTryAgainButton(); // Hide the "Try Again" button

        timer = new Timer(DELAY, this); // Create a timer with a delay
        timer.start(); // Start the timer
    }

    private void placeFood() {
        int randPosition = (int) (Math.random() * (BOARD_WIDTH / TILE_SIZE)); // Generate random x position
        foodX = randPosition * TILE_SIZE;
        randPosition = (int) (Math.random() * (BOARD_HEIGHT / TILE_SIZE)); // Generate random y position
        foodY = randPosition * TILE_SIZE;
    }

    private void initializeTryAgainButton() {
        tryAgainButton = new JButton("Try Again"); // Create the "Try Again" button
        tryAgainButton.setBounds((BOARD_WIDTH/2)-50, (BOARD_HEIGHT/2)+30, 100, 30); // Set the size and position of the button below the game over message
        tryAgainButton.setFocusable(false); // Disable button focus to avoid interfering with key inputs
        tryAgainButton.setBackground(Color.YELLOW);
        tryAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // Restart the game when button is clicked
            }
        });
        setLayout(null); // Use null layout to manually position the button
        add(tryAgainButton); // Add the button to the panel
        hideTryAgainButton(); // Hide the button initially
    }

    private void hideTryAgainButton() {
        tryAgainButton.setVisible(false); // Hide the "Try Again" button
    }

    private void showTryAgainButton() {
        tryAgainButton.setVisible(true); // Show the "Try Again" button
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawGame(g); // Draw the game elements (snake, food, score)
        } else {
            drawGameOver(g); // Display "Game Over" and the score
            showTryAgainButton(); // Show the "Try Again" button
        }
    }

    private void drawGame(Graphics g) {
        // Draw food
        g.setColor(Color.RED);
        g.fillRect(foodX, foodY, TILE_SIZE, TILE_SIZE); // Draw the food

        // Draw snake
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.GREEN); // Head of the snake
            } else {
                g.setColor(Color.WHITE); // Body of the snake
            }
            g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE); // Draw each part of the snake
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20); // Draw the current score at the top left
    }

    private void drawGameOver(Graphics g) {
        String gameOverMsg = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 18); // Font for the game over text
        FontMetrics metrics = getFontMetrics(font); // Metrics to center the text

        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(gameOverMsg, (getWidth() - metrics.stringWidth(gameOverMsg)) / 2, getHeight() / 2 - 30); // Draw "Game Over" text in the center

        g.drawString("Score: " + score, (getWidth() - metrics.stringWidth("Score: " + score)) / 2, getHeight() / 2); // Draw the score below "Game Over"
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1]; // Move the body parts
            y[i] = y[i - 1];
        }

        if (leftDirection) {
            x[0] -= TILE_SIZE; // Move left
        }
        if (rightDirection) {
            x[0] += TILE_SIZE; // Move right
        }
        if (upDirection) {
            y[0] -= TILE_SIZE; // Move up
        }
        if (downDirection) {
            y[0] += TILE_SIZE; // Move down
        }
    }

    private void checkCollision() {
        // Check if the snake's head collides with its body
        for (int i = bodyParts; i > 0; i--) {
            if ((i > 3) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false; // End the game if collision occurs
            }
        }

        // Check if the snake's head hits the wall
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            inGame = false; // End the game if head hits the wall
        }

        if (!inGame) {
            timer.stop(); // Stop the timer when the game ends
        }
    }

    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++; // Increase snake size
            score++; // Increase score
            placeFood(); // Place new food
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkFood(); // Check if snake eats food
            checkCollision(); // Check for collisions
            move(); // Move the snake
        }
        repaint(); // Repaint the panel to update the visuals
    }

    private class TAdapter extends KeyAdapter { // Inner class to handle keyboard input
        @Override
        public void keyPressed(KeyEvent e) { // Method triggered when a key is pressed
            int key = e.getKeyCode(); // Get the code of the pressed key

            // Change direction based on arrow keys, but prevent 180-degree turns
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true; // Set direction to left
                upDirection = false; 
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true; // Set direction to right
                upDirection = false; 
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true; // Set direction to up
                rightDirection = false; 
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true; // Set direction to down
                rightDirection = false; 
                leftDirection = false;
            }
        }
    }
}
