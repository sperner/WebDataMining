//

public class main
{
	public static void showVectors( VSM database, int[] indizes, String word )
	{
		if( indizes[0] != -1 )
		{
			for( int count=0; count<indizes.length; count++ )
			{
				database.printVector( indizes[count] );
			}
		}
		else
		{
			System.out.println( "Das Wort/die Woerter befindet/n sich nicht in der Datenbank" );
		}
	}

	public static void main( String args[] )
	{
		VSM data = new VSM();

//		data.addDocument( "Ein Porsche ist ein Auto" );
//		data.printVectors( );
		// jetzt soll der Text in data.docs sein und
		// das Woerterbuch besteht aus vier Woertern,
		// ein Vektor (2,1,1,1) ist in data.vectors
//		data.addDocument( "Mein Auto ist ein Porsche" );
//		data.printVectors( );
		// jetzt sollen zwei Texte in data.docs sein und
		// das Woerterbuch besteht aus fuenf Woertern,
		// zwei Vektoren (2,1,1,1,0) und (1,1,1,1,1) sind
		// in data.vektors
//		data.addDocument( "Kein Golf ist ein Porsche" );
//		data.printVectors( );
		// jetzt sollen drei Texte in data.docs sein und
		// das Woerterbuch besteht aus sieben Woertern,
		// drei Vektoren (2,1,1,1,0,0,0), (1,1,1,1,1,0,0)
		// und (1,1,1,0,0,1,1) sind in data.vektors

		//Test mit Vorgaben
		data.addDocument( "Human interface computer" );
		data.addDocument( "survey user computer system response time" );
		data.addDocument( "eps user interface system" );
		data.addDocument( "System human system eps" );
		data.addDocument( "user response time" );
		data.addDocument( "trees" );
		data.addDocument( "graph trees" );
		data.addDocument( "Graph minors trees" );
		data.addDocument( "Graph minors survey" );

/*
		//unsignifikante Worte einlesen
		data.addUnsignificants( "texte/000" );

		//Test mit eigenen Dateien
		data.addDocumentFromFile( "texte/KFZ" );
		data.addDocumentFromFile( "texte/Auto" );
		data.addDocumentFromFile( "texte/Motorrad" );
		data.addDocumentFromFile( "texte/Porsche" );
		data.addDocumentFromFile( "texte/VW" );
//		data.addDocumentFromFile( "texte/gibt_es_nicht" );
		data.addDocumentFromFile( "texte/KFZ-Diaet" );
		data.addDocumentFromFile( "texte/Baum" );
		data.addDocumentFromFile( "texte/Blume" );
		data.addDocumentFromFile( "texte/Computer" );
		data.addDocumentFromFile( "texte/Erde" );
		data.addDocumentFromFile( "texte/Gehirn" );
		data.addDocumentFromFile( "texte/Gott" );
//		data.printVectors( );
		//
*/
		String sWords;
/*
		sWords = "AUTO";
		data.search4Vectors( sWords );
		showVectors( data, data.findVectors( sWords ), sWords );

		sWords = "GOLF";
		data.search4Vectors( sWords );
		showVectors( data, data.findVectors( sWords ), sWords );

		sWords = "PORSCHE";
		data.search4Vectors( sWords );
		showVectors( data, data.findVectors( sWords ), sWords );
*/
/*
		sWords = "auto";
		data.search4Vectors( sWords );
		sWords = "motorrad";
		data.search4Vectors( sWords );
		sWords = "auto motorrad";
		showVectors( data, data.searchOR( sWords ), sWords );
*/
/*
		sWords = "auto";
		data.search4Vectors( sWords );
		sWords = "pkw";
		data.search4Vectors( sWords );
		//sWords = "auto pkw ongabonga";
		//data.searchOR( sWords );
		sWords = "auto pkw";
		//showVectors( data, data.searchOR( sWords ), sWords );
		//showVectors( data, data.searchAND( sWords ), sWords );
*/
		//data.printVectors();
		//data.showMatrix();
		//data.printRank();
/*
		System.out.println( "Ausgabe unserer Matrix" );
		data.printMatrix( data.createMatrix() );
		System.out.println( "Ausgabe der A-Matrix" );
		data.printMatrix( data.getA() );
*/
/*
		System.out.println( "Ausgabe der U-Matrix" );
		data.printMatrix( data.getU() );
		System.out.println( "Ausgabe der S-Matrix" );
		data.printMatrix( data.getS() );
		System.out.println( "Ausgabe der V-Matrix" );
		data.printMatrix( data.getV() );
*/
/*
		System.out.println( "Ausgabe der Uk-Matrix" );
		data.printMatrix( data.getUk( 1 ) );
		System.out.println( "Ausgabe der Sk-Matrix" );
		data.printMatrix( data.getSk( 1 ) );
		System.out.println( "Ausgabe der Vk-Matrix" );
		data.printMatrix( data.getVk( 1 ) );
		System.out.println( "Ausgabe der VkT-Matrix" );
		data.printMatrix( data.getVkT( 1 ) );
*/

		sWords = "user interface";
//		sWords = "vw konzern";
//		sWords = "auto oder motorrad fahren";
//		sWords = "computer benutzer";
//		sWords = "gehirn";
//		sWords = "gott";
//		sWords = "baum";
//		System.out.println( "Ausgabe der Q-Matrix mit \"" + sWords + "\"" );
//		data.printMatrix( data.getQ( sWords ) );
//		System.out.println( "Ausgabe der Qt-Matrix mit \"" + sWords + "\"" );
//		data.printMatrix( data.getQt( sWords ) );
//		System.out.println( "Ausgabe der Qk-Matrix mit \"" + sWords + "\"" );
//		data.printMatrix( data.getQk( sWords, 1 ) );
		System.out.println( "Ausgabe der Similarity-Values mit \"" + sWords + "\"" );
		//data.printSimilarity( sWords, 1 );
		data.printSortedDocs( sWords, 1 );

	}
}
