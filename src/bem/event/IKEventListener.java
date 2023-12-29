package bem.event;

import bem.KException;

public interface IKEventListener {

	public boolean dispatch(KEvent event) throws KException;
	
}
