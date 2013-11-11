//
import java.util.ArrayList;		//Array-Listen fuer Dokumente, Woerter, Vectoren
import java.util.Arrays;		//Sortieren eines Arrays mit Dokumenten und Winkeln
import java.util.StringTokenizer;	//Tokenizer zum Teilen der Dokumente in Worte
import java.lang.Math;			//mathematische Funktionen wie Wurzel, ArcCosinus
import java.io.File;			//DateiObjekt zum Einlesen von DateiDokumenten
import java.io.FileInputStream;		//Zum Stringauslesen aus der Dokumenten-Datei
import java.io.FileNotFoundException;	//Ausnahmebehandlung beim Einlesen einer Datei
import java.io.BufferedInputStream;	//Zum Beschleunigen des FileInputStreams
import java.io.IOException;		//Ausnahmebehandlung beim Streamschreiben/-lesen

//Colt-Library einbinden:	tar xvf colt-1.2.0.tar.gz && cd colt && ant build
//export CLASSPATH=/home/user/Schule/WebData/colt/lib/colt.jar:/home/user/Schule/WebData/colt/lib/concurrent.jar:$CLASSPATH
//export CLASSPATH=$(pwd)/colt/lib/colt.jar:$(pwd)/colt/lib/concurrent.jar:$CLASSPATH
//export CLASSPATH=$(pwd)/colt.jar:$(pwd)/concurrent.jar:$CLASSPATH
import cern.colt.matrix.DoubleMatrix2D;
//import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.SingularValueDecomposition;
import cern.colt.matrix.linalg.Algebra;


public class VSM
{
	ArrayList <String> unsignificants;	//Woerterbuch mit unsignifikanten Woertern
	ArrayList <String> words;		//Woerterbuch mit allen Worten
	ArrayList <document> docs;		//Dokumentliste mit allen Dokumenten
	ArrayList <double[]> vectors;		//die Vektoren zu den Dokumenten
	StringTokenizer tokenizer = null;	//zum zerpfluecken der Dokumente in einzelne Worte


	//Konstruktor mit Initialisierung der Listen
	public VSM( )
	{
		unsignificants = new ArrayList<String>();
		words = new ArrayList<String>();
		docs = new ArrayList<document>();
		vectors = new ArrayList<double[]>();
	}

	//Einen Dokument-String mit Woertern und Vektor zu den Listen hinzufuegen
	public void addDocument( String doc )
	{
		document aDocument = new document( doc );
		docs.add( aDocument );
		appendDocument( aDocument );
	}

	//Eine Dokument-Datei mit Woertern und Vektor zu den Listen hinzufuegen
	public void addDocumentFromFile( String docFilePath )
	{
		byte[] cBuffer = new byte[(int) new File(docFilePath).length()];
		BufferedInputStream bufInput = null;
		try {
			bufInput = new BufferedInputStream( new FileInputStream(docFilePath) );
		} catch (FileNotFoundException e) {
			System.out.println( "File \"" + docFilePath + "\" not found" + e );
		}
		try {
			bufInput.read( cBuffer );
		} catch (IOException e) {
			System.out.println( "I/O Exception \"bufInput.read(cBuffer)\": " + e );
		}
		document aDocument = new document( new String(cBuffer) );
		docs.add( aDocument );
		appendDocument( aDocument );
//		appendDocumentWithoutUnsignificant( aDocument );
	}

	//Eigentliches Hinzufuegen von Woertern und Vektor zu den Listen
	private void appendDocument( document aDocument )
	{
		tokenizer = new StringTokenizer( aDocument.getText(), " .,:;-()<>[]!?/-*\"\r\n" );

		String aWord = "";
		int vSize = words.size();
		double[] aVector;
		double[] newVector;
		double[] tmpVector;

		if( words.size() == 0 )
		{	aVector = new double[1];	}
		else
		{	aVector = new double[vSize];	}

		while( tokenizer.hasMoreTokens() )
		{	
			aWord = tokenizer.nextToken().toLowerCase();
			if( words.indexOf( aWord ) == -1 )
			{
				words.add( aWord );
				vSize = words.size();
				newVector = new double[vSize];
				System.arraycopy( aVector, 0, newVector, 0, vSize-1 );
				newVector[vSize-1] = 1.0;
				aVector = new double[vSize];
				aVector = newVector;
				if( vectors.size() > 0 )
				{
					for( int counter=0; counter<vectors.size(); counter++ )
					{
						newVector = new double[vSize];
						tmpVector = vectors.get( counter );
						System.arraycopy( tmpVector, 0, newVector, 0, vSize-1 );
						newVector[vSize-1] = 0.0;
						vectors.set( counter, newVector );
					}
				}
			}
			else if( words.indexOf( aWord ) >= 0 )
			{
				aVector[words.indexOf( aWord )]++;
			}
		}
		vectors.add( aVector );
	}

