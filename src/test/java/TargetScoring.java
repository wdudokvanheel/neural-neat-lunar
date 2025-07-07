import com.bitechular.lunarlander.logic.score.ScoreCalculator;
import com.bitechular.lunarlander.model.Lander;
import com.bitechular.lunarlander.model.Level;
import com.bitechular.lunarlander.model.Vector2d;
import com.bitechular.neural.neat.example.lunar.LanderDistanceSnapshot;
import org.junit.jupiter.api.Test;

import static com.bitechular.lunarlander.logic.LunarGame.*;
import static org.junit.jupiter.api.Assertions.*;

public class TargetScoring{
	/* TEST LEVEL SETUP
	 * Start platform:	45-55, 100
	 * End platform:	245-255, 100
	 * Waypoint:		150, 50
	 */

	@Test
	public void testDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(0, level.start.start.y - LANDER_HEIGHT / 2);

		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertEquals(0, score);
	}

	@Test
	public void testDistanceFromAboeWaypoint(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(level.waypoint.x - LANDER_WIDTH / 2, 0 - LANDER_HEIGHT / 2);
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertEquals(0, score);
	}

	@Test
	public void testDistanceFromWaypoint(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(level.waypoint.x - LANDER_WIDTH / 2, level.waypoint.y - LANDER_HEIGHT / 2);
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertNotEquals(0, score);
		assertNotEquals(1, score);
	}

	@Test
	public void testOnTarget(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.setCenterPosition(getTargetCenterPosition(level));
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertEquals(1, score);
		// Check if the lander is actually positioned in the center of the target
		assertEquals(level.target.start.y - LANDER_HEIGHT, lander.position.y);
		assertEquals(level.target.start.x + PLATFORM_WIDTH / 2 - LANDER_WIDTH / 2, lander.position.x);
	}

	@Test
	public void testSlightAboveTarget(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.setCenterPosition(getTargetCenterPosition(level).add(0, -1));

		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertNotEquals(1, score);
		assertNotEquals(0, score);
		assertTrue(score > 0.99, "Score (" + score + ") should be near 1");
	}

	@Test
	public double test1PxBeforeTarget(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.setCenterPosition(getTargetCenterPosition(level).add(-1, 0));
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertNotEquals(1, score);
		assertNotEquals(0, score);
		assertTrue(score > 0.99, "Score (" + score + ") should be near 1");
		return score;
	}

	@Test
	public double test1PxAfterTarget(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.setCenterPosition(getTargetCenterPosition(level).add(1, 0));
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertNotEquals(1, score);
		assertNotEquals(0, score);
		assertTrue(score > 0.99, "Score (" + score + ") should be near 1");
		return score;
	}

	@Test
	public void testBeforeAfterScore(){
		assertEquals(test1PxBeforeTarget(), test1PxAfterTarget(), "1 pixel distance from target should be penalized the same for left and right side of " +
				"target");
	}

	@Test
	public void testSnapshotUsage(){
		Level level = new TestLevel();
		Lander lander = new Lander();
		lander.position = new Vector2d(level.start.start.x, level.start.start.y - LANDER_HEIGHT);
		double score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertEquals(0, score, "Score should be 0 as the lander is at the starting position");

		lander.closestPositionToTarget = new LanderDistanceSnapshot(getTargetCenterPosition(level).subtract(LANDER_WIDTH / 2, LANDER_HEIGHT), 0, 0, 0);
		score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertEquals(1, score, "Score should be 1 as the lander's closest snapshot was on the target");

		lander.closestPositionToTarget = new LanderDistanceSnapshot(getTargetCenterPosition(level).subtract(LANDER_WIDTH / 2, LANDER_HEIGHT).subtract(1, 1)
				, 0, 0, 0);
		score = ScoreCalculator.REACH_TARGET.calc(level, lander);
		assertNotEquals(1, score);
		assertTrue(score > 0.99, "Score (" + score + ") should be near 1 as the lander's closest snapshot was almost on the target");
	}

	private Vector2d getTargetCenterPosition(Level level){
		return new Vector2d(level.target.start.x + PLATFORM_WIDTH / 2, level.target.start.y - LANDER_HEIGHT / 2);
	}
}
