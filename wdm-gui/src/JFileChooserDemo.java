/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author opa
 */
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class JFileChooserDemo
{
  public JFileChooserDemo ()
  {
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(true);

    fc.setFileFilter( new FileFilter()
    {
      @Override public boolean accept( File f )
      {
        return f.isDirectory() ||
          f.getName().toLowerCase().endsWith( ".txt" );
      }
      @Override public String getDescription()
      {
        return "Texte";
      }
    } );

    int state = fc.showOpenDialog( null );

    if ( state == JFileChooser.APPROVE_OPTION )
    {
      File[] file = fc.getSelectedFiles();
      for (int i = 0; i < file.length; i++)
      {
         System.out.println("file name: " + file[i].getName());
         System.out.println("file name: " + file[i].getAbsolutePath());

      }

    }
    else
      System.out.println( "Auswahl abgebrochen" );

    
  }
}