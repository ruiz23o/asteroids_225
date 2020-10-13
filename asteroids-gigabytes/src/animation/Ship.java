package animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import animation.AbstractAnimation;
import animation.AnimatedObject;

/**
 *
 *
 */
public class Ship extends JPanel implements AnimatedObject {
    Random random = new Random();

    // The animation that this object is part of.
    private AbstractAnimation animation;

    // The shape that is drawn
    private Polygon ship;
    
    // the height of the ship
    private final double SHIP_HEIGHT;
    
    // the width of the ship
    private final double SHIP_WIDTH;

    // The left edge of the shape
    private int x;

    // The top edge of the shape
    private int y;
    
    private double angle;
    
    //variables that give angle at which ship should move
    private double dirX;
    private double dirY;
    
    /**
    * Constructs a triangle for the ship
    */
    public Ship (AbstractAnimation animation) {
       this.animation = animation;
       //gets points for making the triangle
      ship = new Polygon();
      ship.addPoint(10, 20);
      ship.addPoint(0, -20);
      ship.addPoint(-10, 20);
        
      //initialization of variables
      dirX = 0;
      dirY = 1;

      //holds the place the ship will originally appear in middle of screen
      x = 450;
      y = 450;
      
      SHIP_HEIGHT = ship.getBounds2D().getHeight();
      SHIP_WIDTH = ship.getBounds2D().getWidth();
    }


    /**
     * Draws the triangle
     * @param g the graphics context to draw on
     */

    public void paint(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.draw(getShape());
        //g.fill(getShape());
    }



    /**
     * When the user clicks the up arrow button, changes the coordinates of the y axis
     */
    public void up() {
        //moves the ship forward
        x+=getXDirection() * 2;
        y+=getYDirection() * 2;
    }

    /**
     * when the user clicks the right arrow button, it rotates the ship 45 degrees to the right
     *
     */
    public void right() {
        angle = angle + Math.PI/15;
        getShape();
    }

    /**
     *  when the user clicks the left arrow button, the ship rotates to 45 degrees to the left
     *
     */
    public void left() {
       angle = angle - Math.PI/15;
       getShape();
    }

    /**
     * When the space key is pressed, we call the shoot method
     * This prints Space on the console.
     */
    public Shot space() {
      //shooting method that will be implemented
      Shot shot = new Shot(animation, x, y, getXDirection(), getYDirection());
      
      return shot;
    }

    /**
     * When the shift key is pressed, hyper speed is activated
     */
    public void shift() {
        //this generates the random place on the x axis hyperspeed takes the ship
        int ranX = random.nextInt(590);
        //this generates the random place on the y axis hyperspeed takes the ship
        int ranY = random.nextInt(590);
        x = ranX;
        y = ranY;
    }
    

    @Override
    /**
     * Updates the animated object for the next frame of the animation
     * and repaints the window.
     */
    //@Override
    public void nextFrame() {
        getShape();
        checkIfEdge();
        repaint();
        
    }
    
    /**
     * checks to see if asteroid is over edges of window: wraps around to other
     * side if it is
     */
    private void checkIfEdge() {
        // Check if the right edge of the asteroid is beyond the right
        // edge of the window. If it is, move it to the left edge
        if (x + SHIP_WIDTH > animation.getWidth()) {
            x = 0;
        }
        // Check if the left edge of the asteroid is beyond the left
        // edge of the window. If it is, move it to the right edge
        else if (x < 0) {
            x = (int) (animation.getWidth() - SHIP_WIDTH);
        }

        // Check if bottom edge of the asteroid is below the
        // edge of the window. If it is, put it at the top

        if (y + SHIP_HEIGHT > animation.getHeight()) {
            y = 0;
        }
        // check to see if top edge of asteroid is above
        // the top of the window. If it is, move to the bottom
        else if (y < 0) {
            y = (int) (animation.getHeight() - SHIP_HEIGHT);
        }
    }
    
      /**
       * Returns the shape after applying the current translation
       * and rotation
       * @return the shape located as we want it to appear
       */
      public Shape getShape() {
          // AffineTransform captures the movement and rotation we
          // want the shape to have
          AffineTransform at1 = new AffineTransform();
         
              
              at1.translate(x, y);
              // Rotate the shape 45 degrees to the right
              at1.rotate(angle);

              double dirX2 = (dirX*Math.cos(angle))-(dirY*Math.sin(angle));
              double dirY2 = (dirX*Math.sin(angle))+ (dirY*Math.cos(angle));
              dirX= dirX2;
              dirY=dirY2;
              AffineTransform at2 = at1;
              
              // Create a shape that looks like our triangle, but centered
              // and rotated as specified by the AffineTransform object.
              return at2.createTransformedShape(ship);
              
      

      }
      
      /**
       * Returns the direction the x 
       * @return the the angle the x axis moves by
       */
      public double getXDirection(){
        //gives us the angle the ship will move in for the x direction
          dirX = 10* Math.sin(angle);
          return dirX;
      }
      
      /**
       * Returns the direction the y
       * @return the the angle the y axis moves by
       */
      public double getYDirection(){
        //gives us the angle the ship will move in for the y direction
          dirX = 10* Math.sin(angle);
          dirY = -10* Math.cos(angle);
          return dirY;
      }
      
      /**
       * Called by game class when ship is hit by asteroid,
       * resets the ship object to disappear on screen
       */
      public void destroy() {
          ship.reset();
      }
      
}
