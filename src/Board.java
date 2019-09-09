import java.util.HashMap;

public class Board {
	
	public String[] PlayBoard = new String[13];
		
	public String[] ExampleBoard;
	public HashMap<Character, Point> pointmap = new HashMap<Character, Point>();
	
	public Board(String[] exampleboard) {
		ExampleBoard = exampleboard;
		setupBoard();
	}
	
	//Call methods after changing hashmap
	public void update() {
		updateBoard();
		drawBoard();
	}
	
	//Draw view of board
	private void drawBoard() {
		for(int i=0;i<ExampleBoard.length;i++) 
			System.out.println(PlayBoard[i] + "          " + ExampleBoard[i]);
	}
		
	//Update the board using the hashmap
	private void updateBoard() {
		PlayBoard[0] = pointmap.get('A').Value + "-----------" + pointmap.get('B').Value + "-----------" + pointmap.get('C').Value; 
		PlayBoard[1] = "|           |           |";
		PlayBoard[2] = "|   " + pointmap.get('D').Value + "-------" + pointmap.get('E').Value + "-------" + pointmap.get('F').Value + "   |";
		PlayBoard[3] = "|   |       |       |   |";
		PlayBoard[4] = "|   |   " + pointmap.get('G').Value +"---" + pointmap.get('H').Value + "---" + pointmap.get('I').Value + "   |   |";
		PlayBoard[5] = "|   |   |       |   |   |";
		PlayBoard[6] = pointmap.get('J').Value + "---" + pointmap.get('K').Value + "---" + pointmap.get('L').Value + "       " + pointmap.get('M').Value + "---" + pointmap.get('N').Value + "---" + pointmap.get('O').Value;
		PlayBoard[7] = "|   |   |       |   |   |";
		PlayBoard[8] = "|   |   " + pointmap.get('P').Value + "---" + pointmap.get('Q').Value + "---" + pointmap.get('R').Value + "   |   |";
		PlayBoard[9] = "|   |       |       |   |";
		PlayBoard[10] = "|   " + pointmap.get('S').Value + "-------" + pointmap.get('T').Value + "-------" + pointmap.get('U').Value + "   |";
		PlayBoard[11] = "|           |           |";
		PlayBoard[12] = pointmap.get('V').Value + "-----------" + pointmap.get('W').Value + "-----------" + pointmap.get('X').Value;			
	}
	
	//Fill hashmap with values
	private void setupBoard() {
		for (char i = 'A'; i <= 'X'; i++)   
			pointmap.put(i, new Point(Character.toString(i)));	
		updateBoard();
	}
	
	//Enter all testdata
	public void enterTestValues() {
		//White points
		pointmap.get('A').Value = 'W'; pointmap.get('B').Value = 'W';
		pointmap.get('F').Value = 'W'; pointmap.get('H').Value = 'W';
		pointmap.get('K').Value = 'W'; pointmap.get('P').Value = 'W';
		pointmap.get('Q').Value = 'W'; pointmap.get('U').Value = 'W';
		pointmap.get('W').Value = 'W'; 
		
		//Black points
		pointmap.get('C').Value = 'Z'; pointmap.get('D').Value = 'Z';
		pointmap.get('G').Value = 'Z'; pointmap.get('I').Value = 'Z';
		pointmap.get('J').Value = 'Z'; pointmap.get('N').Value = 'Z';
		pointmap.get('S').Value = 'Z'; pointmap.get('V').Value = 'Z';
		pointmap.get('X').Value = 'Z';
	}
	
		
}