	//Debug-Ausgabe eines Vektors
	private void showVector( double[] aVector )
	{
		System.out.print( "Vektor: (" );
		for( int count=0; count<aVector.length; count++ )
		{
			if( count == aVector.length - 1 )
			//{	System.out.printf( "%1.0f", aVector[count] );	}
			{	System.out.printf( "%1.0f=\"%s\"", aVector[count], words.get(count) );	}
			else
			//{	System.out.printf( "%1.0f,", aVector[count] );	}
			{	System.out.printf( "%1.0f=\"%s\",", aVector[count], words.get(count) );	}
		}
		System.out.println( ")" );
	}

	//Ausgabe eines Vektors
	public void printVector( int index )
	{
		double aVector[] = vectors.get( index );
		System.out.print( "V" + index + ": (" );
		for( int count=0; count<aVector.length; count++ )
		{
			if( count == aVector.length - 1 )
			{	System.out.printf( "%1.0f", aVector[count] );	}
			else
			{	System.out.printf( "%1.0f,", aVector[count] );	}
		}
		System.out.printf( "); Laenge: %f; Dimension: %d\n", lengthOfVector( aVector ), words.size() );
	}

	//Ausgabe aller Vektoren mit Laenge, Abstaenden und Winkeln
	public void printVectors( )
	{
		System.out.println( "zeige " + vectors.size() + " Vektoren..." );
		for( int counter=0; counter<vectors.size(); counter++ )
		{
			printVector( counter );
		}
		System.out.println( "zeige " + sumOfMax(vectors.size()-1) + " Vektorenabstaende..." );
		for( int counter=0; counter<vectors.size(); counter++ )
		{
			for( int count=counter+1; count<vectors.size(); count++ )
			{
				System.out.printf("||V%d-V%d|| = %f\n", counter, count, distanceOfVectors(vectors.get(counter), vectors.get(count)) );
			}
		}
		System.out.println( "zeige " + sumOfMax(vectors.size()-1) + " Vektorenwinkel..." );
		for( int counter=0; counter<vectors.size(); counter++ )
		{
			for( int count=counter+1; count<vectors.size(); count++ )
			{
				System.out.printf("<(V%d,V%d) = %f\n", counter, count, angleOfVectors(vectors.get(counter), vectors.get(count)) );
			}
		}
		System.out.println();
	}

	//"Fakultaet" mit Addition statt Multiplikation
	private int sumOfMax( int max )
	{
		int sum = 0;
		for( int count=1; count<=max; count++ )
		{
			sum += count;
		}
		return sum;
	}

	//Laenge eines Vektors berechnen
	public double lengthOfVector( double[] aVector )
	{
		double length = 0.0;
		for( int count=0; count<aVector.length; count++ )
		{
			length += aVector[count] * aVector[count];
		}
		return Math.sqrt( length );
	}

	//Abstand zweier Vektoren berechnen
	public double distanceOfVectors( double[] firstVector, double[] secondVector )
	{
		double distance = 0.0;
		for( int count=0; count<firstVector.length; count++ )
		{
			distance += (firstVector[count]-secondVector[count])*(firstVector[count]-secondVector[count]);
		}
		return Math.sqrt( distance );
	}

	//Winkel zweier Vektoren berechnen
	public double angleOfVectors( double[] firstVector, double[] secondVector )
	{
		double angle = scalarProduct(firstVector,secondVector) /
			( lengthOfVector(firstVector) * lengthOfVector(secondVector) );
		return Math.toDegrees(Math.acos( angle ));
	}

