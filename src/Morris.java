import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;

public class Morris extends BoardGeometry {

	// First = type (0 = person, 1 = computer) [0][0] of [1][0] = type
	// Second = list index [0][1] of [1][1] = index
	public int[][] Roles = new int[2][2];

	public List<Player> Players = new ArrayList<Player>();
	public List<Computer> Computers = new ArrayList<Computer>();

	public Board _board;

	public int StartTurnIndex;
	public int Turnindex;
	public int Phase = 1;
	public int Turn = 0;

	public boolean hasTestUser = false;
	
	public Morris(List<Player> players, List<Computer> computers, int[][] roles) {
		Players = players;
		Computers = computers;
		Roles = roles;

		raffle();
	}

	// Raffle the colors between the two players
	private void raffle() {
		// Determine color using a random number
		Random rnd = new Random();
		double randomNumber = rnd.nextDouble();
		int white;
		int black;

		if (randomNumber < 0.5) {
			white = 0;
			black = 1;
			StartTurnIndex = 0;
			Turnindex = 0;
		} else {
			white = 1;
			black = 0;
			StartTurnIndex = 1;
			Turnindex = 1;
		}

		// Colors
		if (Roles[white][0] == 0)
			Players.get(Roles[white][1]).SetColor(0);
		else
			Computers.get(Roles[white][1]).SetColor(0);
		if (Roles[black][0] == 0)
			Players.get(Roles[black][1]).SetColor(1);
		else
			Computers.get(Roles[black][1]).SetColor(1);

		
		String name = "";
		// Print results
		System.out.println("De loting is verricht");
		if (Roles[0][0] == 0) 
		{
			name = Players.get(Roles[0][1]).returnName();
			System.out.println(
					name + " heeft " + Players.get(Roles[0][1]).returnColor());
			if(name.toLowerCase().equals("test"))
				hasTestUser = true;
		}
		else
		{
			name = Computers.get(Roles[0][1]).returnName();
			System.out.println(
					name + " heeft " + Computers.get(Roles[0][1]).returnColor());
			if(name.toLowerCase().equals("test"))
				hasTestUser = true;
		}
		if (Roles[1][0] == 0)
		{
			name = Players.get(Roles[1][1]).returnName();
			System.out.println(
					name + " heeft " + Players.get(Roles[1][1]).returnColor());
		}
		else
		{			
			name = Computers.get(Roles[1][1]).returnName();
			System.out.println(
					name + " heeft " + Computers.get(Roles[1][1]).returnColor());
		}
		
		//Dont run when test user is entered
		if(!hasTestUser)
			System.out.println("*** Fase 1 van het spel begint nu ***");
	}

