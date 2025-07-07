package nl.wdudokvanheel.neat.lunar.game.model;

import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.neural.LanderDistanceSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static nl.wdudokvanheel.neat.lunar.game.logic.LunarGame.LANDER_HEIGHT;
import static nl.wdudokvanheel.neat.lunar.game.logic.LunarGame.LANDER_WIDTH;


public class Lander {
    private Random random = new Random();

    public boolean alive = true;
    public boolean reachedGoal = false;

    public Vector2d position = new Vector2d(0, 0);
    public Vector2d acceleration = new Vector2d();
    public Vector2d speed = new Vector2d(0, 0);
    public double distanceTraveled = 0;

    public double fuel = LunarGame.LANDER_FUEL_TANK;
    //Angle the lander is pointing at, 0 is straight up (negative y)
    public double angle = 0;
    public double thrust = 0;

    // Input: Value between 0...1 to determine amount of thrust the engine pushes out
    public double inputThrust = 0;
    // Input: Value between -1...1 determines which way the thrust force is applies horizontally
    public double inputSteering = 0;

    public double score = 0;
    public Collision deathCollision;
    public LanderDistanceSnapshot closestPositionToTarget;

    public List<Line> getLines() {
        List<Line> lines = new ArrayList<>();

        int x = (int) Math.floor(position.x);
        int y = (int) Math.floor(position.y);

        int legLength = 3;

        int landerWidth = LANDER_WIDTH - 1;
        int landerHeight = LANDER_HEIGHT - legLength - 1;

        // Center of the lander
        Vector2d center = new Vector2d(x + LANDER_WIDTH / 2.0, y + LANDER_HEIGHT / 2.0);

        // Main body
        addLineWithRotation(lines, center, angle, x + 3, y + 3, x + 3, y + landerHeight);
        addLineWithRotation(lines, center, angle, x + landerWidth - 3, y + 3, x + landerWidth - 3, y + landerHeight);
        addLineWithRotation(lines, center, angle, x + 3, y + landerHeight, x + landerWidth - 3, y + landerHeight);

        // Legs
        addLineWithRotation(lines, center, angle, x + 3, y + landerHeight, x, y + landerHeight + legLength);
        addLineWithRotation(lines, center, angle, x + landerWidth - 3, y + landerHeight, x + landerWidth, y + landerHeight + legLength);

        // Diagonal poles
        addLineWithRotation(lines, center, angle, x + 3, y + 3, x + landerWidth / 2, y);
        addLineWithRotation(lines, center, angle, x + landerWidth - 3, y + 3, x + landerWidth / 2, y);

        // Engine exhaust fire
        if (thrust > 0) {
            double thrustHeight = 2 + thrust * 6 + random.nextDouble(4);
            addLineWithRotation(lines, center, angle, x + 6, y + landerHeight, x + landerWidth / 2, y + landerHeight + thrustHeight);
            addLineWithRotation(lines, center, angle, x + landerWidth - 6, y + landerHeight, x + landerWidth / 2, y + landerHeight + thrustHeight);
        }

        return lines;
    }

    private void addLineWithRotation(List<Line> lines, Vector2d center, double angle, double startX, double startY, double endX, double endY) {
        lines.add(new Line(
                new Vector2d(startX, startY).rotate(center, angle),
                new Vector2d(endX, endY).rotate(center, angle)
        ));
    }

    public void setCenterPosition(Vector2d position) {
        setCenterPosition(position.x, position.y);
    }

    public void setCenterPosition(double x, double y) {
        this.position.x = x - LANDER_WIDTH / 2;
        this.position.y = y - LANDER_HEIGHT / 2;
    }

    public Vector2d getCenterPosition() {
        return position.add(LANDER_WIDTH / 2, LANDER_HEIGHT / 2);
    }
}
