package data;

import java.util.ArrayList;

public class HandDeckLayout {
	
	    private ArrayList<HandCard> lstCard = new ArrayList<HandCard>();
	 
	    public ArrayList<HandCard> GetListCard()
	    {
	       
	            return lstCard;
	    }

	    public void AddNewCard(HandCard card)
	    {
	        lstCard.add(card);
	    }

	    public void RemoveCard(HandCard card)
	    {
	        lstCard.remove(card);
//	        ReBuildDeck(index);
	    }

	  
	    public void ReBuildDeck(int index)
	    {

	    }


}
