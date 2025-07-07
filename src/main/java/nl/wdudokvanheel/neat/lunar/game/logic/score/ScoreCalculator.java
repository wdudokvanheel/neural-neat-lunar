package nl.wdudokvanheel.neat.lunar.game.logic.score;


import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Level;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;

import static nl.wdudokvanheel.neat.lunar.game.logic.LunarGame.*;

public enum ScoreCalculator{
	// 500 for passing the waypoint
	PASS_WAYPOINT(500, (level, lander) -> {
		// Get the relative positions (regardless whether the level is flipped or not) so the waypoint is always to the right of the player
		Vector2d waypoint = getRelativePosition(level, level.waypoint);
		Vector2d position = getRelativePosition(level, lander.getCenterPosition(), LANDER_WIDTH);

		// If lander is still before the waypoint
		if(position.x < waypoint.x){
			// Get horizontal distance to from waypoint
			double maxDistX = waypoint.x;
			double xDist = Math.abs(waypoint.x - position.x);
			double horizontalScore = (1 - xDist / maxDistX) * 2;

			// Get max vertical distance from the starting pad to the waypoint
			double maxDistY = level.start.start.y - waypoint.y;
			// Get current vertical distance
			double yDist = Math.max(0, position.y - waypoint.y);
			double verticalScore = (1 - Math.min(1, yDist / maxDistY));

			// Combine scores
			return (horizontalScore + verticalScore) / 3;
		}
		// Return max score if the lander
		return 1;
	}),

	// Get max 500 points for reaching the target
	REACH_TARGET(750, (level, lander) -> {
		/*
		 * Get the relative positions (regardless whether the level is flipped or not) so the waypoint & target is always to
		 * the right of the player
		 */
		Vector2d position = getLandersBestRelativePosition(level, lander);
		Vector2d waypoint = getRelativePosition(level, level.waypoint);
		Vector2d target = getRelativePosition(level, level.target.start.add(PLATFORM_WIDTH / 2, -LANDER_HEIGHT/2));

		// If lander is still before the waypoint,
		if(position.x < waypoint.x){
			return 0;
		}

		double maxDistX = target.x - waypoint.x;
		double xDist = Math.abs(position.x - target.x);
		double horizontalScore = 1 - xDist / maxDistX;

		double maxDistY = level.target.start.y - LANDER_HEIGHT / 2;
		double yDist = Math.abs(position.y + LANDER_HEIGHT / 2 - target.y);
		double verticalScore = 1 - yDist / maxDistY;

		// Combine scores
		return (horizontalScore + verticalScore) / 2;
	}),

	// Score based on the (near) impact conditions (speed & angle) with the target

	TARGET_IMPACT(200, (level, lander) -> {
		if(lander.deathCollision != null && lander.deathCollision.line == level.target){
			return 1;
		}
		return 0;
	}),

	// Score the impact speed
	TARGET_IMPACT_SPEED(500, (level, lander) -> {
		//Check if the lander got a close enough to the target
		if(lander.closestPositionToTarget == null){
			return 0;
		}

		return 1 - Math.min(1, lander.closestPositionToTarget.speed / 25d);
	}),

	// Score the impact angle
	TARGET_IMPACT_ANGLE(250, (level, lander) -> {
		//Check if the lander got a close enough to the target
		if(lander.closestPositionToTarget == null){
			return 0;
		}

		return 1 - Math.abs(lander.closestPositionToTarget.angle) / 180d;
	}),

	REACH_GOAL(250, (level, lander) -> lander.reachedGoal ? 1 : 0),

	FUEL_REMAINING(100, (level, lander) -> lander.fuel / LANDER_FUEL_TANK);

	// Enum implementation
	private ScoreCalculatorFunction calculator;
	private double weight;

	ScoreCalculator(double weight, ScoreCalculatorFunction calculator){
		this.weight = weight;
		this.calculator = calculator;
	}

	public double calc(Level level, Lander lander){
		return this.calculator.calculate(level, lander);
	}

	public double getWeight(){
		return weight;
	}

	// Helper functions

	private static int getStartingEdge(Level level){
		if(level.start.start.x < level.target.start.x){
			return 0;
		}

		return GAME_WIDTH;
	}

	private static Vector2d getLandersBestRelativePosition(Level level, Lander lander){
		// Check if a closer position has been recorded during the lander's lifetime, use that instead of the current position
		if(lander.closestPositionToTarget != null){
			return getRelativePosition(level, lander.closestPositionToTarget.position.add(LANDER_WIDTH / 2, LANDER_HEIGHT), LANDER_WIDTH);
		}

		return getRelativePosition(level, lander.getCenterPosition(), LANDER_WIDTH);
	}

	private static Vector2d getRelativePosition(Level level, Vector2d position){
		return getRelativePosition(level, position, 0);
	}

	private static Vector2d getRelativePosition(Level level, Vector2d position, int width){
		if(level.start.start.x < level.target.start.x){
			return position.clone();
		}

		return position.clone().setX(GAME_WIDTH - position.x - width);
	}

	public static double getTotalWeight(){
		double total = 0;
		for(ScoreCalculator calc : values()){
			total += calc.getWeight();
		}
		return total;
	}
}

interface ScoreCalculatorFunction{
	double calculate(Level level, Lander lander);
}
