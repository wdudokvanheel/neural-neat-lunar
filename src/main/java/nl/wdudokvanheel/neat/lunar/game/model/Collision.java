package nl.wdudokvanheel.neat.lunar.game.model;

public class Collision{
	public Lander lander;
	public Line line;
	public double impact;
	public double angleDifference;

	public Collision(Lander lander, Line line, double impact, double angleDifference){
		this.lander = lander;
		this.line = line;
		this.impact = impact;
		this.angleDifference = angleDifference;
	}
}
