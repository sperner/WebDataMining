//
//export CLASSPATH=$(pwd)/colt.jar:$(pwd)/concurrent.jar:$CLASSPATH

public class main
{
	public static void main( String args[] )
	{
		VSM data = new VSM();

//		data.addDocument( "Ein Porsche ist ein Auto" );
//		data.addDocument( "Mein Auto ist ein Porsche" );
//		data.addDocument( "Das ist ein IPhone" );

		//Test mit Vorgaben
		data.addDocumentFromFile( "texte/c1" );
		data.addDocumentFromFile( "texte/c2" );
		data.addDocumentFromFile( "texte/c3" );
		data.addDocumentFromFile( "texte/c4" );
		data.addDocumentFromFile( "texte/c5" );
		data.addDocumentFromFile( "texte/m1" );
		data.addDocumentFromFile( "texte/m2" );
		data.addDocumentFromFile( "texte/m3" );
		data.addDocumentFromFile( "texte/m4" );

		String sWords = "user interface";
		System.out.println( "Ausgabe der Similarity-Values mit \"" + sWords + "\"" );
		data.printSortedDocs( sWords, 1 );
	}
}
