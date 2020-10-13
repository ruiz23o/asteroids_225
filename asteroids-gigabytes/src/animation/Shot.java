package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Shot implements AnimatedObject {
    // size of the shot
    private static final int SHOT_SIZE = 8;
    // where in x direction
    private int x;
    // where in y direction
    private int y;
    // how fast it is moving
    private int speed = 8;
    // where it is moving in x direction
    private double x_move;
    // where it is moving in y direction
    private double y_move;
    // The animation that this object is part of
    private AbstractAnimation animation;
    // The shot shape
    private Ellipse2D shot;
    // true when shot is ended
    private boolean ended=false;

    /**
     * creates Shot
     * 
     * @param animation  the animation this is part of
     * @param positionX  the position the shot originates in the x direction
     * @param positionY  the position the shot originates in the y direction
     * @param directionX the direction the shot is being fired in the x
     *                   direction
     * @param directionY the direction the shot is fired in the y direction
     */
    public Shot(AbstractAnimation animation, int positionX, int positionY,
            double directionX, double directionY) {
        this.animation = animation;
        x = positionX;
        y = positionY;
        x_move = speed * directionX;
        y_move = speed * directionY;
        shot = new Ellipse2D.Double(x, y, SHOT_SIZE, SHOT_SIZE);
    }

    @Override
    public void nextFrame() {
        if (ended) {
            shot.setFrame(0, 0, 0, 0);
        } else {
            x += x_move;
            y += y_move;
            shot.setFrame(x, y, SHOT_SIZE, SHOT_SIZE);
        }

    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fill(shot);

    }

    @Override
    public Shape getShape() {
        return shot;
    }

    /**
     * ends the shot
     * should be called when shot hits something
     */
    public void end() {
        ended=true;
    }

}