	//Skalarprodukt zweier Vektoren berechnen
	public double scalarProduct( double[] firstVector, double[] secondVector )
	{
		double scalar = 0.0;
		int shortVectorLength = 0;

		if( firstVector.length < secondVector.length )
		{	shortVectorLength = firstVector.length;		}
		else
		{	shortVectorLength = secondVector.length;	}

		for( int count=0; count<shortVectorLength; count++ )
		{
			scalar += firstVector[count]*secondVector[count];
		}
		return scalar;
	}

	//Suche von Vektoren anhand eines Wortes
	//->Ausgabe der gefundenen Vektoren
	public void search4Vectors( String aWord )
	{
		aWord = aWord.toLowerCase();
		int index = words.indexOf( aWord );
		if( index == -1 )
		{
			System.out.println( aWord + " befindet sich nicht in der Datenbank" );
		}
		else
		{
			for( int counter=0; counter<vectors.size(); counter++ )
			{
				double aVector[] = vectors.get( counter );
				if( aVector[index] > 0 )
				{
					System.out.println( "Vector" + counter + " enthaelt \"" + aWord + "\"" );
				}
			}
		}
	}

	//Suche von Vektoren anhand eines Wortes
	//->Rueckgabe eines Arrays mit Indizes der Vektoren
	public int[] findVectors( String aWord )
	{
		aWord = aWord.toLowerCase();
		int index = words.indexOf( aWord );
		int[] vIndizes = new int[1];
		int[] tmpIndizes;

		vIndizes[0] = -1;
		if( index != -1 )
		{
			for( int counter=0; counter<vectors.size(); counter++ )
			{
				double aVector[] = vectors.get( counter );
				if( aVector[index] > 0 )
				{
					if( vIndizes[0] != -1 )
					{
						tmpIndizes = new int[vIndizes.length+1];
						System.arraycopy( vIndizes, 0, tmpIndizes, 0, vIndizes.length);
						vIndizes = tmpIndizes;
					}
					vIndizes[vIndizes.length-1] = counter;
				}
			}
		}
		return vIndizes;
	}

	//Zusammenbau eines Suchvektors
	private double[] buildSearchVector( String aSearchString )
	{
		tokenizer = new StringTokenizer( aSearchString, " .,:;-()<>[]!?/-*\"\r\n" );
		String aWord = "";

		int vSize = words.size();
		double[] aVector;
		double[] newVector;
		double[] tmpVector;

		if( words.size() == 0 )
		{	aVector = new double[1];	}
		else
		{	aVector = new double[vSize];	}

		while( tokenizer.hasMoreTokens() )
		{	
			aWord = tokenizer.nextToken().toLowerCase();
			if( words.indexOf( aWord ) == -1 )
			{
				vSize++;
				newVector = new double[vSize];
				System.arraycopy( aVector, 0, newVector, 0, vSize-1 );
				newVector[vSize-1] = 1.0;
				aVector = new double[vSize];
				aVector = newVector;
			}
			else if( words.indexOf( aWord ) >= 0 )
			{
				aVector[words.indexOf( aWord )]++;
			}
		}
		return aVector;
	}

	//Oder-Suche von Vektoren anhand beliebig vieler Worte
	//->Rueckgabe eines Arrays mit Indizes der Vektoren
	public int[] searchOR( String aSearchString )
	{
		double[] aSearchVector = buildSearchVector( aSearchString );
		int[] vIndizes = new int[1];
		int[] tmpIndizes;
		vIndizes[0] = -1;
		boolean included = false;

		for( int counter=0; counter<vectors.size(); counter++ )
		{
			double aVector[] = vectors.get( counter );
			if( scalarProduct(aSearchVector, aVector) > 0 )
			{
				if( vIndizes[0] != -1 )
				{
					tmpIndizes = new int[vIndizes.length+1];
					System.arraycopy( vIndizes, 0, tmpIndizes, 0, vIndizes.length);
					vIndizes = tmpIndizes;
				}
				vIndizes[vIndizes.length-1] = counter;
			}
		}

		return vIndizes;
	}

	//Quadratsumme eines Vektors berechnen
	private double squareOfVector( double[] aVector )
	{
		double square = 0.0;
		for( int count=0; count<aVector.length; count++ )
		{
			square += aVector[count] * aVector[count];
		}
		return square;
	}

