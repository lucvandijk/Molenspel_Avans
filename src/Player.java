import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import javafx.util.Pair;

public class Player {

	protected String Name;
	protected String Color;
	protected int Pieces = 9;
	protected int PiecesOnBoard = 0;
	protected boolean finalPhase = false;
	
	public Player(String name) {
		Name = name;
	}

	// Enter character and add it to the HashMap
	public Pair<HashMap<Character, Point>, Player> enterPosition(HashMap<Character, Point> pointmap,
			Player opponent, int phase) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			// PHASE 1:
			if (phase == 1) {
				System.out.println(Name + ", geef het punt waar je een pion wilt zetten: ");

				char letter = br.readLine().toUpperCase().charAt(0);
				
				while(!Character.isLetter(letter)) {
					System.out.println("Je moet hier een letter invullen");
					letter = br.readLine().toUpperCase().charAt(0);
				}
				
				if (letter > 'X')
					letter = 'X';

				//Validate input to be only letters
				while (!isAvailable(pointmap, letter)) {
					letter = br.readLine().toUpperCase().charAt(0);
					if (letter > 'X')
						letter = 'X';
				}

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
						System.out.println(Name + " je hebt een molentje! Geef de pion op die je wilt pakken");
						Pair<HashMap<Character, Point>, Player> takeAwayPair = null;
						while (takeAwayPair == null) {
							char takeawayletter = br.readLine().toUpperCase().charAt(0);
							if (takeawayletter > 'X')
								takeawayletter = 'X';
							takeAwayPair = takeFromBoard(pointmap, takeawayletter, opponent, true);
						}
						if (takeAwayPair.getKey().get('A').Name != "SKIP") {
							opponent = takeAwayPair.getValue();
							pointmap = takeAwayPair.getKey();
						}
					}
				}
			} // END PHASE 1

			// PHASE 2:
			if (phase == 2) {
											
				if(!finalPhase && Pieces == 0 && PiecesOnBoard == 3)
				{
					finalPhase = true;
					System.out.println(Name + " je zit nu in de laatste fase, je kunt nu over het hele bord springen met je pionnen");
				}
					
				String movestring = null;
				boolean correct = false;
				while(!correct) 
				{
					System.out.println(Name + ", geef aan welke pion je wilt verzetten en waarheen: ");
					movestring = br.readLine().toUpperCase();
					
					//Validate input to be only letters
					while(!movestring.chars().allMatch(Character::isLetter)) {
						System.out.println("Je moet hier letters invullen");
						movestring = br.readLine().toUpperCase();
					}
					
					correct = checkMove(pointmap, movestring);
				}
				pointmap = movePoint(pointmap, movestring);
				
				HashMap<Character, Point> temporarymap = isMill(pointmap, movestring.toUpperCase().charAt(1));
				if (temporarymap != null) {
						// Excecute when a mill is placed
						pointmap = temporarymap;
					//Check if opponent has only mills left
					boolean onlyMills = onlyMills(pointmap, opponent);
					// Actie maken wanneer molentje is gemaakt
					if (canDelete(pointmap, opponent, onlyMills)) {
						System.out.println(Name + " je hebt een molentje! Geef de pion op die je wilt pakken");
						Pair<HashMap<Character, Point>, Player> takeAwayPair = null;
						while (takeAwayPair == null) {
							char takeawayletter = br.readLine().toUpperCase().charAt(0);
							if (takeawayletter > 'X')
								takeawayletter = 'X';
							takeAwayPair = takeFromBoard(pointmap, takeawayletter, opponent, true);
						}
						
						opponent = takeAwayPair.getValue();
						pointmap = takeAwayPair.getKey();
						
					}
				}
				
				//Check if opponent can make a move
				if(!canMove(pointmap, opponent))
				{
					//if player cant move, opponent has won!
					opponent.Pieces = 0;
					opponent.PiecesOnBoard = 2;
					System.out.println(opponent.returnName() + " kan niet meer bewegen! Gefeliciteerd " + Name + ", je hebt gewonnen!");
					Pair<HashMap<Character, Point>, Player> returnPair = new Pair<>(pointmap, opponent);
					return returnPair;
				}
				
			} // END PHASE 2
			
			
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		
		//Show pieces in hand and on board
		System.out.println(Name + ": Pionnen in hand: " + Pieces + " Pionnen op bord: " + PiecesOnBoard);
		
		Pair<HashMap<Character, Point>, Player> returnPair = new Pair<>(pointmap, opponent);
		return returnPair;
	}

	// Check if there are any deletable points
	protected boolean canDelete(HashMap<Character, Point> pointmap, Player opponent, boolean onlyMills) {
		int deleteCounter = 0;
		for (char c = 'A'; c <= 'X'; c++) {
			if (!pointmap.get(c).PartOfMill && pointmap.get(c).Value == opponent.returnColor().charAt(0))
				deleteCounter++;
			if (pointmap.get(c).PartOfMill && pointmap.get(c).Value == opponent.returnColor().charAt(0) && onlyMills)
				deleteCounter++;
		}

		if (deleteCounter == 0) {
			System.out.println(
					"**Er zijn geen pionnen die je kunt verwijderen. De beurt gaat naar " + opponent.returnName());

			return false;
		}
		return true;
	}

	// Check if position is available
	public boolean isAvailable(HashMap<Character, Point> pointmap, char letter) {

		char value = pointmap.get(letter).Value;

		// First value of letter
		if (letter == 'Z')
			return false;

		if (value == Color.charAt(0))
			System.out.println("*** Dat punt is al bezet door jou***");

		if (value == '.')
			return true;

		if (value != '.' && value != Color.charAt(0))
			System.out.println("*** Dat punt is al bezet door de tegenstander***");

		return false;
	}

	// Check if position is part of a mill
	protected HashMap<Character, Point> isMill(HashMap<Character, Point> pointmap, char letter) {
		
		String[] mills = BoardGeometry.MILLS;
		
		for (int i = 0; i < mills.length; i++) {
			if (mills[i].contains(Character.toString(letter))) {
				char one = mills[i].toUpperCase().charAt(0);
				char two = mills[i].toUpperCase().charAt(1);
				char three = mills[i].toUpperCase().charAt(2);

				if (pointmap.get(one).Value == Color.charAt(0) && pointmap.get(two).Value == Color.charAt(0)
						&& pointmap.get(three).Value == Color.charAt(0)) {
					pointmap.get(one).PartOfMill = true;
					pointmap.get(two).PartOfMill = true;
					pointmap.get(three).PartOfMill = true;

					return pointmap;
				}
			}
		}
		return null;
	}

	//Check if opponent has only mills left
	protected boolean onlyMills(HashMap<Character, Point> pointmap, Player opponent) {
		
		int totalPieces = 0;
		int totalMills = 0;
		
		for(char c = 'A'; c <= 'X'; c++) {
			if(pointmap.get(c).Value == opponent.Color.charAt(0)){
				totalPieces++;
				if(pointmap.get(c).PartOfMill)
					totalMills++;
			}
		}
		
		if(totalPieces == (totalMills / 3))
			return true;
		
		return false;
	}
	
	// Take point of opponent
	protected Pair<HashMap<Character, Point>, Player> takeFromBoard(HashMap<Character, Point> pointmap, char letter,
			Player opponent, boolean onlyMills) {

		if (pointmap.get(letter).Value != '.' && pointmap.get(letter).Value != Color.charAt(0)
				&& !pointmap.get(letter).PartOfMill) {
			pointmap.get(letter).Value = '.';
			// Take piece from other player:
			opponent.PiecesOnBoard--;

			Pair<HashMap<Character, Point>, Player> returnPair = new Pair<>(pointmap, opponent);
			return returnPair;
		} else if (pointmap.get(letter).Value == '.')
			System.out.println("Hier staat geen pion");
		else if (pointmap.get(letter).Value == Color.charAt(0))
			System.out.println("Dit is jouw pion");
		else if (pointmap.get(letter).PartOfMill)
			System.out.println("Deze pion behoort tot een molen");

		System.out.println("Geef een andere pion op: ");
		return null;
	}

	//Check if the chosen move is possible
	protected boolean checkMove(HashMap<Character, Point> pointmap, String move){
		
		if(move.length() != 2) 
		{
			System.out.println("*** Je antwoord moet 2 letters bevatten ***");
			return false;
		}
		
		char one = move.charAt(0);
		char two = move.charAt(1);
		if(one > 'X')
			one = 'X';
		if(two > 'X')
			two = 'X';
		
		//Check if points are connected
		if(!BoardGeometry.areConnected(one, two) && !finalPhase)
			System.out.println("*** Punt " + one + " is niet verbonden met punt " + two + " ***");
		//Check if point is filled with your color
		else if(pointmap.get(one).Value != Color.charAt(0))
			System.out.println("*** Op punt " + one + " staat geen pion van jou ***");
		//The correct value
		else if(pointmap.get(two).Value == Color.charAt(0))
			System.out.println("*** Op punt " + two + " staat al een pion van jou ***");
		else if(pointmap.get(two).Value != Color.charAt(0) && pointmap.get(two).Value != '.')
			System.out.println("*** Op punt " + two + " staat een pion van een tegenstander ***");
		else if(pointmap.get(two).Value == '.')
			return true;
		
		return false;
	}
	
	//Move the chosen pion and remove windmill tag when needed
	protected HashMap<Character, Point> movePoint(HashMap<Character, Point> pointmap, String move){
		
		char one = move.charAt(0);
		char two = move.charAt(1);
		
		//Remove mill tags from all related points
		if(pointmap.get(one) == null)
		{
			System.out.println("THAERRORISHERE");
		}
		if(pointmap.get(one).PartOfMill)
		{
			for(int i = 0; i < BoardGeometry.MILLS.length; i++) {
				if(BoardGeometry.MILLS[i].toUpperCase().contains(Character.toString(one).toUpperCase())){
					char mill1 = BoardGeometry.MILLS[i].toUpperCase().charAt(0);
					char mill2 = BoardGeometry.MILLS[i].toUpperCase().charAt(1);
					char mill3 = BoardGeometry.MILLS[i].toUpperCase().charAt(2);
					
					pointmap.get(mill1).PartOfMill = false;
					pointmap.get(mill2).PartOfMill = false;
					pointmap.get(mill3).PartOfMill = false;
				}
			}
		}
		
		pointmap.get(one).Value = '.';
		pointmap.get(two).Value = Color.charAt(0);
		
		return pointmap;
		
	}
	
	//Check if the player can make a move. If not, the opponent has won!
	protected boolean canMove(HashMap<Character, Point> pointmap, Player opponent) {
						
		//If a player has 3 pieces on the board he can always make a move
		if(opponent.returnPiecesOnBoard() == 3)
			return true;
		
		//Make list of strings and fill it with points the player owns
		List<String> myPoints = new ArrayList<String>();
		
		for(char c = 'A'; c <= 'X'; c++) {
			if(pointmap.get(c).Value == opponent.returnColor().charAt(0))
				myPoints.add(Character.toString(c));
		}
		
		int MoveCounter = 0;
		
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
					MoveCounter++;
					continue;
				}
			}
		}
		
		//If all pieces on the board cannot move
		if(MoveCounter == 0) {
			return false;
		}
		
		return true;
	}
	
	// Set color of Player, "Wit" or "Zwart"
	public void SetColor(int i) {
		if (i == 0)
			Color = "Wit";
		else
			Color = "Zwart";
	}

	public String returnName() {
		return Name;
	}

	public String returnColor() {
		return Color;
	}
	
	public int returnTotalPieces() {
		return Pieces;
	}

	public int returnPiecesOnBoard() {
		return PiecesOnBoard;
	}

	public void setPieceData(int total, int onBoard) {
		Pieces = total;
		PiecesOnBoard = onBoard;
	}
}
