package nl.wdudokvanheel.neat.lunar.game.logic.score;


import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Level;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neat.lunar.neural.LanderDistanceSnapshot;

public class Scoring{
	public static double score(Level level, Lander lander){
		double score = 0;
		for(ScoreCalculator item : ScoreCalculator.values()){
			score += item.getWeight() * item.calc(level, lander);
		}

		return score;
	}

	public static void saveTargetDistanceSnapshot(Level level, Lander lander){
		Vector2d target = level.getTargetCenter();
		double distance = target.y - lander.getCenterPosition().add(0, LunarGame.LANDER_HEIGHT / 2).y;

		// Check if lander is in range of the target, if so, update the closes position snapshot if the current distance is less than
		// the previous closest distance
		if(distance <= LunarGame.MAX_HEIGHT_RANGE_FOR_SNAPSHOT
				&& (lander.closestPositionToTarget == null || distance < lander.closestPositionToTarget.distance)){

			lander.closestPositionToTarget = new LanderDistanceSnapshot(
					lander.position.clone(), distance,
					lander.speed.magnitude(),
					lander.angle
			);
		}
	}
}

