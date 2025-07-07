package nl.wdudokvanheel.neat.lunar.game.logic;

import nl.wdudokvanheel.neat.lunar.game.model.*;
import nl.wdudokvanheel.neat.lunar.neural.LanderDistanceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollisionDetection{
	private static Logger logger = LoggerFactory.getLogger(CollisionDetection.class);

	public static void perform(Level level, Lander lander){
		//Check for collisions
		List<Collision> collisions = checkCollisionAndResolve(lander, lander.position, level.getAllLines());

		//Handle collision if there is one
		if(!collisions.isEmpty()){
			Collision collision = collisions.get(0);
			List<Collision> targetCollisions = collisions.stream().filter(col -> col.line == level.target).collect(Collectors.toList());
			if(!targetCollisions.isEmpty()){
				collision = targetCollisions.get(0);
			}

			handleCollision(level, collision);
		}
	}

	private static void handleCollision(Level level, Collision collision){
		Lander lander = collision.lander;

		lander.speed.x = 0;
		lander.speed.y = 0;
		lander.acceleration.y = 0;
		lander.acceleration.x = 0;
		lander.position = lander.position.floor();

		if(collision.impact > LunarGame.MAX_IMPACT_FORCE || collision.angleDifference > 1){
//			logger.debug("Impact force/angle too big: {} {}°", collision.impact, collision.angleDifference);
			lander.deathCollision = collision;
			lander.alive = false;
			lander.thrust = 0;
			lander.inputSteering = 0;
			lander.inputThrust = 0;

			lander.closestPositionToTarget = new LanderDistanceSnapshot(lander.position.clone(), 0, lander.speed.magnitude(), lander.angle);
		}

		if(collision.line == level.target){
			lander.score += ((180 - collision.angleDifference) / 180) * 250;
			lander.score += ((25 - Math.max(0, collision.impact - LunarGame.MAX_IMPACT_FORCE)) / 25) * 250;

			lander.thrust = 0;
			lander.inputSteering = 0;
			lander.inputThrust = 0;

			if(lander.alive){
				lander.reachedGoal = true;
			}

			lander.closestPositionToTarget = new LanderDistanceSnapshot(lander.position.clone(), 0, lander.speed.magnitude(), lander.angle);
		}
	}

	public static List<Collision> checkCollisionAndResolve(Lander lander, Vector2d newPosition, List<Line> lines){
		ArrayList<Collision> collisions = new ArrayList<>();
		// Create a bounding box around the lander using its top-left position
		double leftX = newPosition.x;
		double rightX = newPosition.x + LunarGame.LANDER_WIDTH;
		double topY = newPosition.y;
		double bottomY = newPosition.y + LunarGame.LANDER_HEIGHT;

		// Check each line for collisions
		for(Line line : lines){
			// Check if any part of the bounding box intersects the line
			if(line.intersectsLine(leftX, topY, rightX, topY) ||
					line.intersectsLine(leftX, topY, leftX, bottomY) ||
					line.intersectsLine(leftX, bottomY, rightX, bottomY) ||
					line.intersectsLine(rightX, topY, rightX, bottomY)){

				// Find the closest point on the line to the lander's new position
				Vector2d landerCenter = new Vector2d(newPosition.x + LunarGame.LANDER_WIDTH / 2, newPosition.y + LunarGame.LANDER_HEIGHT / 2);
				Vector2d closestPoint = getClosestPointOnLine(line, landerCenter);

				// Calculate the overlap and adjust the position accordingly
				double dx = landerCenter.x - closestPoint.x;
				double dy = landerCenter.y - closestPoint.y;
				double distance = Math.sqrt(dx * dx + dy * dy);
				double overlap = (Math.max(LunarGame.LANDER_WIDTH, LunarGame.LANDER_HEIGHT) / 2) - distance;

				if(overlap > 0){
					double directionX = dx / distance;
					double directionY = dy / distance;
					newPosition.x += directionX * overlap;
					newPosition.y += directionY * overlap;
				}

				// Calculate the angle of the line
				double lineDx = line.end.x - line.start.x;
				double lineDy = line.end.y - line.start.y;
				double lineAngle = Math.atan2(lineDy, lineDx);

				// Calculate the angle difference between the lander and the line
				double landerAngleRad = Math.toRadians(lander.angle);
				double angleDifference = Math.abs(lineAngle - landerAngleRad);

				// Normalize angle difference to be between 0 and 180 degrees
				angleDifference = Math.toDegrees(angleDifference);
				angleDifference = angleDifference > 180 ? 360 - angleDifference : angleDifference;

				// Calculate impact force based on overlap and speed
				double impactForce = Math.abs(overlap * lander.speed.y);
				collisions.add(new Collision(lander, line, impactForce, angleDifference));
			}
		}

		return collisions;
	}

	private static Vector2d getClosestPointOnLine(Line line, Vector2d point){
		double xDelta = line.end.x - line.start.x;
		double yDelta = line.end.y - line.start.y;

		if((xDelta == 0) && (yDelta == 0)){
			throw new IllegalArgumentException("Line start and end points are the same");
		}

		double u = ((point.x - line.start.x) * xDelta + (point.y - line.start.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		final Vector2d closestPoint;
		if(u < 0){
			closestPoint = new Vector2d(line.start.x, line.start.y);
		}
		else if(u > 1){
			closestPoint = new Vector2d(line.end.x, line.end.y);
		}
		else {
			closestPoint = new Vector2d(line.start.x + u * xDelta, line.start.y + u * yDelta);
		}

		return closestPoint;
	}

	public static Vector2d getTargetPosition(List<Line> lines, Vector2d source, Vector2d target){
		double relativeCollisionPosition = getRelativeTargetPosition(lines, source, target);
		return getTargetPosition(source, target, relativeCollisionPosition);
	}

	public static Vector2d getTargetPosition(Vector2d source, Vector2d target, double relativePosition){
		double x = source.x + (target.x - source.x) * relativePosition;
		double y = source.y + (target.y - source.y) * relativePosition;

		return new Vector2d(x, y);
	}

	public static double getRelativeTargetPosition(List<Line> lines, Vector2d source, Vector2d target){
		double minRelativePosition = 1.0;

		for(Line line : lines){
			Vector2d intersection = lineIntersection(source, target, line.start, line.end);

			if(intersection != null){
				double relativePosition = getRelativePositionOnLine(source, target, intersection);

				if(relativePosition < minRelativePosition){
					minRelativePosition = relativePosition;
				}
			}
		}

		return minRelativePosition;
	}

	private static double getRelativePositionOnLine(Vector2d source, Vector2d target, Vector2d point){
		double lineLength = source.distance(target);
		double pointDistance = source.distance(point);
		return pointDistance / lineLength;
	}

	private static Vector2d lineIntersection(Vector2d p1, Vector2d p2, Vector2d q1, Vector2d q2){
		double det = (p2.x - p1.x) * (q2.y - q1.y) - (p2.y - p1.y) * (q2.x - q1.x);

		if(det == 0){
			return null; // Lines are parallel
		}

		double lambda = ((q2.y - q1.y) * (q2.x - p1.x) + (q1.x - q2.x) * (q2.y - p1.y)) / det;
		double gamma = ((p1.y - p2.y) * (q2.x - p1.x) + (p2.x - p1.x) * (q2.y - p1.y)) / det;

		if(lambda < 0 || lambda > 1 || gamma < 0 || gamma > 1){
			return null; // No intersection within line segments
		}

		// Intersection point
		return new Vector2d(p1.x + lambda * (p2.x - p1.x), p1.y + lambda * (p2.y - p1.y));
	}
}
