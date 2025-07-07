package nl.wdudokvanheel.neat.lunar.game.ui;

import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Visual representation of a LunarGame instance
 */
public class LunarWindow extends JFrame{
	private static Logger logger = LoggerFactory.getLogger(LunarWindow.class);

	private GameRenderPanel gameRenderPanel;

	public LunarWindow(){
		setTitle("Lunar Touchdown");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
		gameRenderPanel = new GameRenderPanel(LunarGame.GAME_WIDTH, LunarGame.GAME_HEIGHT);
		add(gameRenderPanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	public void setGame(LunarGame game){
		this.gameRenderPanel.setGame(game);
	}

	public void setDebugText(String text){
		this.gameRenderPanel.debugText = text;
	}
}

