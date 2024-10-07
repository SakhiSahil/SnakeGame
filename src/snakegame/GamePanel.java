package snakegame;

import javax.swing.*; // Import Swing components for GUI
import java.awt.Color; // Import Color class for coloring components
import java.awt.*; // Import AWT classes for graphics and event handling
import java.awt.event.ActionEvent; // Import for action events (used by Timer)
import java.awt.event.ActionListener; // Import for handling action events (e.g., timer ticks)
import java.awt.event.KeyAdapter; // Import for handling keyboard events
import java.awt.event.KeyEvent; // Import for specific key events (like arrow keys)
import java.awt.geom.AffineTransform;

public class GamePanel extends JPanel implements ActionListener {

    private Image headImage;  // Snake head image
    private Image bodyImage;  // Snake body image
    private Image[] foodImages; // Array to hold food images
    private Image currentFoodImage; // Holds the current food image

    private static final int TILE_SIZE = 25; // Size of each tile on the grid
    private static final int BOARD_WIDTH = 600; // Total width of the game board
    private static final int BOARD_HEIGHT = 600; // Total height of the game board
    private static final int ALL_TILES = (BOARD_WIDTH * BOARD_HEIGHT) / (TILE_SIZE * TILE_SIZE); // Maximum number of tiles on the board
    private static final int DELAY = 300; // Delay between game updates