	// Loop until user stops program or game has ended
	public void play() {
		// Create new instance of board object
		_board = new Board(LAYOUT);
		
		if(hasTestUser)
		{
			_board.enterTestValues();
			Phase = 2;
			Turn = 19;
			setTestPlayerData();			
			System.out.println("*** Fase 2 van het spel begint nu ***");
		}
		_board.update();
		
		while (!step()) {
			// keep excecuting while the step method returns false (game is active)
			
			//For better view of the computer-players:
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	//Excecute every turn
	private boolean step() {
		// Used variables
		Pair<HashMap<Character, Point>, Player> pair;
		Player opponent;

		// Find opponent
		if (Turnindex == 0) 
		{
			// if is player
			if (Roles[Turnindex + 1][0] == 0)
				opponent = Players.get(Roles[Turnindex + 1][1]);
			// if is computer
			else
				opponent = (Player) Computers.get(Roles[Turnindex + 1][1]);
		}
		else 
		{
			// if is player
			if (Roles[Turnindex - 1][0] == 0)
				opponent = Players.get(Roles[Turnindex - 1][1]);
			// if is computer
			else
				opponent = (Player) Computers.get(Roles[Turnindex - 1][1]);
		}

		// if players' turn
		if (Roles[Turnindex][0] == 0) 
		{
			pair = Players.get(Roles[Turnindex][1]).enterPosition(_board.pointmap, opponent, Phase);
		}
		// if computers' turn
		else 
		{
			pair = Computers.get(Roles[Turnindex][1]).enterPosition(_board.pointmap, opponent, Phase);
		}

		// Update opponents data
		if (Turnindex == 0) 
		{
			// if player
			if (Roles[Turnindex + 1][0] == 0)
				Players.get(Roles[Turnindex + 1][1]).setPieceData(pair.getValue().returnTotalPieces(),
						pair.getValue().returnPiecesOnBoard());
			// if computer
			else
				Computers.get(Roles[Turnindex + 1][1]).setPieceData(pair.getValue().returnTotalPieces(),
						pair.getValue().returnPiecesOnBoard());
		} 
		else 
		{
			// if player
			if (Roles[Turnindex - 1][0] == 0)
				Players.get(Roles[Turnindex - 1][1]).setPieceData(pair.getValue().returnTotalPieces(),
						pair.getValue().returnPiecesOnBoard());
			// if computer
			else
				Computers.get(Roles[Turnindex - 1][1]).setPieceData(pair.getValue().returnTotalPieces(),
						pair.getValue().returnPiecesOnBoard());
		}

		//Show real time player stats
		//System.out.println("Name: " + opponent.returnName() + " has left: " + opponent.returnTotalPieces()
		//		+ " and on the board: " + opponent.returnPiecesOnBoard());

	
		// Update pointmap
		_board.pointmap = pair.getKey();
		// Update and print board
		_board.update();	
	
		// Increase turn number and start new phase when all points are placed
		Turn++;
		if (Turn == 18) {
			Phase = 2;
			System.out.println("*** Fase 2 van het spel begint nu ***");
		}
		
		// The win condition
		if (opponent.returnTotalPieces() == 0 && opponent.returnPiecesOnBoard() < 3) {
			
			Player winner;

			// if player
			if (Roles[Turnindex][0] == 0)
				winner = Players.get(Roles[Turnindex][1]);
			// if computer
			else
				winner = Computers.get(Roles[Turnindex][1]);
			
			toggleWin(winner);
			return true;			
		}

		// Swap player
		if (Turnindex == 0)
			Turnindex = 1;
		else
			Turnindex = 0;

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	// Win event
	private void toggleWin(Player player) {
		System.out.println("*** " + player.returnName() + " heeft gewonnen! ***");
		playAgain();
	}

	//Ask player to play another game
	private void playAgain() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("*** Wilt u nog een wedstrijd spelen? (ja/nee) ***");

		try {
			if (br.readLine().toUpperCase().equals("JA"))
			{
				resetData();
				raffle();
				play();
			}
			else
				System.out.println("*** Bedankt voor het spelen ;-) ***");
				System.out.println("*** Gemaakt door Luc van Dijk :D ***");
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Reset the data to start a new game
	private void resetData() {
		Phase = 1;
		Turn = 0;
		
		//Reset current player
		if(Roles[Turnindex][0] == 0) 
			Players.get(Roles[Turnindex][1]).setPieceData(9, 0);
		else
			Computers.get(Roles[Turnindex][1]).setPieceData(9, 0);
		
		//Switch player
		if(Turnindex == 0) 
			Turnindex = 1;
		else
			Turnindex = 0;
		
		//Reset other player
		if(Roles[Turnindex][0] == 0) 
			Players.get(Roles[Turnindex][1]).setPieceData(9, 0);
		else
			Computers.get(Roles[Turnindex][1]).setPieceData(9, 0);
		
		//Set the turnindex to the player with the white points
		Turnindex = StartTurnIndex;
	}

	//In case player one is a test player, add test data to the game
	private void setTestPlayerData() {
		
		for(int i = 0; i < 2; i++) {
			// if player
			if (Roles[Turnindex][0] == 0)
				Players.get(Roles[Turnindex][1]).setPieceData(0, 9);
			// if computer
			else
				Computers.get(Roles[Turnindex][1]).setPieceData(0, 9);
			
			if(Turnindex == 0)
				Turnindex = 1;
			else
				Turnindex = 0;

		}	
	}

}
