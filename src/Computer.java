
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;

public class Computer extends Player {
	
	public Computer() {
		super("Computer");
	}
	
	// Enter character and add it to the HashMap
		public Pair<HashMap<Character, Point>, Player> enterPosition(HashMap<Character, Point> pointmap,
			Player opponent, int phase) {
			System.out.println("*** 'NU IS HET MIJN BEURT' ***");

			//Let the computer think of a move
			String computermove = letMeThink(pointmap, opponent, phase);
			
			// PHASE 1:
			if (phase == 1) {

				char letter = computermove.charAt(0);
				
				// Add given value to HashMap
				pointmap.get(letter).Value = Color.charAt(0);

				// Remove point from invertory and add to playfield
				Pieces--;
				PiecesOnBoard++;

				HashMap<Character, Point> temporarymap = isMill(pointmap, letter);
				if (temporarymap != null) {
					// Excecute when a mill is placed
					pointmap = temporarymap;
					//Check if opponent has only mills left
					boolean onlyMills = onlyMills(pointmap, opponent);
					// Actie maken wanneer molentje is gemaakt
					if (canDelete(pointmap, opponent, onlyMills)) {
						Pair<HashMap<Character, Point>, Player> takeAwayPair = null;
						while (takeAwayPair == null) {
							char takeawayletter = letMeDecedeAHit(pointmap, opponent, phase);
							takeAwayPair = takeFromBoard(pointmap, takeawayletter, opponent, true);
							System.out.println("*** 'HAHAHA >:-) IK HEB JE: " + Character.toString(takeawayletter)+ " GEPAKT' ***");
						}
						if (takeAwayPair.getKey().get('A').Name != "SKIP") {
							opponent = takeAwayPair.getValue();
							pointmap = takeAwayPair.getKey();
						}
					}
				}
				System.out.println("*** DE COMPUTER PLAATST ZIJN PION OP: " + letter + " ***"); 
			} // END PHASE 1

			// PHASE 2:
			if (phase == 2) {
																	
				String movestring = computermove;
				
				pointmap = movePoint(pointmap, movestring);
				
				System.out.println("*** DE COMPUTER VERZET: " + movestring.charAt(0) +  " NAAR: " + movestring.charAt(1) + " ***");
				
				HashMap<Character, Point> temporarymap = isMill(pointmap, movestring.toUpperCase().charAt(1));
				if (temporarymap != null) {
					// Excecute when a mill is placed
					pointmap = temporarymap;
					//Check if opponent has only mills left
					boolean onlyMills = onlyMills(pointmap, opponent);
					// Actie maken wanneer molentje is gemaakt
					if (canDelete(pointmap, opponent, onlyMills)) {
						Pair<HashMap<Character, Point>, Player> takeAwayPair = null;
						while (takeAwayPair == null) {
							char takeawayletter = letMeDecedeAHit(pointmap, opponent, phase);
							takeAwayPair = takeFromBoard(pointmap, takeawayletter, opponent, true);
							System.out.println("*** 'HAHAHA >:-) IK HEB JE: " + Character.toString(takeawayletter)+ " GEPAKT' ***");
							
						}
						if (takeAwayPair.getKey().get('A').Name != "SKIP") {
							opponent = takeAwayPair.getValue();
							pointmap = takeAwayPair.getKey();
						}
						
					} 
				}
				
				//Check if opponent can make a move
				if(!canMove(pointmap, opponent))
				{
					//if player cant move, opponent has won!
					opponent.Pieces = 0;
					opponent.PiecesOnBoard = 2;
					System.out.println(opponent.returnName() + ", je kunt niet meer bewegen. Ik heb gewonnen! ");
					Pair<HashMap<Character, Point>, Player> returnPair = new Pair<>(pointmap, opponent);
					return returnPair;
				}
			} // END PHASE 2
				
			//Show pieces in hand and on board
			System.out.println(Name + ": Pionnen in hand: " + Pieces + " Pionnen op bord: " + PiecesOnBoard);
			
			Pair<HashMap<Character, Point>, Player> returnPair = new Pair<>(pointmap, opponent);
			return returnPair;
		}
	
