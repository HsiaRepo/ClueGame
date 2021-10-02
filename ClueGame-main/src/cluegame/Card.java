package cluegame;

import java.util.ArrayList;

public class Card {

	public String cardName;
	public CardType type;
	
	public Card(String cName, CardType cType) {
		super();
		this.cardName = cName;
		this.type = cType;
	}
	
	public String getName() {
		return cardName;
	}
	
	public CardType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Card [cardName=" + cardName + ", type=" + type + "]";
	}
	
	// get all cards of a specific card type
	public static ArrayList<Card> getCardsOfType(ArrayList<Card> cards, CardType type) {
		ArrayList<Card> ret = new ArrayList<Card>();
		// look at every card and add it to the return array if its the correct type
		for(Card c : cards) {
			if(c.type == type) {
				ret.add(c);
			}
		}
		return ret;
	}
}
