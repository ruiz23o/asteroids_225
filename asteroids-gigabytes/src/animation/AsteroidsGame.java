package animation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Creates an Asteroids game similar to the classic Atari game.
 * Users can use keys to move a ship and shoot at asteroids.
 */

public class AsteroidsGame extends AbstractAnimation implements KeyListener {
    // Create game window
    private static JFrame frame = new JFrame();
    
    // Set window height and width
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 900;
    
    // variable that holds ship
    private Ship ship = new Ship(this);
    
    // initial number of asteroids
    private static int asteroidAmount = 5;
    
    // variable containing asteroids
    private List<Asteroid> asteroids = new ArrayList<>();
    
    
    //true when game is playing
    private boolean moving = true;
    
    //when shot is fired
    private Shot shot;
    
    //true when ship is shooting
    private boolean shooting = false;
    
    //score to be displayed
    private int score=0;
    
    // level of difficulty
    private int gameLevel = 0;

    
    //lives to be displayed
    private int lives = 3;
    
    // true when the respawn point is clear of asteroids
    private boolean canRespawn = true;
    
    // true when ship is destroyed (in respawn mode)
    private boolean isDestroyed = false;
    
    // stores a respawn point
    private Rectangle respawnPoint = new Rectangle(425, 425, 50, 50);


    /**
     * Constructs animation for Asteroids.
     */
    public AsteroidsGame() {
        setFocusable(true);
        addKeyListener(this);
        makeAsteroids();
    }
    
    /**
     * Fills the list with asteroids
     */
    private void makeAsteroids() {
        for (int i = 0; i < asteroidAmount; i++) {
            asteroids.add(new Asteroid(this));
        }
    }
    
    /**
     * Handles each new frame of game animation.
     */
    protected void nextFrame() {
        //holds any newly generated asteroids
        List<Asteroid> newAsteroids = new ArrayList<>();
        
        // checks if ship can be respawned
        checkRespawn();
        
        // Call nextFrame on ship and asteroid if moving is true
        if (moving) {
            // if ship is not destroyed, get next frame
            if (!isDestroyed) {
                ship.nextFrame();
            }
           
            for (Asteroid asteroid : asteroids) {
                asteroid.nextFrame();
            }
            // if the ship is shooting, get next frame of shot object
            if (shooting) {
                shot.nextFrame();
            }
            
            // repaint animated objects
            repaint();
            
            // loop through asteroids to check collision with Ship or Shot
            for (Asteroid asteroid : asteroids) {
                
                // if ship is not destroyed, check for a new collision with asteroid
                if (!isDestroyed) {
                    
                    // check for collision
                    if (checkCollision(ship, asteroid)) {
                        // if ship is hit, a life is lost and Ship disappears from screen
                        lives -= 1;
                        isDestroyed = true;
                        ship.destroy();
                        
                        // if there are remaining lives,
                        // set canRespawn to false until
                        // the respawn point has no Asteroids
                        if (lives != 0) {
                            canRespawn = false;
                        // if there no remaining lives, animation stops
                        } else {
                            moving = false;
                        }
                    }
                }
                
                // Check for a shot hitting an asteroid
                if (shooting && checkCollision(shot, asteroid)) {
                    // Add any new asteroids that may generate from big asteroids
                    newAsteroids.addAll(asteroid.destroy());
                    
                    // Ship is no longer shooting
                    shooting = false;
                    
                    // Increase score based on asteroid value
                    score+=asteroid.score();
                    
                    // Check if all asteroids have been destroyed and level should be increased
                    checkLevelChange();

                }
            }
        // if game is over bring up replay dialog box    
        } else {
            replayDialog();
        }
        
        //adds the newly generated asteroids to main asteroid list, then clears newAsteroids list 
        asteroids.addAll(newAsteroids);
        newAsteroids.clear();
    }
    
    /**
     * Creates replay option at the end of game.
     */
    private void replayDialog() {
        // Options for dialog box
        String [] options = {"Replay", "Quit"};
        
        // Creates replay option dialog
        int response = JOptionPane.showOptionDialog(frame, "You've died! Play again?", "Game over!",
                JOptionPane.ERROR_MESSAGE, 0, null, options, null);
        
        // If user chooses replay, animation reinitializes
        if (response == 0) {
            restart();
            moving = true;
            
        // If user chooses quit, program terminates
        } else if (response == 1) {
            System.exit(0);
        }
    }
    
