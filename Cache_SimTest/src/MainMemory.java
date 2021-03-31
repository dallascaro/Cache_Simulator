import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A word-addressable memory model.
 *
 * @author Deborah Hawkins
 * @author Rishi Dewan
 */
public class MainMemory {
    static int MEMSIZE = 4194304;
    int[] data;
    
    static String outPutFileName = "";
    
    public MainMemory() {
        data = new int[MEMSIZE];
        for (int i = 0; i < data.length; i++)
            data[i] = i; // data is same as word-address by default
    }

    public int read(int address) {
        return data[address];
    }

    public void write(int address, int value) {
        data[address] = value;
    }

    /**
     * Prints a section of memory to standard out as described in the assignment.
     */
    protected void print(String outPutFile) {
        System.out.println( "MAIN MEMORY:" );
        System.out.println( "Address    Words " );
        for (int i = 0; i < 128; i++) {
            System.out.print(IO.int_to_hex(i*8 + 4161280) + " ");
            for (int j = 0; j < 8; j++)
                System.out.print( IO.int_to_hex( data[i*8 + j + 4161280] ) + " " );
            System.out.println();  
        }
        
        outPutFileName = outPutFile;
        try (FileWriter write = new FileWriter(outPutFileName, true);
      			 BufferedWriter buffWrite = new BufferedWriter(write);
      	    		PrintWriter printWrite = new PrintWriter(buffWrite))
           {
        	printWrite.println( "MAIN MEMORY:" );
        	printWrite.println( "Address    Words " );
           
        	for (int i = 0; i < 128; i++) 
            {
            	printWrite.print(IO.int_to_hex(i*8 + 4161280) + " ");
                for (int j = 0; j < 8; j++)
                	printWrite.print( IO.int_to_hex( data[i*8 + j + 4161280] ) + " " );
                printWrite.println();  
            }
          }	
      	 	catch (IOException ex) 
      	 	{
                 Logger.getLogger(cache_sim.class.getName()).log(Level.SEVERE, null, ex);
             }
      	 	
    }
}