	//Und-Suche von Vektoren anhand beliebig vieler Worte
	//->Rueckgabe eines Arrays mit Indizes der Vektoren
	public int[] searchAND( String aSearchString )
	{
		double[] aSearchVector = buildSearchVector( aSearchString );
		int[] vIndizes = new int[1];
		int[] tmpIndizes;
		vIndizes[0] = -1;
		boolean included = false;

		for( int counter=0; counter<vectors.size(); counter++ )
		{
			double aVector[] = vectors.get( counter );
			if( scalarProduct(aSearchVector, aVector) == squareOfVector(aSearchVector) )
			{
				if( vIndizes[0] != -1 )
				{
					tmpIndizes = new int[vIndizes.length+1];
					System.arraycopy( vIndizes, 0, tmpIndizes, 0, vIndizes.length);
					vIndizes = tmpIndizes;
				}
				vIndizes[vIndizes.length-1] = counter;
			}
		}

		return vIndizes;
	}


	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	/*+++++++++++++++++++++++++ Funktionen mit der Cern-COLT-Library +++++++++++++++++++++++++*/
	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


	//Erstellen einer DoubleMatrix2D
	public DoubleMatrix2D createMatrix( )
	{
		//DoubleMatrix2D matrix = new DenseDoubleMatrix2D( words.size(), vectors.size() );
		DoubleMatrix2D matrix = new SparseDoubleMatrix2D( words.size(), vectors.size() );

		for( int vectorNr=0; vectorNr<vectors.size(); vectorNr++ )
		{
			double aVector[] = vectors.get( vectorNr );
			for( int wordNr=0; wordNr<words.size(); wordNr++ )
			{
				matrix.set( wordNr, vectorNr, aVector[wordNr] );
			}
		}
		return matrix;
	}

	//Ausgabe einer Matrix
	public void showMatrix( )
	{
		System.out.println( "Aktuelle Matrix:\n----------------" );
		DoubleMatrix2D matrix = createMatrix( );
		for( int row=0; row<matrix.rows(); row++ )
		{
			for( int column=0; column<matrix.columns(); column++ )
			{
				System.out.printf( "%3.0f ", matrix.get(row, column) );
			}
			System.out.print( "\n" );
		}
	}

	//Ausgabe einer Matrix
	public void printMatrix( DoubleMatrix2D aMatrix )
	{
		for( int row=0; row<aMatrix.rows(); row++ )
		{
			for( int column=0; column<aMatrix.columns(); column++ )
			{
				System.out.printf( "%10.3f ", aMatrix.get(row, column) );
			}
			System.out.print( "\n" );
		}
	}

	//Erstellen einer SingularValueDecomposition
	public SingularValueDecomposition createDecomposition( )
	{
		SingularValueDecomposition decomposition = new SingularValueDecomposition( createMatrix() );
		return decomposition;
	}

	//Ausgabe vom Rang
	public void printRank()
	{
		System.out.println( "Rang der Matrix: " + createDecomposition().rank() );
	}

	//Erstellen von U
	public DoubleMatrix2D getU( )
	{
		return createDecomposition().getU();
	}

	//Erstellen von S
	public DoubleMatrix2D getS( )
	{
		return createDecomposition().getS();
	}

	//Erstellen von V
	public DoubleMatrix2D getV( )
	{
		return createDecomposition().getV();
	}

	//Erstellen von A
	public DoubleMatrix2D getA( )
	{
		Algebra algebra = new Algebra();
		DoubleMatrix2D tmpMatrix = algebra.mult( getU(), getS() );
		return algebra.mult( tmpMatrix, algebra.transpose(getV()) );
	}

	//Erstellen von Q
	public DoubleMatrix2D getQ( String aQuery )
	{
		double[] qVector = buildSearchVector( aQuery );
		DoubleMatrix2D tmpMatrix = new SparseDoubleMatrix2D( words.size(), 1 );
		for( int wordNr=0; wordNr<words.size(); wordNr++ )
		{
			tmpMatrix.set( wordNr, 0, qVector[wordNr] );
		}
		return tmpMatrix;
	}

