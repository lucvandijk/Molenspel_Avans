import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Application {
	
	//First index = type (0 = person, 1 = computer) [0][0] of [1][0] = type
	//Second index = list index						[0][1] of [1][1] = index
	int[][] roles = new int[2][2];
	List<Player> players = new ArrayList<Player>();
	List<Computer> computers = new ArrayList<Computer>();
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Morris _morris;
	
	
	//Show welcome message and ask for player names
	public void setUpGame() {
		System.out.println("Welkom bij het aloude Molenspel!");
		setUpPlayers();
		
		//Create new instance of morris object
		_morris = new Morris(players, computers, roles);
		
		_morris.play();
	}
	
	//Ask player to choose player names and/or computer player(s)
	private void setUpPlayers() {
		try {
			String input;

			for(int i = 0; i < 2; i++) {
				System.out.println("Geef de naam van speler " + (i + 1) + " (of een C voor een computer): ");
				
				input = br.readLine();
				if(!input.toLowerCase().equals("c")) {
					roles[i] = new int[] {0, players.size()};
					players.add(new Player(input));
				}
				else {
					roles[i] = new int[] {1, computers.size()};
					computers.add(new Computer());
				}		
			}			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
