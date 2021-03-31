import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class cache_sim {

	// Var for constructor
    private static int cache_capacity;
    private static int cache_blocksize;
    private static int cache_associativity;
    
    private static  boolean testFlag = false;
    
    static public String outPutFileName = null;
    static public String inPutFileName = null;
    
    static ArrayList <String> TraceFiles = new ArrayList<String>();
    
    final static int CACHE_READ = 0;
    final static int CACHE_WRITE = 1;
    
    public static void main(String[] args) throws IOException {
    
        cache_sim c = new cache_sim();
  
	if(!c.parseParams(args)) {
	    return;
	}
		
		if(testFlag == false)
		{	
			if(c.cache_capacity == 4 && c.cache_blocksize == 512 && c.cache_associativity == 16)
			{
				System.out.println("This block size and associativity throws error" );
				System.exit(0);
			}
				
				MainMemory mainmem = new MainMemory();
				
		        System.out.println("cache size is " + c.cache_capacity +
		        ", blocksize is " + c.cache_blocksize +
		        ", associativity is " + c.cache_associativity + "\n");
		        //System.out.println(io.hex_to_int("003f8008"));
		        Cache cache = new Cache(c.cache_capacity, c.cache_associativity, c.cache_blocksize, mainmem, outPutFileName);

		        String parseString;
		        String[] strings;
		        byte cc;
		        int read_write;
		        int address;
		        int data = 0;
		        // Read until a newline
		        
		        Iterator<String> iterateFiles = TraceFiles.iterator();
		        
		        while (iterateFiles.hasNext())
		        {
		        	parseString = iterateFiles.next();
		         /*   
		            while(cc != '\n')
		            {
		                if(cc == -1) 
		                { 
		                	break; 
		                }
		                parseString += (char)cc;
		                
		                cc = (byte)br.read();
		            }
		           */ 
		            strings = parseString.split("\\s");

		            if(strings.length < 2)
		            {
		                break;
		            }

		             // Read the first character of the line.
		             // It determines whether to read or write to the cache.
		             read_write = Integer.parseInt(strings[0]);

		             // Read the address (as a hex number)
		             address = IO.hex_to_int(strings[1]);

		             // If it is a cache write the we have to read the data
		             if(read_write == CACHE_WRITE)
		             {
		                 data = IO.hex_to_int(strings[2]);
		                 cache.write(address, data);
		                 //mainmem.write(address, data);
		             }

		             // If it is a cache write the we have to read the data
		             if(read_write == CACHE_READ)
		             {
		                 cache.read(address);
		                 //mainmem.read(address);
		             }
		        }
		        cache.print();
		        
		        try {
		          //io.main(new String[0]);
		        } catch (Exception ex) {
		            Logger.getLogger(cache_sim.class.getName()).log(Level.SEVERE, null, ex);
		        }
			}
		
			    else if(testFlag == true)
			    {
			    	 for(int cap = 4; cap < 128; cap*=2)
			         {
			             for(int block = 4; block < 1024; block*=2)
			             {
			                 for(int associativity = 1; associativity < 32; associativity *= 2)
			                 {
			                	 if(cap == 4 && block == 512 && associativity == 16 )
			                		 continue;
			                	 
			                     MainMemory mainmem = new MainMemory();
			                     	c.cache_capacity = cap;
			                     	c.cache_blocksize = block;
			                     	c.cache_associativity = associativity;
			                     	
			                     	  System.out.println("cache size is " + c.cache_capacity +
			              			        ", blocksize is " + c.cache_blocksize +
			              			        ", associativity is " + c.cache_associativity + "\n");
			                     	  
			                     	  String outPutFileNameTest = "output c" +
			                     	  Integer.toString(cap) +"_b" + 
			                     	  Integer.toString(block) + "_a" +
			                     	  Integer.toString(associativity) + ".txt";
			                     	  
			              			        //System.out.println(io.hex_to_int("003f8008"));
			              			        Cache cache = new Cache(c.cache_capacity, c.cache_associativity, c.cache_blocksize, mainmem, outPutFileNameTest);

			      
			        String parseString;
			        String[] strings;
			        byte cc;
			        int read_write;
			        int address;
			        int data = 0;
			        // Read until a newline
			        
			        Iterator<String> iterateFiles = TraceFiles.iterator();
			        
			        while (iterateFiles.hasNext())
			        {
			        	parseString = iterateFiles.next();
			         /*   
			            while(cc != '\n')
			            {
			                if(cc == -1) 
			                { 
			                	break; 
			                }
			                parseString += (char)cc;
			                
			                cc = (byte)br.read();
			            }
			           */ 
			            strings = parseString.split("\\s");

			            if(strings.length < 2)
			            {
			                break;
			            }

			             // Read the first character of the line.
			             // It determines whether to read or write to the cache.
			             read_write = Integer.parseInt(strings[0]);

			             // Read the address (as a hex number)
			             address = IO.hex_to_int(strings[1]);

			             // If it is a cache write the we have to read the data
			             if(read_write == CACHE_WRITE)
			             {
			                 data = IO.hex_to_int(strings[2]);
			                 cache.write(address, data);
			                 //mainmem.write(address, data);
			             }

			             // If it is a cache write the we have to read the data
			             if(read_write == CACHE_READ)
			             {
			                 cache.read(address);
			                 //mainmem.read(address);
			             }
			        }
			        cache.print();
			        
			        try {
			          //io.main(new String[0]);
			        } catch (Exception ex) {
			            Logger.getLogger(cache_sim.class.getName()).log(Level.SEVERE, null, ex);
			        }
				        

			                 }
			             }
			         }
			        
			    	 Cache.testParamteres();
			    }
		}

    public boolean parseParams(String[] args)
    {
	//needed for the parsing of command line options
	int c;
	boolean c_flag, b_flag, a_flag;
	boolean errflg = false;

	c_flag = b_flag = a_flag = errflg = false;
	
	// Create file to read in data
	

	for(int i = 0; i < args.length; i++) {
		
	    c = args[i].charAt(1);

	    switch (c) {
	    case 'c':
                cache_capacity = Integer.parseInt(args[i].substring(2, args[i].length()));
                c_flag = true;
                break;
	    case 'b':
                cache_blocksize = Integer.parseInt(args[i].substring(2, args[i].length()));
                b_flag = true;
                break;
	    case 'a':
                cache_associativity = Integer.parseInt(args[i].substring(2, args[i].length()));
                a_flag = true;
                break;
	    case ':':       /* -c without operand */
		System.err.println("Option -" + c + " requires an operand\n");
                errflg = true;
                break;
	    case '?':
		System.err.println("Unrecognized option: -" + c + "\n");
                errflg=true;
                
	    case 't':
	    	testFlag = true;
	    	break;
	    
	    case 'i':
	    	Trace(args[i+1]);
	    	break;
	    	
	    case 'o':
	    	outPutFileName = args[i + 1];
	    	break;
        
	    }
	}
	
	/*String fileExtension = " ";
	
	if(args.length > 3)
	{
		// File to read through
		
			String inputFileName = "mem.trace";
				
			File inputFile = new File(inputFileName);
			
			boolean error;
		
		for(int i = 3; i < args.length; i ++)
		{
			if(i == 3)
				Trace(args[i]);
			else if(i == 4)
			{
				outPutFileName = args[i];
			}
			else if(i == 5)
			{
				if (args[i] ==  "t")
					System.out.println("Test");
			}
			
		}
	}
*/
	//check if we have all the options and have no illegal options
	if(errflg || !c_flag || !b_flag || !a_flag) {
	    System.err.println("usage: java Cache -c<capacity> -b<blocksize> -a<associativity>\n");
	    return false;
	}

	return true;

    }
    
    public static ArrayList<String> Trace(String inputFiletxt) 
    {
    	
    	 // Create a file reader
    	
    	File inputFile = new File(inputFiletxt);
    	
    	System.out.println(inputFiletxt);
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));
			
			String file = fileReader.readLine();
			
			while(file != null)
			{
				TraceFiles.add(file);
				
				file = fileReader.readLine();
			}
			
			fileReader.close();
		}
		
		catch (IOException e1) 
		{
			System.out.println("File Not Found");
			System.exit(0);
		}
    	return TraceFiles ;
    	
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