package net.k2tek.gamb;


public class GambActionControl {
	private GambActionHandler handler;
	//
	private GambAction action1;
	private GambAction action2;
	
	public GambActionControl(GambActionHandler handler) {
		this.handler = handler;
	}
	
	public void setAction(GambAction[] actions){
		if (actions.length == 1){
			action1 = null;
			action2 = actions[0];
		}else if (actions.length == 2){
			action1 = actions[0];
			action2 = actions[1];
		}else {
			clear();
		}
	}
	
	public void clear(){
		action1 = null;
		action2 = null;
	}
	
	
}