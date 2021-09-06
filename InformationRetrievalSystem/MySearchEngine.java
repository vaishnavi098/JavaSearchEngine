import java.io.*;
import java.io.File;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MySearchEngine{
    static DecimalFormat DF = new DecimalFormat("#.###");
    File OutFile;
    public static String WORK_DIR;
    public static String QRY_DIR;
    public static String MASTER_DIR;
    public static String INDEX_DIR;
    public static String STOP_FILE;
    public static String INT_TERM_FILE;
    public static String INT_STEM_FILE;
    public static String INPUT_DIR;
    public static String INVERT_FILE;
    public static Double TOT_DOCS;
    public static String TEMP_FILE;
    public static String TEMP_TERM_OUT_FILE;

    public static String Q_INT_STEM_FILE;
    public static String Q_INT_TERM_FILE;
    public static String Q_TEMP_TERM_OUT_FILE;
    public static String Q_INVERT_FILE;
    public static String Q_TEMP_FILE;
    public static int NUM_DOCS;
    public static String INPUT_DIR_NAME_FILE;

    static MultiKeyHashMap<String,String,Double> tdMatrix = new  MultiKeyHashMap<String,String,Double>();
    static MultiKeyHashMap<String,String,Double> tqryMatrix = new  MultiKeyHashMap<String,String,Double>();
    configData CFD = new configData();

    MySearchEngine(){

        String EQ_regex="=";
        try{
            CFD.keyData("engConfig.cfg",EQ_regex);
            
        } catch (IOException e) { System.out.println("in trap");}
   
        MASTER_DIR=CFD.getData("MASTER_DIR");
        WORK_DIR=CFD.getData("WORK_DIR");
        QRY_DIR=CFD.getData("QRY_DIR");
        INPUT_DIR=CFD.getData("INPUT_DIR");
        INDEX_DIR=CFD.getData("INDEX_DIR");
        INVERT_FILE=INDEX_DIR+"\\"+CFD.getData("INVERT_FILE");
        INPUT_DIR_NAME_FILE=INDEX_DIR+"\\"+CFD.getData("INPUT_DIR_NAME_FILE");

        STOP_FILE=MASTER_DIR+"\\"+CFD.getData("STOP_FILE");

        INT_TERM_FILE=WORK_DIR+"\\"+CFD.getData("INT_TERM_FILE");
        INT_STEM_FILE=WORK_DIR+"\\"+CFD.getData("INT_STEM_FILE");

        TEMP_FILE=WORK_DIR+"\\"+CFD.getData("TEMP_FILE");
        TEMP_TERM_OUT_FILE=WORK_DIR+"\\"+CFD.getData("TEMP_TERM_OUT_FILE");

        Q_INT_STEM_FILE=WORK_DIR+"\\"+CFD.getData("Q_INT_STEM_FILE");
        Q_INT_TERM_FILE=WORK_DIR+"\\"+CFD.getData("Q_INT_TERM_FILE");
        Q_TEMP_TERM_OUT_FILE=WORK_DIR+"\\"+CFD.getData("Q_TEMP_TERM_OUT_FILE");
        Q_TEMP_FILE=WORK_DIR+"\\"+CFD.getData("Q_TEMP_FILE");
        Q_INVERT_FILE=INDEX_DIR+"\\"+CFD.getData("Q_INVERT_FILE");
        NUM_DOCS=Integer.parseInt(CFD.getData("NUM_DOCS"));
        OutFile= new File(CFD.getData("INT_TERM_FILE"));

        if(OutFile.exists()) {
            System.out.println("Initialization");
            OutFile.delete();
        }
    }

    public static void main(String[] argv) {
        int i;
        String[] tokens;
        for (i=0;i<argv.length;i++) System.out.println(""+argv[i] +"");

        MySearchEngine RF = new MySearchEngine();
        boolean indexOps=false;
        boolean searchOps=false;
        String idx="index";

        System.out.println(""+argv[0] +"");
            if ((idx.compareTo(argv[0]))==0) {
            indexOps=true;
            if(argv.length==4){
                INPUT_DIR=argv[1];
                INDEX_DIR=argv[2];
                STOP_FILE=argv[3];
                try{
                    BufferedWriter outBuff1 = new BufferedWriter(new FileWriter(INPUT_DIR_NAME_FILE));
                    outBuff1.write(INPUT_DIR); 
					outBuff1.flush();
                    outBuff1.close();  
                }catch(IOException e){
                    System.out.println("IOException  Verify Stack");
                }
            }else {
                System.out.println("Proceeding with Search Engine Defaults");
            }
        }
        String Qstr="search";

        if ((Qstr.compareTo(argv[0]))==0) {
            searchOps=true;
            System.out.println("inHere srch");
            if(argv.length>3){
                INDEX_DIR=argv[1];
                NUM_DOCS=Integer.parseInt(argv[2]);
				Qstr = "";
                for(int n=3;n<argv.length;n++) {  Qstr=Qstr+" "+argv[n]; }
				System.out.println(Qstr);
                try{
                    BufferedWriter outBuff = new BufferedWriter(new FileWriter(QRY_DIR+"\\"+"Query1.txt"));
                    outBuff.write(Qstr); 
					outBuff.flush();
                    outBuff.close();                
                }catch(IOException e){System.out.println("IOException  Verify Stack");
                }
            }
            			
            try{
                BufferedReader tBr = new BufferedReader(new FileReader(INPUT_DIR_NAME_FILE));
                String line = "";
                if ((line = tBr.readLine())!= null) {INPUT_DIR = tBr.readLine();}
				System.out.println(INPUT_DIR);
                tBr.close();
            }catch (IOException e) {System.out.println("IN IO Exception"); }
        } else { System.out.println("Proceeding with Search Engine Defaults"); }

        //System.out.println(INPUT_DIR+" is input dir");
        // start of index ops 

        if(indexOps==true) {
            File dir = new File(INPUT_DIR);
            String[] dir_files = dir.list();
            if ((dir_files == null) || (dir_files.length == 0)) {
                System.out.println("Files does not exist or is not a directory");
            } else {
                RF.TOT_DOCS=(double)dir_files.length;
                System.out.println("No of files considered for indexing :" + dir_files.length);
                String filenam;
                String filename;
                for (i = 0; i < dir_files.length; i++) {
                    filename = dir_files[i];
                    System.out.println("Working on File : " + INPUT_DIR+ "\\" + filename);

                    Checkfor t1 = new Checkfor(TEMP_FILE); //INT_TERM_FILE);
                    Stemmer S1 = new Stemmer();

                    String SPLIT_regex = t1.getRegexStr("SPLIT");
                    String URL_regex = t1.getRegexStr("URL");
                    String Email_regex = t1.getRegexStr("Email");
                    String IP_regex = t1.getRegexStr("IP");
                    String ACRY_regex = t1.getRegexStr("ACRY");
                    String QUOTES_regex = t1.getRegexStr("QUOTES");
                    //need to be repeated in loop
                    try{
                        t1.cutFile(INPUT_DIR+ "\\" + filename,SPLIT_regex,"SPLIT") ;
                        S1.stemToFile(TEMP_FILE,INT_STEM_FILE); 
                        t1.copyFile(INT_STEM_FILE,TEMP_FILE);
                        t1.cutTokens(INPUT_DIR+ "\\" + filename,URL_regex,"URL") ;
                        t1.cutTokens(INPUT_DIR+ "\\" + filename,Email_regex,"Email");
                        t1.cutTokens(INPUT_DIR+ "\\" + filename,IP_regex,"IP Add") ;
                        t1.cutTokens(INPUT_DIR+ "\\" + filename,ACRY_regex, " ACRO") ;
                        t1.cutTokens(INPUT_DIR+ "\\" + filename,QUOTES_regex, "QUOTES") ;
                        t1.out.close();
                        t1.appendToFile(INT_TERM_FILE,TEMP_FILE);
                        
                    } catch (IOException e) { System.out.println("in trap");}

                }  //for end

                //Start of Vector Operations
                VectOps Vops =new VectOps();
                Vops.removeStopAndDuplicateWords(STOP_FILE,INT_TERM_FILE);
                Vops.writeVectorToFile(INT_TERM_FILE);
                Vops.vectChunks.clear();
                //End of Vector Operations

                try{
                    //Start of HaSH operations
                    RF.loadTermstoHash(INPUT_DIR,INT_TERM_FILE);
                    RF.loadIDFtoHash(INT_TERM_FILE);

                    RF.writeHashtoInvertTab (INVERT_FILE, INT_TERM_FILE);
                    RF.tdMatrix.clear();
                } catch (IOException e) {  System.out.println("IOException in HashOperations");
                } catch (Exception e) {  System.out.println("Exception in HashOperations");
                }

                //else end
            }//end of index ops
            // }catch(Exception e){ System.println("Unknown Exception found.");
            //Start of search Operations

            if(searchOps==true){
                try{
                    File sdir = new File(INPUT_DIR);
                    String[] sdir_files = sdir.list();
                    if ((sdir_files == null) || (sdir_files.length == 0)) {
                        System.out.println("Files does not exist or is not a directory");
                        System.exit(1);
                    }
                    RF.loadTermWeightTab(INVERT_FILE);
                    RF.loadQueryTab("Query1.txt");
                    Double[] coSinResult= new Double[sdir_files.length+2];
                    System.out.println("dir lenght :"+sdir_files.length);
                    String filename;
                    String fName;
                    for (int it = 0; it < sdir_files.length; it++) {
                        fName = sdir_files[it];
                        filename=RF.getFileId(fName);
                        coSinResult[Integer.parseInt(filename)] = RF.getCosineSimilarity(filename);
                    }
                    // PUT SORT HERE.
                    for ( i = 0; i < sdir_files.length+2; i++) {
                        if(coSinResult[i]!=null)
                            System.out.println(i+".txt CoSine Value : "+coSinResult[i]);
                        }

                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(" Array Out of Bounds Exception or In sufficient Arguments .Need a Directory to work on ");
                } catch (IOException e) {
                    System.out.println(" Array Out of Bounds Exception or In sufficient Arguments .Need a Directory to work on ");
                } catch (Exception e) {
                    System.out.println(" Array Out of Bounds Exception or In sufficient Arguments .Need a Directory to work on ");
                } //try end of search ops
				//try end of searc//try end of search ops
				//try end of search ops
                
            }//if end of search ops

            } //end of
        } //end of main
            public Double getCosineSimilarity(String fileId) {
            Double coSinSim=0.0;
           Double sigmaDQ=0.0;
            Double sigmaDsqR=0.0;
            Double sigmaQsqR=0.0;
            String dataKey;//="";
            String qryKey;//="";
            if(tdMatrix.get(fileId)!=null){
                Map<String, Double> dVMap = tdMatrix.get(fileId);    
                Iterator dVMapIterator=dVMap.entrySet().iterator();
                //System.out.println(dVMap);
                while (dVMapIterator.hasNext())
                { 
                    Map.Entry dVSetMapEntry= (Map.Entry)dVMapIterator.next();
                    sigmaDsqR=sigmaDsqR+Math.pow((Double)dVSetMapEntry.getValue(),2);
                    dataKey=(String)dVSetMapEntry.getKey();

                    if(tqryMatrix.get("QRY")!=null){
                        Map<String, Double> dQMap = tqryMatrix.get("QRY");
                        Iterator dQMapIterator=dQMap.entrySet().iterator();
                        sigmaQsqR=0.0;
                        while (dQMapIterator.hasNext())
                        { 
                            Map.Entry dQSetMapEntry= (Map.Entry)dQMapIterator.next();
                            if(dQSetMapEntry.getValue()!=null){
                                qryKey=(String)dQSetMapEntry.getKey();
                                if(dataKey.equals(qryKey)) {
                                    sigmaDQ=sigmaDQ+((Double)dVSetMapEntry.getValue()*(Double)dQSetMapEntry.getValue());

                                }
                            }
                            sigmaQsqR=sigmaQsqR+Math.pow((Double)dQSetMapEntry.getValue(),2);
                           
                        }
                        sigmaQsqR=Math.pow(sigmaQsqR,0.5);
                    }

                }

            }

            sigmaDsqR=Math.pow(sigmaDsqR,0.5);
            coSinSim=sigmaDQ/(sigmaDsqR*sigmaQsqR);
            System.out.println(coSinSim+ "  Cosine'");
            return coSinSim;
        }//end of function
        public void loadTermstoHash(String inputDir, String intTermFile) throws IOException, FileNotFoundException, java.lang.Exception {
            BufferedReader Br = new BufferedReader(new FileReader(intTermFile));
            String line;
            while ((line = Br.readLine()) != null) {
                loadTermCount(inputDir,line);
                //System.out.println(tdMatrix.get(line,"1.txt")+ " hashdata ");    //System.out.println(line);
            }
        }

        public String getFileId(String fileName){
        String fId = null;
        if(fileName!=null)
            fId=fileName.substring(0,fileName.length()-4);

        return fId;
    }

    public void loadTermCount(String inDir, String term)throws Exception {
        int i;
        String[] tokens;
        Double termCount=0.0;
        try {
            File dir = new File(inDir);
            String[] dir_files = dir.list();
            if ((dir_files == null) || (dir_files.length == 0)) {
                System.out.println("Files does not exist or is not a directory");
            } else {

                
                String filename;
                Checkfor t2 = new Checkfor(INT_TERM_FILE); 
                for (i = 0; i < dir_files.length; i++) {
                    filename = dir_files[i];
                    //System.out.println("Working on File to load Term Count  for " +term + ": in " + inDir+ "\\" + filename);
                    termCount=0.0;
                    //Checkfor t2 = new Checkfor(INT_TERM_FILE); //);
                    termCount=(double)t2.getTermCount(inDir+ "\\" + filename,term, TEMP_TERM_OUT_FILE,"WORDS");
                    if(termCount>0.0) tdMatrix.put(term,getFileId(filename),termCount) ;
                    //if(termCount>0.0) System.out.println(term + "\t"+getFileId(filename)+".txt"+"\t"+termCount) ;

                }  //for end

            } //else end
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(" In sufficient Arguments .Need a Directory to work on ");
        }// catch end

    } //end of method

    public void loadTermCount_SAFE(String inDir, String term)throws Exception {
        int i;
        String[] tokens;
        Double termCount=0.0;
        try {
            File dir = new File(inDir);
            String[] dir_files = dir.list();
            if ((dir_files == null) || (dir_files.length == 0)) {
                System.out.println("Files does not exist or is not a directory");
            } else {

                //System.out.println("No of files considered for loading TermCount :" + dir_files.length);
                //String filenam;
                String filename;
                Checkfor t2 = new Checkfor(INT_TERM_FILE); 
                for (i = 0; i < dir_files.length; i++) {
                    filename = dir_files[i];
                    //System.out.println("Working on File to load Term Count  for " +term + ": in " + inDir+ "\\" + filename);
                    termCount=0.0;
                    //Checkfor t2 = new Checkfor(INT_TERM_FILE); //);
                    //termCount=(double)t2.getTermCount(inDir+ "\\" + filename,term, "WORDS");
                    if(termCount>0.0) tdMatrix.put(term,getFileId(filename),termCount) ;
                    //if(termCount>0.0) System.out.println(term + "\t"+filename+"\t"+termCount) ;

                }  //for end

            } //else end
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(" In sufficient Arguments .Need a Directory to work on ");
        }// catch end

    } //end of method

    public void loadIDFtoHash(String intTermFile)throws Exception {

        System.out.println(intTermFile);
        BufferedReader Br = new BufferedReader(new FileReader(intTermFile));
        String line;
        while ((line = Br.readLine()) != null) {
            tdMatrix.put(line,"iDf",calculateIDF(line));
            //System.out.println(line +"\t iDf \t"+(calculateIDF(line)));
            //System.out.println(tdMatrix.get(line,"1")+ " hashdata ");    //System.out.println(line);
        }
    }

    public Double calculateIDF(String term){
        if(tdMatrix.get(term)!=null){

            Map<String, Double> dVMap = tdMatrix.get(term);
            Double tCount=0.0, idf=0.0, docCount=0.0;
            Iterator dVMapIterator=dVMap.entrySet().iterator();
            //System.out.println(dVMap);
            while (dVMapIterator.hasNext())
            { 
                Map.Entry dVSetMapEntry= (Map.Entry)dVMapIterator.next();
                //tCount=tCount + (Double)dVSetMapEntry.getValue();
                if((double)dVSetMapEntry.getValue()!=0.0) { 
                    docCount++;
                }
            }
            //System.out.println(tdMatrix.get(line,"1.txt")+ " hashdata ");    //System.out.println(line);    idf=Math.log(TOT_DOCS/docCount);
            //System.out.println(tCount + " term count of  " + term +"  and doc count is " + docCount + " and idf is  " + Math.log(TOT_DOCS/docCount));
            idf=Math.log(TOT_DOCS/docCount);
            return idf;
        }
        else{
            return null;
        } 

    }//end of method
    public void writeHashtoInvertTab(String invertFile,  String intTermFile) throws IOException, FileNotFoundException, java.lang.Exception {
        BufferedWriter outBuff; //Public Static
        File OutFile;

        OutFile= new File(invertFile);
        if(OutFile.exists()) {
            OutFile.delete(); 
        }
        outBuff = new BufferedWriter(new FileWriter(OutFile,true));
        BufferedReader Br = new BufferedReader(new FileReader(intTermFile));
        String line;
        while ((line = Br.readLine()) != null) {

            Map<String, Double> dVMap = tdMatrix.get(line);
            Double tCount=0.0, idf=0.0, docCount=0.0;
            Iterator dVMapIterator=dVMap.entrySet().iterator();
            //System.out.print(line);
            outBuff.write(line);
            while (dVMapIterator.hasNext())
            { 
                Map.Entry dVSetMapEntry= (Map.Entry)dVMapIterator.next();
                //tCount=tCount + (Double)dVSetMapEntry.getValue();

                if(dVSetMapEntry.getValue()!=null){
                    if(dVSetMapEntry.getKey()=="iDf") {
                        //outBuff.write(","+ Math.round(1000.000*(double)dVSetMapEntry.getValue())/1000.000+"\n");
                        Double iDfVal=0.0;
                        iDfVal=Math.round(1000.000*(double)dVSetMapEntry.getValue())/1000.000; //rounded to three decimals
                        outBuff.write(","+iDfVal); //outBuff.flush();
                        // outBuff.write(","+dVSetMapEntry.getValue()+"\n");
                        //System.out.println(","+DF.format(dVSetMapEntry.getValue())+"\n");
                    }
                    else{
                        int keyVal;//=0;
                        keyVal=(int)Math.round((Double) dVSetMapEntry.getValue());
                        //System.out.print(","+dVSetMapEntry.getKey() +","+keyVal);
                        // System.out.print(","+dVSetMapEntry.getKey() +","+dVSetMapEntry.getValue());
                        //                        outBuff.write(","+dVSetMapEntry.getKey() +","+dVSetMapEntry.getValue());
                        outBuff.write(","+dVSetMapEntry.getKey() +","+keyVal);
                    }

                    // if((double)dVSetMapEntry.getValue()!=0.0) { docCount++; } 
                }
            } outBuff.write("\n");outBuff.flush();//System.out.println();       
        }
    }

    public void loadTermWeightTab(String invertFile)throws Exception {
        int i;
        String[] tokens;
        String str;
        Double termCount=0.0;
        String fileId;
        BufferedReader invertData = new BufferedReader(new FileReader(invertFile));

        try {
            if ((invertFile == null) ) {
                System.out.println("Files does not exist or is not a directory");
            } else {

                while ((str = invertData.readLine()) != null) {
                    tokens = str.split(",");
                    if(tokens.length >3) {
                        String term=tokens[0];
                        String strIdf=tokens[tokens.length-1];
                        Double idf = Double.parseDouble(strIdf);
                        //System.out.println("START LO uNNA befor for " +term+ "  "+idf);
                        i=1;
                        while (i < tokens.length-1) {
                            fileId=tokens[i];
                            termCount=  Double.parseDouble(tokens[i+1]);
                            //System.out.println("SuperSpecial Effect  "+termCount);
                            tdMatrix.put(fileId,term,termCount*idf) ;  //loading into the table file wise
                            i=i+2;
                            //System.out.println(fileId + "  "+term+ "  "+termCount*idf);
                            if(tdMatrix.get("idf",term)==null) { 
                                tdMatrix.put("idf",term,idf) ;
                                //System.out.println("idf for " + "  "+term+ " is "+tdMatrix.get("idf",term));
                            }
                        }  //for end
                        //tdMatrix.put("idf",term,idf) ;

                    }
                }

            } //else end
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(" ArrayIndexOutOfBoundsException caught while loading term table to HASH.");
        }// catch end

    } //end of method

    public void loadQryIDFtoHash(String intTermFile)throws Exception {

        System.out.println(intTermFile);
        BufferedReader Br = new BufferedReader(new FileReader(intTermFile));
        String line;
        while ((line = Br.readLine()) != null) {
            Double qIdf=0.0;
            qIdf=tdMatrix.get("idf",line);
            if(qIdf==null) qIdf=0.0;
            tqryMatrix.put("QRY",line,qIdf);
            System.out.println(line +"\t qiDf \t"+qIdf);
            //System.out.println(tdMatrix.get(line,"1")+ " hashdata ");    //System.out.println(line);
        }
    }

    public void loadQueryTab(String qFile)throws Exception {
        String[] tokens;
        String str;
        Double termCount=0.0;
        String fileId;

        //Checkfor t1 = new Checkfor("G:\\KKK\\SVM\\Vanga1.txt");//INT_TERM_FILE);
        MySearchEngine qRF = new MySearchEngine();
        Checkfor t2 = new Checkfor(Q_TEMP_FILE); //INT_TERM_FILE);
        Stemmer S2 = new Stemmer();
        qFile=QRY_DIR+"\\"+qFile; //adding path to File;
        BufferedReader qData = new BufferedReader(new FileReader(qFile));

        String SPLIT_regex = t2.getRegexStr("SPLIT");
        String URL_regex = t2.getRegexStr("URL");
        String Email_regex = t2.getRegexStr("Email");
        String IP_regex = t2.getRegexStr("IP");
        String ACRY_regex = t2.getRegexStr("ACRY");
        String QUOTES_regex = t2.getRegexStr("QUOTES");
        //need to be repeated in loop
        try{
            t2.cutFile(qFile,SPLIT_regex,"SPLIT") ;
            S2.stemToFile(Q_TEMP_FILE,Q_INT_STEM_FILE); 
            t2.copyFile(Q_INT_STEM_FILE,Q_TEMP_FILE);
            t2.cutTokens(qFile,URL_regex,"URL") ;
            t2.cutTokens(qFile,Email_regex,"Email");
            t2.cutTokens(qFile,IP_regex,"IP Add") ;
            t2.cutTokens(qFile,ACRY_regex, " ACRO") ;
            t2.cutTokens(qFile,QUOTES_regex, "QUOTES") ;
            t2.out.close();
            t2.copyFile(Q_TEMP_FILE,Q_INT_TERM_FILE);
            //for(int l=0;l<3;l++) System.out.println(t1.getTermCount(argv[0]+ "\\" + filename,"wing", "WORDS") +"   term Count");

        } catch (IOException e) { System.out.println("in trap");}

        VectOps Vops =new VectOps();
        Vops.removeStopAndDuplicateWords(STOP_FILE,Q_INT_TERM_FILE);
        Vops.writeVectorToFile(Q_INT_TERM_FILE);
        Vops.vectChunks.clear();

        qRF.loadTermstoHash(QRY_DIR,Q_INT_TERM_FILE);
        qRF.loadQryIDFtoHash(Q_INT_TERM_FILE);

        qRF.writeHashtoInvertTab(Q_INVERT_FILE, Q_INT_TERM_FILE);
        //qRF.tdMatrix.clear();

    }  // end Method

} //end of Class
