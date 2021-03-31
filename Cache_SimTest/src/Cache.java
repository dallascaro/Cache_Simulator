import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Cache {
    int capacity;
    int associativity;
    int blocksize;
    
    int globalCapacity;
    int globalAssociativity;
    int globalBlocksize;
    
    int numWrites;
    int numReads;
    int numWriteMisses;
    int numReadMisses;
    private int numEvictions;
    
    String outPutFileName;
    
    static ArrayList<MissComb> missrate = new ArrayList<MissComb>();
    static ArrayList<EvictComb> evictions = new ArrayList<EvictComb>();
    static ArrayList<AccessComb> access = new ArrayList<AccessComb>();
    
    MainMemory memory; //Memory to which reads and writes will occur on
    Set[] sets;
    DecimalFormat format = new DecimalFormat("#.######"); //formats a double to 6 decimal places.

    /**
     *
     * @param capacity      the size in words of the cache
     * @param associativity the number of blocks in each set
     * @param blocksize     the size in bytes of each block
     * @param memory        a reference to main memory
     */
    public Cache( int capacity, int associativity, int blocksize, MainMemory memory, String outPutFile) {
        
        // initialize counters
        numWrites = 0;
        numReads = 0;
        numWriteMisses = 0;
        numReadMisses = 0;
        numEvictions = 0;
        
        outPutFileName = outPutFile;

        // initialize values of the cache
        this.capacity = capacity * 1024 / 4;
        this.associativity = associativity;
        this.blocksize = blocksize / 4;
        this.memory = memory;
        
        this.globalCapacity = capacity;
        this.globalAssociativity = associativity;
        this.globalBlocksize = blocksize;
        
        sets = new Set[capacity * 1024 / (associativity * blocksize)];
        //sets = new Set[ this.capacity / (this.associativity * this.blocksize) ];

        // create the sets that make up this cache
        for (int i = 0; i < sets.length; i++)
            sets[i] = new Set(this.associativity, this.blocksize);
    }

    public int read( int address ) {
        
        // if the block isn't found in the cache, put it there
        if ( !isInMemory(address) ) {
            allocate(address);
            numReadMisses++;
        }

        // calculate what set the block is in and ask it for the data
        Set set = sets[ ( address / blocksize ) % sets.length ];
        int blockoffset = address % blocksize;
        numReads++;
        return set.read( getTag(address), blockoffset );
    }

    public void write( int address, int data ) {

        // if the block isn't found in the cache, put it there
        if ( !isInMemory(address) ) {
            allocate(address);
            numWriteMisses++;
        }

        // calculate what set the block is in and ask it for the data
        int index = ( address / blocksize ) % sets.length;
        int blockoffset = address % blocksize;
        Set set = sets[index];
        numWrites++;
        set.write( getTag(address), blockoffset, data );
    }
    public void print() 
    {
    	if(outPutFileName == null)
    	{
    		outPutFileName = "default outPutFile";
    	}
    	
        System.out.println( "STATISTICS");
        System.out.println("Misses:");
        
        int totalmiss = numReadMisses + numWriteMisses;
        double totalmissrate = ((double)numReadMisses + (double)numWriteMisses)/((double)numReads+(double)numWrites);
        double totalreadmissrate = ((double)numReadMisses/(double)numReads);
        double totalwritemissrate = ((double)numWriteMisses/(double)numWrites);
       
        System.out.println("Total: " + totalmiss +
        " DataReads: " + numReadMisses + " DataWrites: " + numWriteMisses);
        System.out.println("Miss rate:");
        System.out.println("Total: "+ format.format(totalmissrate) + " DataReads: " + format.format(totalreadmissrate) + " DataWrites: "+ format.format(totalwritemissrate)); //TODO
        System.out.println("Number of Dirty Blocks Evicted from the Cache: " + numEvictions);
        System.out.println();

        System.out.println("CACHE CONTENTS");
      //  System.out.println("Set\tV\tTag\tD\tWord0\tWord1\tWord2\tWord3\tWord4\tWord5\tWord6\tWord7"); //TODO
        String header = "Set\tV\tTag\tD";
        
        for(int i = 0; i < blocksize; i ++)
       	{
       		header = header + "\tWord" + i;
       	}
        
        System.out.println(header);
        
        for (int i = 0; i < sets.length; i++)
        {
        for (Block block : sets[i].blocks)
        System.out.println((Integer.toHexString(i)) + whitespaceformat(Integer.toHexString(i).length()) + block.toString());
        }
        System.out.println();

        try (FileWriter write = new FileWriter(outPutFileName, false);
   			 BufferedWriter buffWrite = new BufferedWriter(write);
   	    		PrintWriter printWrite = new PrintWriter(buffWrite))
        {
   		printWrite.println();
   		printWrite.println("Capacity " + globalCapacity + " Associativity " + globalAssociativity + " BlockSize " + globalBlocksize);
   	 printWrite.println( "STATISTICS");
   	printWrite.println("Misses:");
     
   	printWrite.println("Total: " + totalmiss +
    	        " DataReads: " + numReadMisses + " DataWrites: " + numWriteMisses);
   	printWrite.println("Miss rate:");
   	printWrite.println("Total: "+ format.format(totalmissrate) + " DataReads: " + format.format(totalreadmissrate) + " DataWrites: "+ format.format(totalwritemissrate)); //TODO
   	printWrite.println("Number of Dirty Blocks Evicted from the Cache: " + numEvictions);
   	printWrite.println();

   	printWrite.println("CACHE CONTENTS");
   	printWrite.println(header); 
   	
   	//String header = "Set\\tV\\tTag\\tD\\tWord0\\tWord1\\tWord2\\tWord3\\tWord4\\tWord5\\tWord6\\tWord7";
   
    	        for (int i = 0; i < sets.length; i++)
    	        {
    	        for (Block block : sets[i].blocks)
    	        printWrite.println((Integer.toHexString(i)) + whitespaceformat(Integer.toHexString(i).length()) + block.toString());
    	        }
    	        printWrite.println();
   		
          } 
   	 
   	 	catch (IOException ex) {
              Logger.getLogger(cache_sim.class.getName()).log(Level.SEVERE, null, ex);
          }
        
        //NOTE: The next 3 lines of code were for testing purposes only.
        
        missrate.add(new MissComb(this.associativity,this.capacity/256,this.blocksize*4,Double.valueOf(format.format(totalmissrate))));
        evictions.add(new EvictComb(this.associativity,this.capacity/256,this.blocksize*4,numEvictions));
        access.add(new AccessComb(this.associativity,this.capacity/256,this.blocksize*4,((double)(numEvictions+numReadMisses+numWriteMisses)/(numReads+numWrites))));

        this.flush();
        memory.print(outPutFileName);
    }
    private String whitespaceformat(int s)
    {
        String ws = "";
        for(int i = 0; i < (11 - s); i++)
            ws += " ";
        return ws;
    }

    // should only be called if we already know the address is not in cache
    private void allocate( int address ) {

        int index = ( address / blocksize ) % sets.length;
        Set set = sets[index];
        Block evictee = set.getLRU();

        // If the least recently used block is dirty, write it back to
        // main memory.
        if ( evictee.isDirty() ) {
            numEvictions++;
            int[] evicteeData = evictee.readAllData();
            int evicteeAddress = ( evictee.getTag() * sets.length + index ) * blocksize;
            for (int i = 0; i < blocksize; i++)
                memory.write(evicteeAddress + i, evicteeData[i]);
        }

        // Then write the block with the data we need
        int[] newData = new int[blocksize];
        int SAddress = (address/blocksize)*blocksize;
        for (int i = 0; i < blocksize; i++)
            newData[i] = memory.read(SAddress + i);
        evictee.writeBlock( getTag(address), newData );
    }

    private boolean isInMemory( int address ) {
        int index = ( address / blocksize ) % sets.length;
        return sets[index].findBlock( getTag(address) ) != null;
    }

    private int getTag(int address) {
        //System.out.println((int) ( (double)address / ((double)sets.length * (double)blocksize)));
        return ( address / (sets.length * blocksize));
    }
    private void flush()
    {
            for(int i = 0; i < sets.length; i++)
            {
                for(Block block : sets[i].blocks)
                {
                    if(block.isDirty())
                        wb(block,i);
                }
            }
    }
    private void wb(Block b, int in)
    {
        int add = ((b.getTag()*sets.length)+in) * this.blocksize;
        int[] data = b.readAllData();
        for(int i = 0; i < blocksize; i++)
        {
            memory.write(add+i, data[i]);
        }
    }

	public static void testParamteres() 
	{
		 try (FileWriter writeTestPar = new FileWriter("testParameters.txt", false);
	   			 BufferedWriter buffWritePar = new BufferedWriter(writeTestPar);
	   	    		PrintWriter printWritePar = new PrintWriter(buffWritePar))
	        {
			 printWritePar.println();
			 printWritePar.println("Test Parameters");
			 printWritePar.println();
				  sortMisses(missrate);
			        for(MissComb m : missrate)
			        	printWritePar.println(m.toMissString());
			        printWritePar.println();

			        sortEvicts(evictions);
			        for(EvictComb e : evictions)
			        	printWritePar.println(e.toEvictString());
			        printWritePar.println();

			        sortAccess(access);
			        for(AccessComb a : access)
			        	printWritePar.println(a.toAccessString());
			        printWritePar.println();
	        }
	   	 	catch (IOException ex) {
	              Logger.getLogger(cache_sim.class.getName()).log(Level.SEVERE, null, ex);
	          }
	}
	
	 public static void sortMisses(ArrayList<MissComb> al)
	    {
	        for(int i = 1; i < al.size(); i++)
	        {
	            for(int j = 0; j < al.size()-i; j++)
	            {
	                if(al.get(j).getMiss() > al.get(j+1).getMiss())
	                {
	                    MissComb temp = al.get(j);
	                    al.set(j, al.get(j+1));
	                    al.set(j+1, temp);
	                }
	            }
	        }
	    }
	    public static void sortEvicts(ArrayList<EvictComb> al)
	    {
	        for(int i = 1; i < al.size(); i++)
	        {
	            for(int j = 0; j < al.size()-i; j++)
	            {
	                if(al.get(j).getEvict() > al.get(j+1).getEvict())
	                {
	                    EvictComb temp = al.get(j);
	                    al.set(j, al.get(j+1));
	                    al.set(j+1, temp);
	                }
	            }
	        }
	    }
	    public static void sortAccess(ArrayList<AccessComb> al)
	    {
	        for(int i = 1; i < al.size(); i++)
	        {
	            for(int j = 0; j < al.size()-i; j++)
	            {
	                if(al.get(j).getAccessTime() > al.get(j+1).getAccessTime())
	                {
	                    AccessComb temp = al.get(j);
	                    al.set(j, al.get(j+1));
	                    al.set(j+1, temp);
	                }
	            }
	        }
	    }
}