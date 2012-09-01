package blockygame;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class BlockyGame extends StateBasedGame {
	
	public static final int MAIN_MENU_STATE = 0;
	public static final int GAME_PLAY_STATE = 1;
	
	public BlockyGame() {
		super("Blocky");
	}
	
	@Override
    public void initStatesList(GameContainer gc) throws SlickException {
		addState(new MainMenuState(MAIN_MENU_STATE));
		addState(new GamePlayState(GAME_PLAY_STATE));
    }
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new BlockyGame());
		app.setDisplayMode(800, 600, false);
		app.start();
	}
	
}
