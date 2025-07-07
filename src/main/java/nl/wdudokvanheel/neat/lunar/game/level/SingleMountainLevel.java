package nl.wdudokvanheel.neat.lunar.game.level;


import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.model.Level;
import nl.wdudokvanheel.neat.lunar.game.model.Line;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class SingleMountainLevel extends Level {

	@Override
	protected List<Line> createLevel(int width, int height){
		List<Line> lines = new ArrayList<>();

		int leftGroundElevation = height - 20 - 25;
		int rightGroundElevation = height - 20 - 25;

		int platformWidth = LunarGame.PLATFORM_WIDTH;
		int platformHeight = 6;

		int mountainPeakX = width / 2 - 5;
		int mountainPeakY = height / 2 - 5;

		int leftMountainBaseX = width / 4;
		int leftMountainBaseY = leftGroundElevation;
		int rightMountainBaseX = 3 * width / 4;
		int rightMountainBaseY = rightGroundElevation;

		int leftPlatformX = leftMountainBaseX / 2 - platformWidth / 2;
		int leftPlatformY = leftGroundElevation - platformHeight;

		int rightPlatformX = rightMountainBaseX + (width - rightMountainBaseX - platformWidth) / 2;
		int rightPlatformY = rightGroundElevation - platformHeight;

		waypoint = new Vector2d(mountainPeakX, mountainPeakY - LunarGame.LANDER_HEIGHT);
		start = new Line(leftPlatformX, leftPlatformY, leftPlatformX + LunarGame.PLATFORM_WIDTH, leftPlatformY);
		target = new Line(rightPlatformX, rightPlatformY, rightPlatformX + LunarGame.PLATFORM_WIDTH, rightPlatformY);

//		if(random.nextBoolean()){
//			start = target;
//			target = new Line(leftPlatformX, leftPlatformY, leftPlatformX + LunarGame.PLATFORM_WIDTH, leftPlatformY);
//		}

		// Left ground
		lines.add(new Line(0, leftGroundElevation, leftMountainBaseX, leftGroundElevation));

		// Mountain left side
		lines.add(new Line(leftMountainBaseX, leftMountainBaseY, mountainPeakX, mountainPeakY));

		// Mountain right side
		lines.add(new Line(mountainPeakX, mountainPeakY, rightMountainBaseX, rightMountainBaseY));

		// Left platform poles
		lines.add(new Line(leftPlatformX + 7, leftPlatformY + 1, leftPlatformX + 7, leftGroundElevation));
		lines.add(new Line(leftPlatformX + 28, leftPlatformY + 1, leftPlatformX + 28, leftGroundElevation));

		// Right platform poles
		lines.add(new Line(rightPlatformX + 7, rightPlatformY + 1, rightPlatformX + 7, rightGroundElevation));
		lines.add(new Line(rightPlatformX + 28, rightPlatformY + 1, rightPlatformX + 28, rightGroundElevation));

		// Right ground
		lines.add(new Line(rightMountainBaseX, rightGroundElevation, width, rightGroundElevation));

		// Left edge
		lines.add(new Line(0, leftGroundElevation, 0, 0));
		// Top edge
		lines.add(new Line(0, 0, LunarGame.GAME_WIDTH - 1, 0));
		// Right edge
		lines.add(new Line(LunarGame.GAME_WIDTH - 1, 0, LunarGame.GAME_WIDTH - 1, rightGroundElevation));

		return lines;
	}
}