    /**
     * Method called when player wants to restart the game.
     * Resets instance variables to their initial values.
     */
    public void restart() {
        ship = new Ship(this);
        asteroidAmount = 5;
        asteroids = new ArrayList<>();
        makeAsteroids();
        score = 0;
        lives = 3;
        gameLevel = 0;
        canRespawn = true;
        isDestroyed = false;
    }
    
    /**
     * Checks the respawn point in the middle of the screen
     * for collisions with an asteroid to see if a ship can
     * respawn without colliding immediate with another asteroid.
     */
    private void checkRespawn() {
        // checks if ship can respawn in the middle without collision
        for (Asteroid asteroid : asteroids) {
            // if the respawn point and the asteroid do not intersect, then canRespawn is true
            if (!asteroid.getShape().getBounds2D().intersects(respawnPoint.getBounds2D())) {
                canRespawn = true;
            } else {
                canRespawn = false;
                break;
            }
        }
        // reinitializes the ship if there is no danger of collision
        if (canRespawn && isDestroyed) {
            ship = new Ship(this);
            isDestroyed = false;
        }
    }
    
    /**
     * Checks if level of difficulty should be changed.
     * If yes, this method increases the level of 
     * difficulty.
     */
    private void checkLevelChange() {
        // loop through asteroids and tally how many are destroyed
        
        int destroyedAsters = 0;
        for (Asteroid aster : asteroids) {
            if (aster.isDestroyed()) {
                destroyedAsters++;
            }
        }
        
        // if all asteroids are destroyed, increase difficulty
        if (destroyedAsters == asteroids.size()) {
            gameLevel+= 0.5;
            
            // reinitialize asteroidsList with more asteroids
            asteroids = new ArrayList<>();
            asteroidAmount += 1;
            makeAsteroids();
            
            // change speed of asteroids to make game harder
            for (Asteroid asteroid : asteroids) {
                asteroid.changeSpeed(gameLevel);
            }
        }
    }
    
    /**
     * Check whether two object collide.  This tests whether their shapes intersect.
     * @param shape1 the first shape to test
     * @param shape2 the second shape to test
     * @return true if the shapes intersect
     */
    private boolean checkCollision(AnimatedObject shape1,
            AnimatedObject shape2) {
        return shape2.getShape().getBounds2D().intersects(shape1.getShape().getBounds2D());
    }
    
    /**
     * paints the score
     * @param g the graphic context to draw in
     */
    private void paintScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }
    
    /**
     * this displays the lives left for the ship
     * @param g the graphic that the score will be painted in
     */
    private void displayLives(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Lives Left: " + lives, 10, 40);
    }
    
    /**

     * Paint the animation by painting the objects in the animation.
     * @param g the graphic context to draw on
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintScore(g);
        displayLives(g);
        ship.paint((Graphics2D) g);
        for (Asteroid asteroid : asteroids) {
            asteroid.paint((Graphics2D) g);
        }
        if (shooting) {
            shot.paint((Graphics2D) g);
        }
    }
    
    @Override
    /**
     * This is called on the downward action when the user presses a key.
     * It notifies the animation about presses of up arrow, right 
     * arrow, left arrow, and the space bar.  All other keys are ignored.
     * @param e information about the key pressed
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_UP:
            // call Ship's method to thrust ship forward
            ship.up();
            break;
        case KeyEvent.VK_RIGHT:
            // call Ship's rotate clockwise method
            ship.right();
            break;
        case KeyEvent.VK_LEFT:
            // call Ship's rotate counterclockwise method
            ship.left();
            break;
        case KeyEvent.VK_SPACE:
            // call Ship's method to shoot
            shooting = true;
            shot = ship.space();
            break;
        case KeyEvent.VK_SHIFT:
            // call Ship's hyperspace method
            ship.shift();
            break;
        default:
            // ignore all other keys
        }
    }
    
    @Override
    /**
     * This is called when the user releases the key after pressing it.
     * It does nothing.
     * @param e information about the key released
     */
    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }

    @Override
    /**
     * This is called when the user presses and releases a key without
     * moving the mouse in between.  Does nothing.
     * @param e information about the key typed.
     */
    public void keyTyped(KeyEvent e) {
        // Nothing to do
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Set game window's title and size
        frame.setTitle("Asteroids");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Entire program exits when user closes window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create animation
        AsteroidsGame game = new AsteroidsGame();
        
        // Add animation to window
        Container contentPane = frame.getContentPane();
        contentPane.setBackground(Color.BLACK);
        contentPane.add(game, BorderLayout.CENTER);
        
        // Display window
        frame.setVisible(true);
        
        // Start animation
        game.start();
    }    
}