		//Let the computer think of a move
		private String letMeThink(HashMap<Character, Point> pointmap, Player opponent, int phase){
			
			List<String> currentPoints = new ArrayList<String>();
			
			//Fill array with current owned points
			for(char c = 'A'; c <= 'X'; c++)
				if(pointmap.get(c).Value == Color.charAt(0))
					currentPoints.add(Character.toString(c));	
			
			if(!finalPhase && Pieces == 0 && PiecesOnBoard == 3)
			{
				finalPhase = true;
				System.out.println("*** JE HEBT ME BIJNA VERSLAGEN >:-( ");
			}
			
			if(phase == 1)
			{
				return Character.toString(firstPhase(pointmap, currentPoints, opponent));	
			}
			else if(phase == 2) {
				return secondPhase(pointmap, currentPoints, opponent);
			}
			return "A";
		}
		
		//Return position to either block the opponent or to help yourself
		private char firstPhase(HashMap<Character, Point> pointmap, List<String> currentPoints, Player opponent) {
			
			//List of empty points
			List<String> emptyPoints = getEmptyPoints(pointmap);		
			
			//If there are no current points, return random empty point
			if(currentPoints.size() == 0) {
				//Random number
				Random random = new Random();
				
				int index = random.nextInt(emptyPoints.size());			
				
				return emptyPoints.get(index).charAt(0);
			}
			//For the second move always try to make a combination of two points of a mill
			if(currentPoints.size() == 1) {
				
				Pair<String, List<String>> opponentMill = canMakeMill(pointmap, opponent); 
				//if the opponent can make a mill stop him
				if(opponentMill != null && opponentMill.getKey().toUpperCase().equals("TRUE"))
					return opponentMill.getValue().get(0).charAt(0);
				
				for(String mill : BoardGeometry.MILLS) {
					if(mill.contains(currentPoints.get(0))) {
						char one = mill.charAt(0);
						char two = mill.charAt(1);
						char three = mill.charAt(2);
						
						//Continue if the opponent has a point on this row
						if(		pointmap.get(one).Value == opponent.Color.charAt(0) || 
								pointmap.get(two).Value == opponent.Color.charAt(0) ||
								pointmap.get(three).Value == opponent.Color.charAt(0))
							continue;
						
						if(one != currentPoints.get(0).charAt(0) && pointmap.get(one).Value == '.')
							return one;
						if(two != currentPoints.get(0).charAt(0) && pointmap.get(two).Value == '.')
							return two;
						if(three != currentPoints.get(0).charAt(0) && pointmap.get(three).Value == '.')
							return three;
					}
				}
			}
			//Check to finish a mill(1) of to block an opponents mill(2)
			if(currentPoints.size() > 1) {
				
				for(String point : currentPoints) 
				{
					for(String mill : BoardGeometry.MILLS) {
						if(mill.contains(point)) {
							int rowCount = 0;
							char one = mill.charAt(0);
							char two = mill.charAt(1);
							char three = mill.charAt(2);
							
							//Continue if the opponent has a point on this row
							if(		pointmap.get(one).Value == opponent.Color.charAt(0) || 
									pointmap.get(two).Value == opponent.Color.charAt(0) ||
									pointmap.get(three).Value == opponent.Color.charAt(0))
								continue;		
												
							//Check how many points on this row are yours
							if(pointmap.get(one).Value == Color.charAt(0))
								rowCount++;
							if(pointmap.get(two).Value == Color.charAt(0))
								rowCount++;
							if(pointmap.get(three).Value == Color.charAt(0))
								rowCount++;
							
							//Continue if you already own the entire row
							if(rowCount == 3)
								continue;
							
							//If you own one of the three points extend the row or block the opponents mill
							if(rowCount == 1) {
								
								Pair<String, List<String>> opponentMill = canMakeMill(pointmap, opponent); 
								//if the opponent can make a mill stop him
								if(opponentMill != null && opponentMill.getKey().toUpperCase().equals("TRUE"))
									return opponentMill.getValue().get(0).charAt(0);
								
								if(pointmap.get(one).Value == Color.charAt(0))
									return two;
								if(pointmap.get(two).Value == Color.charAt(0))
								{
									//return either one or three (randomised)
									char[] options = {one,three};
									int rnd = new Random().nextInt(options.length);
								    return options[rnd];
								}
								if(pointmap.get(three).Value == Color.charAt(0))
									return two;
							}
							
							//If you own 2 points in the row finish the mill
							if(rowCount == 2) {
								
								if(pointmap.get(one).Value == '.')
									return one;
								if(pointmap.get(two).Value == '.')
									return two;
								if(pointmap.get(three).Value == '.')
									return three;
							}
						}
					}
				}
			}
			
			//Random number
			Random random = new Random();
			int index = random.nextInt(emptyPoints.size());			
			
			return emptyPoints.get(index).charAt(0);
		}
		
