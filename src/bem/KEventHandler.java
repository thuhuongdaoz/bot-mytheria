package bem;

import bem.event.IKEventListener;
import bem.event.KEvent;

public class KEventHandler implements IKEventListener {

	public boolean dispatch(KEvent event) throws KException {
		return false;
	}

}
