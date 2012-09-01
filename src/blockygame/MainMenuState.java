package blockygame;

import java.util.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenuState extends BasicGameState {
	private int stateID = -1;
	
	private Image background, title;
	
	private List<Image> menuIddleImgs, menuHighlightedImgs;
	private List<Rectangle> menuItemsPositions;
	private int menuItemHighlighted = -1;
	
	private static final int START = 0;
	private static final int HIGHSCORES = 1;
	private static final int EXIT = 2;
	
	MainMenuState(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		background = new Image("data/background.png");
		title = new Image("data/title.png");
		
		menuIddleImgs = new ArrayList<Image>();
		menuHighlightedImgs = new ArrayList<Image>();
		menuItemsPositions = new ArrayList<Rectangle>();
		
		int centerWidth = container.getWidth() / 2;
		int itemY = 290;
		
		for (int i = 0; i <= 2; i++) {
			Image img = new Image("data/menu_" + i + ".png");
			int imgX = centerWidth - img.getWidth() / 2;
			
			menuIddleImgs.add(img);
			menuHighlightedImgs.add(new Image("data/menu_" + i + "_h.png"));
			menuItemsPositions.add(new Rectangle(imgX, itemY, img.getWidth(), img.getHeight()));
			
			itemY += img.getHeight() + 30;
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		background.draw(0, 0);
		title.drawCentered(container.getWidth() / 2, 80);
				
		for (int i = 0; i < menuIddleImgs.size(); i++) {
			Image img = (menuItemHighlighted == i) ? menuHighlightedImgs.get(i) : menuIddleImgs.get(i);
			Rectangle rect = menuItemsPositions.get(i);
			
			img.draw(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		// Highlight menu item
		menuItemHighlighted = -1;
		for (int i = 0; i < menuItemsPositions.size(); i++) {
			Rectangle imageBox = menuItemsPositions.get(i);
			if (imageBox.contains(input.getMouseX(), input.getMouseY())) {
				menuItemHighlighted = i;
			}
		}
		
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			switch (menuItemHighlighted) {
			case START:
				game.enterState(BlockyGame.GAME_PLAY_STATE);
				break;
			case HIGHSCORES:
				break;
			case EXIT:
				container.exit();
				break;
			}
		}
	}

}
