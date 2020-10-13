package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Shape;

/**
 * has asteroid movement and animation. 
 * asteroid will move at a random speed and direction in a straight line, 
 * asteroid will be generated in a random location, 
 * asteroid will wrap around screen upon reaching an edge
 */
public class Asteroid implements AnimatedObject {
    Random rand = new Random();

    private final int ASTEROID_HEIGHT;

    // the width of the asteroid: max of 15 pixels smaller than the height
    private final int ASTEROID_WIDTH;

    // The starting left edge of the asteroid, 600 is the amount of pixels in x
    // direction
    private int x;
    
    // The starting top of the asteroid, 600 is the amount of pixels in y
    // direction
    private int y;

    // how fast asteroid moves in x, from 1-3
    private int x_move = 1 + rand.nextInt(3);

    // how fast asteroid moves in y, from 1-3
    private int y_move = 1 + rand.nextInt(3);

    // determines if x is moving left or right
    private boolean isXPositive = rand.nextBoolean();

    // determines if y is moving up or down
    private boolean isYPositive = rand.nextBoolean();

    // The animation that this object is part of
    private AbstractAnimation animation;

    // The asteroid shape
    private Ellipse2D asteroid;

    // turns true when asteroid is destroyed
    private boolean destroyed = false;

    // how many frames the asteroid has been destroyed for
    private int lengthDestroy = 0;
    
    //size level of asteroid- 1 is small, 2 is medium, 3 is large
    int size;

    /**
     * Creates the asteroid
     * 
     * @param animation the animation this object is part of
     */
    public Asteroid(AbstractAnimation animation) {
        this.animation = animation;
        generateLocation();
        //random asteroid size- max size of 80, min size of 30
        ASTEROID_HEIGHT = 30 + rand.nextInt(50);
        ASTEROID_WIDTH= ASTEROID_HEIGHT - rand.nextInt(15);
        //finds size level
        findSize();
        // makes ellipse shaped asteroid:
        asteroid = new Ellipse2D.Double(x, y, ASTEROID_HEIGHT, ASTEROID_WIDTH);
    }
    
    public Asteroid(AbstractAnimation animation, int size, int xLocation, int yLocation) {
        this.animation=animation;
        x=xLocation;
        y=yLocation;
        if(size==1) {
            //max size of 40
            ASTEROID_HEIGHT = 30 + rand.nextInt(10);
        }
        else {
            //min size 41, max size 60
            ASTEROID_HEIGHT = 41 + rand.nextInt(20);
        }
        
        //makes width
        ASTEROID_WIDTH= ASTEROID_HEIGHT - rand.nextInt(15);
        //finds size level
        findSize();
        // makes ellipse shaped asteroid:
        asteroid = new Ellipse2D.Double(x, y, ASTEROID_HEIGHT, ASTEROID_WIDTH);
    }

    @Override
    public void nextFrame() {

        // how the asteroid acts when destroyed
        if (destroyed) {
            // blinks twice before disappearing
            if (lengthDestroy < 5
                    || (lengthDestroy < 15 && lengthDestroy > 10)) {
                asteroid.setFrame(x, y, ASTEROID_HEIGHT, ASTEROID_WIDTH);
            }

            else {
                asteroid.setFrame(0, 0, 0, 0);
            }
            lengthDestroy++;
        }

        // how the asteroid acts otherwise
        else {
            checkIfEdge();
            // move normally
            // x increases if positive, decreases if negative
            if (isXPositive) {
                x += x_move;
            } else {
                x -= x_move;
            }
            // y increases if positive, decreases if negative
            if (isYPositive) {
                y += y_move;
            } else {
                y -= y_move;
            }
            asteroid.setFrame(x, y, ASTEROID_HEIGHT, ASTEROID_WIDTH);
        }
    }

    /**
     * checks to see if asteroid is over edges of window: wraps around to other
     * side if it is
     */
    private void checkIfEdge() {
        // Check if the right edge of the asteroid is beyond the right
        // edge of the window. If it is, move it to the left edge
        if (x + ASTEROID_WIDTH > animation.getWidth()) {
            x = 0;
        }
        // Check if the left edge of the asteroid is beyond the left
        // edge of the window. If it is, move it to the right edge
        else if (x < 0) {
            x = animation.getWidth() - ASTEROID_WIDTH;
        }

        // Check if bottom edge of the asteroid is below the
        // edge of the window. If it is, put it at the top

        if (y + ASTEROID_HEIGHT > animation.getHeight()) {
            y = 0;
        }
        // check to see if top edge of asteroid is above
        // the top of the window. If it is, move to the bottom
        else if (y < 0) {
            y = animation.getHeight() - ASTEROID_HEIGHT;
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if(destroyed) {
            g.setColor(Color.DARK_GRAY);
        }
        else {
            g.setColor(Color.GRAY);
        }
        g.fill(asteroid);

    }

    @Override
    public Shape getShape() {
        return asteroid;
    }

    /**
     * destroys asteroids
     * @return the list of new asteroids, list will be empty if no new asteroids are generated 
     */
    public List<Asteroid> destroy() {
        destroyed = true;
        List<Asteroid> newAsteroids =new ArrayList<>();
        if(size==2 || size==3) {
            newAsteroids.add(new Asteroid(animation, size-1, x, y));
            newAsteroids.add(new Asteroid(animation, size-1, x, y));
        }
        return newAsteroids;
    }
    
    private void findSize() {
        //asteroid is less than 40, small
        if( ASTEROID_HEIGHT< 40) {
            size=1;
        }
        //asteroid is bigger than 40 and smaller than 60, medium
        else if(ASTEROID_HEIGHT< 60) {
            size=2;
        }
        //otherwise, asteroid is bigger than 60 and large sized
        else {
            size=3;
        }
    }
    
    /**
     * @return the point worth of the asteroid
     */
    public int score() {
        if (size==1) {
            return 100;
        }
        if(size==2) {
            return 50;
        }
        return 20;
    }
    
    /**
     * 
     * @return true when the asteroid has been destroyed
     */
    public boolean isDestroyed() {
        boolean temp = destroyed;
        return temp;
    }
    /**
     * generates in a location that isn't on top of the spaceship
     * spaceship is at 300, 300 and is 40 long at largest
     */
    private void generateLocation() {
        boolean left= rand.nextBoolean();
        boolean above=rand.nextBoolean();
        if(left) {
            x=rand.nextInt(280)-ASTEROID_HEIGHT;
        }
        else {
            x=320+rand.nextInt(280) + ASTEROID_HEIGHT;
        }
        if(above) {
            y=rand.nextInt(280)-ASTEROID_WIDTH;
        }
        else {
            y=320+rand.nextInt(280)+ASTEROID_WIDTH;
        }
    }
    
    /**
     * Method used by game class to increase
     * game difficulty after level change.
     * @param change int representing level/speed change
     */
    public void changeSpeed(int change) {
        x_move += change;
        y_move += change;
    }

}
