package nl.wdudokvanheel.neat.lunar.game.ui;

import nl.wdudokvanheel.neat.lunar.game.logic.CollisionDetection;
import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Line;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class GameRenderPanel extends JPanel{
	private Logger logger = LoggerFactory.getLogger(GameRenderPanel.class);
	private static Color BG_COLOR = Color.decode("#1d1d1d");
	private static Color LINE_COLOR = Color.decode("#eaeaea");
	private static Color START_COLOR = Color.decode("#6c0dcf");
	private static Color END_COLOR = Color.decode("#11cf20");
	private static Color RED = Color.decode("#cf0f4c");

	private final Color[] LANDER_COLORS = {
			Color.decode("#6c0dcf"),
			Color.decode("#134ccf"),
			Color.decode("#bf8bf6"),
			Color.decode("#8badf6"),
			Color.decode("#14cfb8"),
			Color.decode("#8cf6ea"),
			Color.decode("#11cf20"),
			Color.decode("#8bf694"),
			Color.decode("#98cf10"),
			Color.decode("#d8f68b"),
	};

	private int panelWidth;
	private int panelHeight;
	private BufferedImage buffer;
	private LunarGame game;
	public String debugText = "";

	public GameRenderPanel(int panelWidth, int panelHeight){
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		buffer = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
		setPreferredSize(new Dimension(panelWidth * 2, panelHeight * 2));
	}

	private void renderGameToImage(BufferedImage image){
		if(game == null){
			return;
		}
		Graphics2D g = image.createGraphics();

		g.setColor(BG_COLOR);
		g.fillRect(0, 0, panelWidth, panelHeight);

		renderLevel(g);
		renderLanders(g);

		g.dispose();
	}

	private void renderLanders(Graphics2D g){
		List<Lander> landers = game.landers.stream().filter(c -> c.alive).collect(Collectors.toList());
		for(int i = 0; i < landers.size(); i++){
			Lander lander = landers.get(i);
//			g.drawString("Score: " + lander.score, 10, 36);

			if(drawDebugLines(lander)){
				Vector2d center = lander.position.add(LunarGame.LANDER_WIDTH / 2, LunarGame.LANDER_HEIGHT / 2);
				for(int j = 0; j < LunarGame.DIRECTIONS.length; j++){
					Vector2d direction = LunarGame.DIRECTIONS[j];
					Vector2d target = center.floor().add(direction.scale(LunarGame.SENSOR_LENGTH)).rotate(center, lander.angle);
					double relativeLength = CollisionDetection.getRelativeTargetPosition(game.level.getAllLines(), center, target);
					g.setColor(blendColors(RED, END_COLOR, Math.abs(relativeLength)));
//				g.drawString("i: " + relativeLength, 10, 20 + j * 12);
					drawLine(g,
							center,
							CollisionDetection.getTargetPosition(center, target, relativeLength)
					);
				}
			}

			g.setColor(LANDER_COLORS[game.landers.indexOf(lander) % LANDER_COLORS.length] );
			drawLines(g, lander.getLines());
		}
	}

	private boolean drawDebugLines(Lander lander){
		return false;
	}

	private void renderLevel(Graphics2D g){
		g.setColor(LINE_COLOR);

		drawLines(g, game.level.lines);

		g.setColor(START_COLOR);
		drawLine(g, game.level.start);
		g.setColor(END_COLOR);
		drawLine(g, game.level.target);
	}

	private void drawLines(Graphics2D g, List<Line> lines){
		for(Line line : lines){
			drawLine(g, line);
		}
	}

	private void drawLine(Graphics2D g, Line line){
		drawLine(g, line.start, line.end);
	}

	private void drawLine(Graphics2D g, Vector2d start, Vector2d end){
		g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		renderGameToImage(buffer);
		g.drawImage(buffer, 0, 0, panelWidth * 2, panelHeight * 2, null);

		g.setColor(Color.RED);
		String[] split = debugText.split("\n");
		for(int i = 0; i < split.length; i++){
			g.drawString(split[i], 10, 24 + i * 14);
		}

//		Lander lander = game.landers.get(0);
//		String debug =
//				"Angle: " + lander.angle + " Lander accel: " + lander.acceleration.x + "," + lander.acceleration.y + " speed: " + lander.speed.x + "," + lander.speed.y + " " +
//				"pos: " + lander.position.y;
//		g.setColor(Color.red);
//		g.drawString(debug, 10, 24);
//
//		g.drawString("Thrust i: " + lander.inputThrust,  10,36);
//		g.drawString("Thrust r: " + lander.thrust,  10,48);
//		g.drawString("Steering: " + lander.inputSteering, 10, 60);
	}

	public void setGame(LunarGame game){
		this.game = game;
	}

	public static Color blendColors(Color color1, Color color2, double blendRate){
		if(blendRate < 0 || blendRate > 1){
			throw new IllegalArgumentException("Blend rate must be between 0 and 1");
		}

		double factor = Math.pow(blendRate, 2.5); // Exponential function for quicker color2 visibility

		int blendedRed = (int) (color1.getRed() * (1 - factor) + color2.getRed() * factor);
		int blendedGreen = (int) (color1.getGreen() * (1 - factor) + color2.getGreen() * factor);
		int blendedBlue = (int) (color1.getBlue() * (1 - factor) + color2.getBlue() * factor);
		int blendedAlpha = (int) (color1.getAlpha() * (1 - factor) + color2.getAlpha() * factor);

		return new Color(blendedRed, blendedGreen, blendedBlue, blendedAlpha);
	}
}
