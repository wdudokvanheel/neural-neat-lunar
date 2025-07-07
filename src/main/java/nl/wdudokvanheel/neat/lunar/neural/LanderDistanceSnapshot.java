package nl.wdudokvanheel.neat.lunar.neural;

import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;

public class LanderDistanceSnapshot {
    public Vector2d position;
    public double distance;
    public double speed;
    public double angle;

    public LanderDistanceSnapshot(Vector2d position, double distance, double speed, double angle) {
        this.position = position;
        this.distance = distance;
        this.speed = speed;
        this.angle = angle;
    }
}
