package nl.wdudokvanheel.neat.lunar.game.model;


import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Level{
	protected Random random = new Random();
	public List<Line> lines;
	public Line start;
	public Line target;
	public Vector2d waypoint;

	public Level(){
		lines = createLevel(LunarGame.GAME_WIDTH, LunarGame.GAME_HEIGHT);
	}

	protected abstract List<Line> createLevel(int width, int height);

	/**
	 * Get a list of all the lines of the level, including the start and end lines
	 */
	public List<Line> getAllLines(){
		ArrayList<Line> allLines = new ArrayList<>(lines);
		allLines.add(start);
		allLines.add(target);
		return allLines;
	}

	public Vector2d getTargetCenter(){
		return target.start.add(LunarGame.PLATFORM_WIDTH / 2, 0);
	}
}
