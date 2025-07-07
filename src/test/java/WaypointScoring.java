import com.bitechular.lunarlander.logic.score.ScoreCalculator;
import com.bitechular.lunarlander.model.Lander;
import com.bitechular.lunarlander.model.Level;
import com.bitechular.lunarlander.model.Vector2d;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bitechular.lunarlander.logic.LunarGame.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaypointScoring{
	private Logger logger = LoggerFactory.getLogger(WaypointScoring.class);

	/* TEST LEVEL SETUP
	 * Start platform:	45-55, 100
	 * End platform:	245-255, 100
	 * Waypoint:		150, 50
	 */

	@Test
	/**
	 * Test when position is exactly on the waypoint
	 */
	public void testOnWaypoint(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		// Position lander on top of waypoint, should give a score of 1
		lander.setCenterPosition(level.waypoint);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(1, score);
	}

	@Test
	public void testPassedWaypoint(){
		// Start @
		Level level = new TestLevel();
		Lander lander = new Lander();

		// Position lander on top of waypoint, should give a score of 1
		lander.position = new Vector2d(level.waypoint.x - LANDER_WIDTH / 2 + 1, 0);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(1, score);
	}


	@Test
	public void testMaxHorizontalDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(0 - LANDER_WIDTH / 2, 50 - LANDER_HEIGHT / 2);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(0.5, score);
	}

	@Test
	public void testCloseHorizontalDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(level.waypoint.x - LANDER_WIDTH / 2 - 1, level.waypoint.y - LANDER_HEIGHT / 2);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertTrue(score > 0.99, "Score (" + score + ") should be almost 1");
	}

	@Test
	public void testLowestVerticalDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(0 - LANDER_WIDTH / 2, (level.start.start.y - LANDER_HEIGHT / 2));
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(0, score, "Score (" + score + ") should be 0");
	}

	@Test
	public void testBeyondLowestVerticalDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(0 - LANDER_WIDTH / 2, (level.start.start.y - LANDER_HEIGHT / 2) + 1);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(0, score, "Score (" + score + ") should be 0");
	}

	@Test
	public void testPassedWaypointHeight(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		lander.position = new Vector2d(0 - LANDER_WIDTH / 2, 0);
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(0.5, score, "Score (" + score + ") should be 0.5");
	}

	@Test
	public void testMaxDistance(){
		Level level = new TestLevel();
		Lander lander = new Lander();

		// Position lander at max horizontal distance and 0 vertical distance, score should be 0.5
		lander.position = new Vector2d(-LANDER_WIDTH / 2, (level.start.start.y - LANDER_HEIGHT / 2));
		double score = ScoreCalculator.PASS_WAYPOINT.calc(level, lander);
		assertEquals(0, score);
	}
}
