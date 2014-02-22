package levelmodel;

import java.util.Stack;

/**
 * History of LevelStates. To support undo operations.
 * 
 * TODO: very simple now, add redo operation?
 */
public class LevelStateHistory {
	private Stack<LevelState> history;
	
	public LevelStateHistory(LevelState initialState) {
		history = new Stack<LevelState>();
		history.push(initialState);
	}
	
	public void newState() {
		System.out.println("Save undo state.");
		history.push(new LevelState(getCurrentState()));
	}
	
	public void newState(LevelState newState) {
		System.out.println("Save undo state.");
		history.push(newState);
	}
	
	public void undo() {
		if (history.size() <= 1) {
			System.out.println("No more undos.");
			return;
		}
		System.out.println("Undo.");
		history.pop();
	}
	
	public LevelState getCurrentState() {
		return history.peek();
	}
}