	//Erstellen von Qt
	public DoubleMatrix2D getQt( String aQuery )
	{
		double[] qVector = buildSearchVector( aQuery );
		DoubleMatrix2D tmpMatrix = new SparseDoubleMatrix2D( 1, words.size() );
		for( int wordNr=0; wordNr<words.size(); wordNr++ )
		{
			tmpMatrix.set( 0, wordNr, qVector[wordNr] );
		}
		return tmpMatrix;
	}


	//Erstellen von Uk
	public DoubleMatrix2D getUk( int k )
	{
		Algebra algebra = new Algebra();
		DoubleMatrix2D uMatrix = createDecomposition().getU();
		DoubleMatrix2D ukMatrix = algebra.subMatrix( uMatrix, 0, uMatrix.rows()-1, 0, k );
		return ukMatrix;
	}

	//Erstellen von Sk
	public DoubleMatrix2D getSk( int k )
	{
		Algebra algebra = new Algebra();
		DoubleMatrix2D sMatrix = createDecomposition().getS();
		DoubleMatrix2D skMatrix = algebra.subMatrix( sMatrix, 0, k, 0, k );
		return skMatrix;
	}

	//Erstellen von Vk
	public DoubleMatrix2D getVk( int k )
	{
		Algebra algebra = new Algebra();
		DoubleMatrix2D vMatrix = createDecomposition().getV();
		DoubleMatrix2D vkMatrix = algebra.subMatrix( vMatrix, 0, vMatrix.columns()-1, 0, k );
		return vkMatrix;
	}

	//Erstellen von VkT
	public DoubleMatrix2D getVkT( int k )
	{
		DoubleMatrix2D vkMatrix = getVk( k );
		DoubleMatrix2D vktMatrix = new SparseDoubleMatrix2D( vkMatrix.columns(), vkMatrix.rows() );
		for( int row=0; row<vktMatrix.rows(); row++ )
		{
			for( int column=0; column<vktMatrix.columns(); column++ )
			{
				vktMatrix.set( row, column, vkMatrix.get( column, row ) );
			}
		}
		return vktMatrix;
	}

	//Erstellen von Qk
	public DoubleMatrix2D getQk( String aQuery, int k )
	{
		Algebra algebra = new Algebra();
		DoubleMatrix2D tmpMatrix = algebra.mult( getQt(aQuery), getUk(k) );
		return algebra.mult( tmpMatrix, algebra.inverse(getSk(k)) );
	}


	//Winkel zweier Vektoren berechnen
	private double getAngle( double[] firstVector, double[] secondVector )
	{
		double angle = scalarProduct(firstVector,secondVector) /
			( lengthOfVector(firstVector) * lengthOfVector(secondVector) );
		//return Math.toDegrees(Math.acos( angle ));
		//return Math.acos( angle );
		return angle;
	}

	//Ausgeben der Winkelabstaende
	public void printSimilarity( String aQuery, int k )
	{
		DoubleMatrix2D tmpQk = getQk( aQuery, k );
		DoubleMatrix2D tmpVk = getVk( k );
		double[] tmpVector1 = new double[tmpVk.rows()];
		double[] tmpVector2 = new double[tmpVk.rows()];
		for( int ctr=0; ctr<tmpQk.columns(); ctr++ )
		{
			tmpVector1[ctr] = tmpQk.get( 0, ctr );
		}
		System.out.printf( "Qk:  %5.4f  %5.4f\n", tmpVector1[0], tmpVector1[1] );
		for( int count=0; count<tmpVk.rows(); count++ )
		{
			for( int counter=0; counter<k+1; counter++ )
			{
				tmpVector2[counter] = tmpVk.get( count, counter );
			}
			System.out.printf( "document%5d: %10.3f\n", count, getAngle(tmpVector1, tmpVector2) );
		}
	}

	//Sortieren der Dokumente anhand ihrer Gewichtung
	private static double[][] sortDocsBySimilarity(double[][] array, double length)
	{
		double temp0 = 0.0;
		double temp1 = 0.0;
		for( int count=0; count<length-1; count++ )
		{
			for( int counter=0; counter<length-1; counter++ )
			{
				if( array[0][counter] < array[0][counter+1] )
				{
					temp0 = array[0][counter];
					temp1 = array[1][counter];
					array[0][counter] = array[0][counter+1];
					array[1][counter] = array[1][counter+1];
					array[0][counter+1] = temp0;
					array[1][counter+1] = temp1;
				}
			}
		}
		return array;
	}

