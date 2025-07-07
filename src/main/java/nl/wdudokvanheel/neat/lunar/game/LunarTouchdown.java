package nl.wdudokvanheel.neat.lunar.game;


import nl.wdudokvanheel.neat.lunar.game.level.SingleMountainLevel;
import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neat.lunar.game.ui.LunarWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LunarTouchdown implements KeyListener{
	private Logger logger = LoggerFactory.getLogger(LunarTouchdown.class);

	private LunarWindow lunarWindow;

	private LunarGame game;
	private Lander player;

	public static void main(String[] args){
		new LunarTouchdown();
	}

	public LunarTouchdown(){
		logger.debug("Starting Lunar Touchdown");

		game = new LunarGame(new SingleMountainLevel());
		player = game.addLander();

		lunarWindow = new LunarWindow();
		lunarWindow.setGame(game);
		lunarWindow.addKeyListener(this);

		double lastUpdate = System.currentTimeMillis();

		while(true){
			game.update();
			String debugText = "Score: " + player.score;

			if(!player.alive){
				lunarWindow.setTitle("Lunar Touchdown :: GAME OVER");
				debugText += "\nGAME OVER";
			}
			else if(player.reachedGoal){
				lunarWindow.setTitle("Lunar Touchdown :: Win");
				debugText += "\nWIN";
			}
			else {
				debugText += "\nSpeed: " + player.speed.magnitude();
				Vector2d position = player.position.add(new Vector2d(LunarGame.LANDER_WIDTH, LunarGame.LANDER_HEIGHT).scale(0.5));
				Vector2d delta = position.subtract(game.level.target.start.add(LunarGame.PLATFORM_WIDTH / 2, 0));
				debugText += "\nX: " + delta.x / LunarGame.GAME_WIDTH;
				debugText += "\nY: " + delta.y / LunarGame.GAME_HEIGHT;
			}
			lunarWindow.setDebugText(debugText);
			lunarWindow.repaint();
			try {
				Thread.sleep((long) Math.max(0, (1000 / (60) - (System.currentTimeMillis() - lastUpdate))));
			} catch(InterruptedException e){
				throw new RuntimeException(e);
			}
			lastUpdate = System.currentTimeMillis();
		}
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			player.inputThrust = 1;
		}
		else if(e.getKeyCode() == KeyEvent.VK_D){
			player.inputSteering = 1;
		}
		else if(e.getKeyCode() == KeyEvent.VK_A){
			player.inputSteering = -1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			player.inputThrust = 0;
		}
		else if(e.getKeyCode() == KeyEvent.VK_D){
			player.inputSteering = 0;
		}
		else if(e.getKeyCode() == KeyEvent.VK_A){
			player.inputSteering = 0;
		}

		if(e.getKeyCode() == KeyEvent.VK_R){
			logger.debug("Restarting game");
			game = new LunarGame(new SingleMountainLevel());
			game.addLander();
			lunarWindow.setGame(game);
		}
	}

	@Override
	public void keyTyped(KeyEvent e){

	}
}