    private final int[] x = new int[ALL_TILES]; // x-coordinates of the snake's body parts
    private final int[] y = new int[ALL_TILES]; // y-coordinates of the snake's body parts

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
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());

        // Load the images for the snake
        loadImages();

        initializeTryAgainButton();
        startGame();
        setFocusable(true);
    }

    public void startGame() {
        bodyParts = 3; // Start with 3 body parts
        score = 0; // Reset score to 0
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 150 - i * TILE_SIZE; // Adjust initial position of the snake
            y[i] = 150;
        }
        placeFood(); // Place the food randomly on the board
        inGame = true;
        hideTryAgainButton();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void placeFood() {
        int randPositionX = (int) (Math.random() * (BOARD_WIDTH / TILE_SIZE));  // Random x position
        int randPositionY = (int) (Math.random() * (BOARD_HEIGHT / TILE_SIZE));  // Random y position

        // Ensure food position is within bounds
        foodX = Math.min(randPositionX * TILE_SIZE, BOARD_WIDTH - TILE_SIZE);
        foodY = Math.min(randPositionY * TILE_SIZE, BOARD_HEIGHT - TILE_SIZE);

        // Randomly select a food image from the array
        int randomIndex = (int) (Math.random() * foodImages.length); // Random index
        currentFoodImage = foodImages[randomIndex]; // Assign the selected food image to currentFoodImage
    }

    private void initializeTryAgainButton() {
        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds((BOARD_WIDTH / 2) - 50, (BOARD_HEIGHT / 2) + 30, 100, 30);
        tryAgainButton.setFocusable(false);
        tryAgainButton.setBackground(Color.YELLOW);
        tryAgainButton.addActionListener(e -> startGame());
        setLayout(null);
        add(tryAgainButton);
        hideTryAgainButton();
    }

    private void hideTryAgainButton() {
        tryAgainButton.setVisible(false);
    }

    private void showTryAgainButton() {
        tryAgainButton.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawGame(g);
        } else {
            drawGameOver(g);
            showTryAgainButton();
        }
    }

    private void drawGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the grid
        //g.setColor(Color.LIGHT_GRAY);
        //for (int i = 0; i < BOARD_WIDTH; i += TILE_SIZE) {
       //     g.drawLine(i, 0, i, BOARD_HEIGHT); // Vertical lines
       // }
       // for (int j = 0; j < BOARD_HEIGHT; j += TILE_SIZE) {
        //    g.drawLine(0, j, BOARD_WIDTH, j); // Horizontal lines
       // }

        // Draw the current food image
        g.drawImage(currentFoodImage, foodX, foodY, TILE_SIZE, TILE_SIZE, this);

        // Draw snake with rotated images
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                // Rotate head based on direction
                drawRotatedImage(g2d, headImage, x[i], y[i], getHeadRotationAngle());
            } else {
                drawRotatedImage(g2d, bodyImage, x[i], y[i], 0);
            }
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    private void drawGameOver(Graphics g) {
        String gameOverMsg = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 18);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString(gameOverMsg, (getWidth() - metrics.stringWidth(gameOverMsg)) / 2, getHeight() / 2 - 30);
        g.drawString("Score: " + score, (getWidth() - metrics.stringWidth("Score: " + score)) / 2, getHeight() / 2);
    }

    private void move() {
        // Store the current position of the head
        int newHeadX = x[0];
        int newHeadY = y[0];

        // Determine the new head position based on the current direction
        if (leftDirection) {
            newHeadX -= TILE_SIZE;
        }
        if (rightDirection) {
            newHeadX += TILE_SIZE;
        }
        if (upDirection) {
            newHeadY -= TILE_SIZE;
        }
        if (downDirection) {
            newHeadY += TILE_SIZE;
        }

        // Check for wall collisions before updating the head's position
        if (newHeadX < 0 || newHeadX >= BOARD_WIDTH || newHeadY < 0 || newHeadY >= BOARD_HEIGHT) {
            inGame = false; // End the game if head hits the wall
            timer.stop(); // Stop the timer immediately
            return; // Exit the method
        }

        // Move the body parts
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Update the head's position
        x[0] = newHeadX;
        y[0] = newHeadY;
    }

    private void checkCollision() {
        // Check if the snake's head collides with its body
        for (int i = bodyParts; i > 0; i--) {
            if ((i > 3) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false; // End the game if collision occurs with itself
            }
        }

        // Check if the snake's head hits the wall
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            inGame = false; // End the game if head hits the wall
        }

        // Adjust for the size of the snake to prevent going out of bounds
        if (x[0] >= BOARD_WIDTH || y[0] >= BOARD_HEIGHT) {
            inGame = false; // End the game if head hits the right or bottom wall
        }

        if (!inGame) {
            timer.stop(); // Stop the timer when the game ends
        }
    }

    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++; // Increase the size of the snake
            score++; // Increase the score
            placeFood(); // Place a new food item, which will also change the image
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkFood();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }

    private double getHeadRotationAngle() {
        if (leftDirection) {
            return Math.toRadians(270);
        }
        if (rightDirection) {
            return Math.toRadians(90);
        }
        if (upDirection) {
            return 0;
        }
        if (downDirection) {
            return Math.toRadians(180);
        }
        return 0;  // Default, should not reach here
    }

    private void drawRotatedImage(Graphics2D g2d, Image image, int x, int y, double angle) {
        // Save the current transformation
        AffineTransform oldTransform = g2d.getTransform();

        // Set the rotation transformation
        g2d.rotate(angle, x + TILE_SIZE / 2, y + TILE_SIZE / 2);  // Rotate around the center of the image

        // Draw the image at the given coordinates with specified size
        g2d.drawImage(image, x, y, TILE_SIZE, TILE_SIZE, this); // Use TILE_SIZE for width and height

        // Restore the original transformation
        g2d.setTransform(oldTransform);
    }

    private Image resizeImage(Image originalImage, int width, int height) {
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return resizedImage;
    }

    private void loadImages() {
        try {
            headImage = new ImageIcon(getClass().getResource("snake_head.png")).getImage();
            bodyImage = new ImageIcon(getClass().getResource("snake_body.png")).getImage();

            // Resize snake images to TILE_SIZE
            headImage = resizeImage(headImage, TILE_SIZE, TILE_SIZE);
            bodyImage = resizeImage(bodyImage, TILE_SIZE, TILE_SIZE);

            // Load food images
            foodImages = new Image[]{
                new ImageIcon(getClass().getResource("apple.png")).getImage(),
                new ImageIcon(getClass().getResource("apple1.png")).getImage(),
                new ImageIcon(getClass().getResource("potato.png")).getImage(),
                new ImageIcon(getClass().getResource("potato1.png")).getImage(),
                new ImageIcon(getClass().getResource("tomato.png")).getImage(),
                new ImageIcon(getClass().getResource("tomato1.png")).getImage()
            };

            // Resize food images to TILE_SIZE
            for (int i = 0; i < foodImages.length; i++) {
                foodImages[i] = resizeImage(foodImages[i], TILE_SIZE, TILE_SIZE);
            }
        } catch (Exception e) {
            System.out.println("Image loading failed: " + e.getMessage());
        }
    }

}
