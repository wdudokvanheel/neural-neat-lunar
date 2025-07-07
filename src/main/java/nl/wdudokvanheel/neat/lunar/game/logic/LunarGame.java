package nl.wdudokvanheel.neat.lunar.game.logic;

import nl.wdudokvanheel.neat.lunar.game.logic.score.Scoring;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Level;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LunarGame{
	private Logger logger = LoggerFactory.getLogger(LunarGame.class);
	private final ExecutorService executor = Executors.newFixedThreadPool(10);

	public static int GAME_WIDTH = 512;
	public static int GAME_HEIGHT = 512;
	public static int PLATFORM_WIDTH = 35;

	//Lander properties
	public static double SENSOR_LENGTH = 60;
	public static int LANDER_WIDTH = 15;
	public static int LANDER_HEIGHT = 14;
	public static double LANDER_MASS = 2;
	public static double LANDER_THRUST_POWER = 75;
	public static double LANDER_TURNING_SPEED = 1.5;
	public static double LANDER_FUEL_TANK = 60 * 30;
	public static double MAX_IMPACT_FORCE = 1;

	//World physics
	public static double GRAVITY = 9.81;
	public static double DRAG_COEFFICIENT_X = 0.1;
	public static double DRAG_COEFFICIENT_Y = 0.01;

	public static Vector2d[] DIRECTIONS = new Vector2d[]{
			new Vector2d(0, -1),                // N
			new Vector2d(1, -1).normalize(),    // NE
			new Vector2d(1, 0),                 // E
			new Vector2d(1, 1).normalize(),     // SE
			new Vector2d(0, 1),                 // S
			new Vector2d(-1, 1).normalize(),    // SW
			new Vector2d(-1, 0),                // W
			new Vector2d(-1, -1).normalize()    // NW
	};

	public static double MAX_HEIGHT_RANGE_FOR_SNAPSHOT = 10;

	public int frame = 0;
	public boolean running = true;
	public Level level;
	public List<Lander> landers = new ArrayList<>();

	public LunarGame(Level level){
		this.level = level;
	}

	/**
	 * Update the game world, moving all Landers based on their input and detect & resolve collisions.
	 */
	public void update(){
		frame++;

		if(!running){
			return;
		}

		boolean hasUpdatedLander = false;
		for(Lander lander : landers){
			if(!lander.alive || lander.reachedGoal){
				continue;
			}
			hasUpdatedLander = true;
//			lander.score = getLanderScore(lander);

			handleSteeringInput(lander);
			handleThrustInput(lander);
			Physics.updateLanderPhysics(lander);
			CollisionDetection.perform(level, lander);
			Scoring.saveTargetDistanceSnapshot(level, lander);
			lander.score = Scoring.score(level, lander);
		}
		running = hasUpdatedLander;
	}

	private double getLanderScore(Lander lander){
		double score;

		Vector2d waypoint = level.waypoint;
		double wallX = 0;
		double wayX = waypoint.x;
		double x = lander.position.x + LANDER_WIDTH / 2;
		double y = lander.position.y;
		double targetX = level.target.start.x + PLATFORM_WIDTH / 2;

		if(level.start.start.x > waypoint.x){
			x = GAME_WIDTH - x;
			wayX = GAME_WIDTH - wayX;
			wallX = GAME_WIDTH;
			targetX = GAME_WIDTH - targetX;
		}

		//If lander is still before the waypoint
		if(x < wayX){
			double maxDistX = Math.abs(wallX - waypoint.x);
			double xDist = Math.abs(wayX - x);
			double xscore = 250 - (xDist / maxDistX) * 250;


			double maxDistY = level.start.start.y - waypoint.y;
			double yDist = Math.max(0, y - waypoint.y);
			double yscore = 250 - (yDist / maxDistY) * 250;

			score = xscore + yscore;
		}
		else {
			//Lander passed the waypoint, so give max points for the first part
			score = 500;

			double maxDistX = Math.abs(wayX - targetX);
			double xDist = Math.abs(x - targetX);
			score += 250 - (xDist / maxDistX) * 250;

			double maxDistY = level.target.start.y;
			double yDist = Math.abs(y - level.target.start.y + LANDER_HEIGHT);
			score += 250 - (yDist / maxDistY) * 250;
		}
		return score;
	}

	private void handleSteeringInput(Lander lander){
		if(lander.inputSteering != 0){
			lander.angle += lander.inputSteering * LANDER_TURNING_SPEED;
			lander.angle = lander.angle % 360;
		}
	}

	private void handleThrustInput(Lander lander){
		lander.thrust = lander.inputThrust;
	}

	public Lander addLander(){
		Lander lander = new Lander();
		addLander(lander);
		return lander;
	}

	public void addLander(Lander lander){
		lander.position = new Vector2d(level.start.start.x + (PLATFORM_WIDTH - LANDER_WIDTH) / 2, level.start.start.y - LANDER_HEIGHT);
//		lander.position = new Vector2d(level.target.start.x + (PLATFORM_WIDTH - LANDER_WIDTH) / 2, level.target.start.y - LANDER_HEIGHT - 50);
		landers.add(lander);
	}
}