		//Return movestring to either block the opponent or to help yourself
		private String secondPhase(HashMap<Character, Point> pointmap, List<String> currentPoints, Player opponent) {	
			
			if(currentPoints.size() == 3) {
				return randomMove(pointmap);
			}
			
			for(String point : currentPoints) {
				for(String mill : BoardGeometry.MILLS) {
					if(mill.contains(point)) {
						int rowCount = 0;
						char one = mill.charAt(0);
						char two = mill.charAt(1);
						char three = mill.charAt(2);
						
						//Check how many points on this row are yours
						if(pointmap.get(one).Value == Color.charAt(0))
							rowCount++;
						if(pointmap.get(two).Value == Color.charAt(0))
							rowCount++;
						if(pointmap.get(three).Value == Color.charAt(0))
							rowCount++;
						
						//Continue if you only have one point
						if(rowCount == 1)
							continue;
						
						//Continue if you already own the entire row
						if(rowCount == 3)
							continue;
						
						//When you have 2 points on a row check possibility to make a mill
						if(rowCount == 2) {
							
							//Continue if you own two points and the opponent the third
							
							if(	pointmap.get(one).Value == opponent.Color.charAt(0) || 
								pointmap.get(two).Value == opponent.Color.charAt(0) || 
								pointmap.get(two).Value == opponent.Color.charAt(0)) 
								continue;
							
							//Search for an empty connected point
							char emptyPoint = 'Z';
							if(pointmap.get(one).Value == '.')
								emptyPoint = one;
							else if(pointmap.get(two).Value == '.')
								emptyPoint = two;
							else if(pointmap.get(three).Value == '.')
								emptyPoint = three;
							
							if(emptyPoint == 'Z')
								continue;
							
							String rowOnBoard = Character.toString(one) + Character.toString(two) + Character.toString(three);
							
							List<String> ownedConnectedPoints = getOwnedConnectedPoints(pointmap, emptyPoint, rowOnBoard);
							if(ownedConnectedPoints.size() == 0)
								continue;
							
							int rnd = new Random().nextInt(ownedConnectedPoints.size());
						    return ownedConnectedPoints.get(rnd);
						}

					}
				}
			}
			
			return randomMove(pointmap);
		}
		
		//Make a random move
		private String randomMove(HashMap<Character, Point> pointmap) {
					
			//Make list of strings and fill it with points the player owns
			List<String> myPoints = new ArrayList<String>();
			
			for(char c = 'A'; c <= 'X'; c++) {
				if(pointmap.get(c).Value == Color.charAt(0))
					myPoints.add(Character.toString(c));
			}
			
			//If the computer has 3 pieces on the board he can jump across the board
			if(myPoints.size() == 3)
			{
				
				Pair<String, List<String>> myMill = canMakeMill(pointmap, this);
				if(myMill != null)
					if(myMill.getKey().toUpperCase() == "TRUE") {
						List<String> points = getPoints(pointmap, this);
						for(String point : points) {
							if(!myMill.getValue().get(2).contains(point))
								return point + myMill.getValue().get(0); 
						}
					}
				
				//List of empty points
				List<String> emptyPoints = getEmptyPoints(pointmap);
				
				//Random number
				Random random = new Random();
				int emptyIndex = random.nextInt(emptyPoints.size());			
				int myPointIndex = random.nextInt(myPoints.size());
				
				return myPoints.get(myPointIndex) + emptyPoints.get(emptyIndex);
			}
			
			List<String> possibleMoves = new ArrayList<String>();
			
			//check if player can make a move
			for(int i = 0; i < myPoints.size(); i++) {
				char from = myPoints.get(i).charAt(0);
				
				for(char to = 'A'; to < 'X'; to++) {
					//Check if to isnt the same as from
					if(to == from)
						continue;
					//Check if field is empty and if the field is connected
					if(pointmap.get(to).Value == '.' && BoardGeometry.areConnected(from, to))
					{
						possibleMoves.add(Character.toString(from) + Character.toString(to));
						continue;
					}
				}
			}
			
			//Random number
			Random random = new Random();
			int index = random.nextInt(possibleMoves.size());			
			
			return possibleMoves.get(index);
			 
		}
		
