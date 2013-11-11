/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author opa
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableModel;

class oben extends JFrame
{
    
    JPanel panel = new JPanel();


    public oben ()
    {
        
       
        setTitle("Suchmaschiene");
        


        JLabel text = new JLabel("Suchbegriffe eingeben: ");
        text.setVisible(true);
        panel.add(text);

        JTextField eingabe = new JTextField(30);
        panel.add(eingabe);

        JButton suchb = new JButton("Suchen");
        suchb.addActionListener(new SuchListener());
        panel.add(suchb);





        String[][] rowData =
        {

       
        };

        String[] columnNames =
        {
            "<html><H3>SUCHERGEBNIS</H3></html>"
        };

        JTable table = new JTable( rowData, columnNames );
        //JTable(Object[] rowData, columnNames);
        panel.add( new JScrollPane( table ) );
        



        
        panel.setBackground(Color.white);



        //Menuelisten
        JMenuBar menuBar= new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        menuBar.add( fileMenu);
        setJMenuBar( menuBar);

        JMenuItem neu = new JMenuItem("New");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem einfuegen = new JMenuItem("Einfügen");

        
        fileMenu.add(neu);
        fileMenu.add(einfuegen);
        fileMenu.add(exit);

        exit.addActionListener(new ExitListener());
        neu.addActionListener(new NeuListener());
        einfuegen.addActionListener(new EinfuegeListener());



        



        setContentPane(panel);
        pack();
        setBackground(Color.black);
        setSize(600, 520);
        setLocation(140,25);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    class NeuListener implements ActionListener
    {
         public void actionPerformed(ActionEvent e)
        {

            setVisible(false);
            oben o = new oben();
        }

    }

    class SuchListener implements ActionListener
    {
         public void actionPerformed(ActionEvent e)
        {

        }

    }

    class EinfuegeListener implements ActionListener
    {
         public void actionPerformed(ActionEvent e)
        {
            JFileChooserDemo n = new JFileChooserDemo();
        }

    }

    public static void main(String[]args)
    {
        oben o = new oben();
    }
}

class ExitListener implements ActionListener
{
    public void actionPerformed(ActionEvent e)
    {
        System.exit(0);
        //Beendet die Scheisse
    }

}