	//Ausgeben der Dokumente sortiert nach Gewichtung
	public void printSortedDocs( String aQuery, int k )
	{
		DoubleMatrix2D tmpQk = getQk( aQuery, k );
		DoubleMatrix2D tmpVk = getVk( k );
		double[] tmpVector1 = new double[tmpVk.rows()];
		double[] tmpVector2 = new double[tmpVk.rows()];
		double[][] tmpDocAngle = new double[2][tmpVk.rows()];
		for( int ctr=0; ctr<tmpQk.columns(); ctr++ )
		{
			tmpVector1[ctr] = tmpQk.get( 0, ctr );
		}
		System.out.printf( "Qk:  %5.4f  %5.4f\n", tmpVector1[0], tmpVector1[1] );
		for( int count=0; count<tmpVk.rows(); count++ )
		{
			for( int counter=0; counter<k+1; counter++ )
			{
				tmpVector2[counter] = tmpVk.get( count, counter );
			}
			tmpDocAngle[0][count] = getAngle(tmpVector1, tmpVector2);
			tmpDocAngle[1][count] = count;
		}
		tmpDocAngle = sortDocsBySimilarity( tmpDocAngle, tmpVk.rows() );
		for( int count=0; count<tmpVk.rows(); count++ )
		{
			System.out.printf( "document%5.0f: %10.3f\n", tmpDocAngle[1][count], tmpDocAngle[0][count] );
		}
	}



	//Eine Dokument-Datei mit unsignifikanten Woertern hinzufuegen
	public void addUnsignificants( String docFilePath )
	{
		byte[] cBuffer = new byte[(int) new File(docFilePath).length()];
		BufferedInputStream bufInput = null;
		try {
			bufInput = new BufferedInputStream( new FileInputStream(docFilePath) );
		} catch (FileNotFoundException e) {
			System.out.println( "File \"" + docFilePath + "\" not found" + e );
		}
		try {
			bufInput.read( cBuffer );
		} catch (IOException e) {
			System.out.println( "I/O Exception \"bufInput.read(cBuffer)\": " + e );
		}
		tokenizer = new StringTokenizer( cBuffer.toString(), " .,:;-()<>[]!?/-*\"\r\n" );
		String aWord = "";
		while( tokenizer.hasMoreTokens() )
		{	
			aWord = tokenizer.nextToken().toLowerCase();
			if( unsignificants.indexOf( aWord ) == -1 )
			{
				unsignificants.add( aWord );
			}
		}
	}

	//Hinzufuegen von Woertern und Vektor, abhaengig von unsignifikanten Woertern
	private void appendDocumentWithoutUnsignificant( document aDocument )
	{
		tokenizer = new StringTokenizer( aDocument.getText(), " .,:;-()<>[]!?/-*\"\r\n" );

		String aWord = "";
		int vSize = words.size();
		double[] aVector;
		double[] newVector;
		double[] tmpVector;

		if( words.size() == 0 )
		{	aVector = new double[1];	}
		else
		{	aVector = new double[vSize];	}

		while( tokenizer.hasMoreTokens() )
		{	
			aWord = tokenizer.nextToken().toLowerCase();
			if( words.indexOf( aWord ) == -1 && unsignificants.indexOf( aWord ) == -1 )
			{
				words.add( aWord );
				vSize = words.size();
				newVector = new double[vSize];
				System.arraycopy( aVector, 0, newVector, 0, vSize-1 );
				newVector[vSize-1] = 1.0;
				aVector = new double[vSize];
				aVector = newVector;
				if( vectors.size() > 0 )
				{
					for( int counter=0; counter<vectors.size(); counter++ )
					{
						newVector = new double[vSize];
						tmpVector = vectors.get( counter );
						System.arraycopy( tmpVector, 0, newVector, 0, vSize-1 );
						newVector[vSize-1] = 0.0;
						vectors.set( counter, newVector );
					}
				}
			}
			else if( words.indexOf( aWord ) >= 0 )
			{
				aVector[words.indexOf( aWord )]++;
			}
		}
		vectors.add( aVector );
	}

}
