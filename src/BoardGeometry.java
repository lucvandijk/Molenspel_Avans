//package morris;

/**
 * Deze klasse bevat een aantal datastructuren en een methode
 * die je kunnen helpen om te gaan met de geometrie van het bord.
 * 
 * Je komt hier een aantal malen het keyword "static" tegen.
 * Dat betekent dat een veld of een methode bij de klasse hoort.
 * Als je dat veld in een andere klasse wilt gebruiken,
 * moet je er de naam van deze klasse (BoardGeometry) voor zetten.
 * 
 * Voorbeeld 1
 * -----------
 * Om het bord af te drukken, kun je de volgende code gebruiken.
 * 
 * 		for (String s : BoardGeometry.LAYOUT)
 * 		{
 * 			System.out.println(s);
 * 		}
 * 
 * Voorbeeld 2
 * -----------
 * De array MILLS bevat alle molentjes met de punten in alfabetische volgorde.
 * Je kunt deze gebruiken om te kijken of er na een zet een molentje ontstaan is.
 * Stel bv. dat wit net gezet heeft op punt "p". Dan zou je zoiets kunnen doen:
 * 
 * 		for (String s : BoardGeometry.MILLS)
 * 		{
 * 			if (s.indexof(p) >= 0)
 * 			{
 * 				// Check of op alle drie punten in s een witte pion staat.
 * 				// Zo ja, dan heeft wit een molentje gevormd.
 * 			}
 * 		}
 * 
 * Voorbeeld 3
 * -----------
 * De array CONNECTIONS bevat alle verbindingen tussen twee punten,
 * dus "AB", "BA, "BC", "CB", "CO", "OC", enzovoort, enzovoort.
 * Om alle mogelijke zetten te bekijken, kun je de volgende code gebruiken:
 * 
 * 		for (String move : BoardGeometry.CONNECTIONS)
 * 		{
 * 			char from = move.charAt(0);
 * 			char to = move.charAt(1);
 * 
 * 			// Doe iets met de mogelijke zet van "from" naar "to".
 * 			// Kijk bv. of er op "from" een witte pion staat en op "to" niks;
 * 			// dan is dit een mogelijke zet voor wit in fase 2.
 * 		}
 * 
 * Er worden nog wat andere keywords gebruikt (abstract, final)
 * die je nog niet gehad hebt. Geen probleem, gewoon negeren.
 * Ook het stukje code hoef je niet per se te begrijpen.
 * Als je de methode areConnected() maar kunt toepassen, daar gaat het om.
 */

import java.util.HashSet;

public abstract class BoardGeometry
{
	// Een array van Strings waarmee je het bord kunt tekenen
	
	public final static String[] LAYOUT =
	{
			"A-----------B-----------C",
			"|           |           |",
			"|   D-------E-------F   |",
			"|   |       |       |   |",
			"|   |   G---H---I   |   |",
			"|   |   |       |   |   |",
			"J---K---L       M---N---O",
			"|   |   |       |   |   |",
			"|   |   P---Q---R   |   |",
			"|   |       |       |   |",
			"|   S-------T-------U   |",
			"|           |           |",
			"V-----------W-----------X"
	};
	

	
	// Een array van Strings waarin alle mogelijke molentjes staan
	// met de drie punten in alfabetische volgorde

	public final static String[] MILLS =
	{
			"ABC", "DEF", "GHI", "JKL", "MNO", "PQR", "STU", "VWX",
			"AJV", "DKS", "GLP", "BEH", "QTW", "IMR", "FNU", "COX"
	};
	
	// Een hulparray waarmee zometeen de mogelijke connecties berekend worden

	private static String[] SEQUENCES =
	{
			"ABCOXWVJA", "DEFNUTSKD", "GHIMRQPLG", "BEH", "ONM", "WTQ", "JKL"
	};
	
	// Alle mogelijk connecties: "AB", "BA", "AJ", "JA", enzovoort

	public final static HashSet<String> CONNECTIONS = new HashSet<>();

	// Een stukje code om de connecties te berekenen
	
	static
	{
		for (int i = 0; i < SEQUENCES.length; i++)
		{
			for (int j = 0; j < SEQUENCES[i].length() - 1; j++)
			{
				CONNECTIONS.add("" + SEQUENCES[i].charAt(j) + SEQUENCES[i].charAt(j + 1));
				CONNECTIONS.add("" + SEQUENCES[i].charAt(j + 1) + SEQUENCES[i].charAt(j));
			}
		}
	}
	
	// Een methode die vertelt of twee punten al dan niet verbonden zijn
	// Voorbeeld: BoardGeometry.areConnected('A','B') -> true
	// Voorbeeld: BoardGeometry.areConnected('B','A') -> true
	// Voorbeeld: BoardGeometry.areConnected('A','C') -> false
	// Voorbeeld: BoardGeometry.areConnected('X','A') -> false

	public static boolean areConnected(char from, char to)
	{
		return CONNECTIONS.contains("" + from + to);
	}
}