		//Let the computer think of a point to take from opponent
		private char letMeDecedeAHit(HashMap<Character, Point> pointmap, Player opponent, int phase) {
			
			List<String> opponentPoints = getPoints(pointmap, opponent);
			
			Pair<String, List<String>> opponentMill = canMakeMill(pointmap, opponent); 
			//if the opponent can make a mill stop him
			if(opponentMill != null && opponentMill.getKey().toUpperCase().equals("TRUE"))
			{
				List<String> choices = new ArrayList<String>() {{
					add(Character.toString(opponentMill.getValue().get(1).charAt(0)));
					add(Character.toString(opponentMill.getValue().get(1).charAt(1)));
				}};
				
				//Random number
				Random random = new Random();
				int index = random.nextInt(choices.size());			
				
				//Return random element from list (one of the two taken points in the row)
				return choices.get(index).charAt(0);
			}
			
			//Random number
			Random random = new Random();
			int index = random.nextInt(opponentPoints.size());			
			
			return opponentPoints.get(index).charAt(0);
		}

		//Return list containing empty points
		private List<String> getEmptyPoints(HashMap<Character, Point> pointmap){
			
			List<String> emptyPoints = new ArrayList<String>();
			//Fill array with current owned points
			for(char c = 'A'; c <= 'X'; c++)
				if(pointmap.get(c).Value == '.')
					emptyPoints.add(Character.toString(c));
			return emptyPoints;	
		}

		//Return whether the opponent can make a mill in his next move
		private Pair<String, List<String>> canMakeMill(HashMap<Character, Point> pointmap, Player player) {
			
			List<String> opponentPoints = getPoints(pointmap, player);
			
			for(String point : opponentPoints) 
			{
				for(String mill : BoardGeometry.MILLS) {
					if(mill.contains(point)) {
						int rowCount = 0;
						char one = mill.charAt(0);
						char two = mill.charAt(1);
						char three = mill.charAt(2);
						
						//Continue if the you have a point on this row
						if(		pointmap.get(one).Value == Color.charAt(0) || 
								pointmap.get(two).Value == Color.charAt(0) ||
								pointmap.get(three).Value == Color.charAt(0))
							continue;		
											
						//Check how many points on this row are yours
						if(pointmap.get(one).Value == player.Color.charAt(0))
							rowCount++;
						if(pointmap.get(two).Value == player.Color.charAt(0))
							rowCount++;
						if(pointmap.get(three).Value == player.Color.charAt(0))
							rowCount++;
						
						//Continue if you already own the entire row
						if(rowCount == 3)
							continue;
						
						//If 1 point owned dont bother blocking
						if(rowCount == 1) {
							continue;
						}
						
						//If 2 points owned return the index of the last point
						if(rowCount == 2) {
							if(pointmap.get(one).Value == '.') {
								List<String> filledList = new ArrayList<String>() {{
									add(Character.toString(one));
									add(Character.toString(two) + Character.toString(three));
									add(Character.toString(one) + Character.toString(two) + Character.toString(three));
								}};
								return new Pair<String, List<String>>("true", filledList);
							}
								
							if(pointmap.get(two).Value == '.') {
								List<String> filledList = new ArrayList<String>() {{
									add(Character.toString(two));
									add(Character.toString(one) + Character.toString(three));
									add(Character.toString(one) + Character.toString(two) + Character.toString(three));
								}};
								return new Pair<String, List<String>>("true", filledList);
							}
							if(pointmap.get(three).Value == '.') {
								List<String> filledList = new ArrayList<String>() {{
									add(Character.toString(three));
									add(Character.toString(one) + Character.toString(two));
									add(Character.toString(one) + Character.toString(two) + Character.toString(three));
								}};
								return new Pair<String, List<String>>("true", filledList);
							}
						}
					}
				}
			}
			
			return null;			
		}
		
		//Return list op given players points
		private List<String> getPoints(HashMap<Character, Point> pointmap, Player player){
			
			List<String> points = new ArrayList<String>();
			for(Point point : pointmap.values())
				if(point.Value == player.Color.charAt(0))
					points.add(point.Name);
			return points;
			
		}
		
		//Return list of owned connected points
		private List<String> getOwnedConnectedPoints(HashMap<Character, Point> pointmap, char point, String mill){
			
			List<String> ownedConnectedPoints = new ArrayList<String>();
			
			for(Point pointData : pointmap.values()) {
				if(BoardGeometry.areConnected(pointData.Name.charAt(0), point) && pointData.Value == Color.charAt(0) &&
						!mill.contains(pointData.Name)) 
				{
					String moveString = Character.toString(pointData.Name.charAt(0)) + Character.toString(point);
					ownedConnectedPoints.add(moveString);
				}
			}
			
			return ownedConnectedPoints;
		}
}
