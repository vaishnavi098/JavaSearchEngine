import java.io.*;
import java.io.File;
import java.util.*;
public class VectOps {

    static Vector<String> vectChunks = new Vector<String>(500, 200);//>;
    static Vector<String> vectStopWords = new Vector<String>(500, 200);//(500, 200);
    static Vector<String> vectFilteredChunks = new Vector<String>(500, 200);

    public VectOps(){
    }

    public  void removeDuplicateWords() {
        for(int i=0;i<vectChunks.size();i++){
            String tempTerm;
            tempTerm=vectChunks.get(i);
            if(tempTerm!=null){
                while(vectChunks.contains(tempTerm)){
                    vectChunks.removeElement(tempTerm);
                }

            }
            vectChunks.add(i,tempTerm);
            vectChunks.trimToSize();

        }
    }

    public  void removeStopAndDuplicateWords(String sFile, String tFile) {
        int i;
        try {

            BufferedReader rawChunks  = new BufferedReader(new FileReader(tFile));
            BufferedReader stopWords  = new BufferedReader(new FileReader(sFile));
            String str;
            //String filename;
            try {
                while ((str = rawChunks.readLine()) != null) {
                    vectChunks.addElement(str);
                }
                vectChunks.trimToSize();
                System.out.println("Capacity after Additions : " + vectChunks.capacity());
            } catch (IOException e)  {
            }

            try {
                while ((str = stopWords.readLine()) != null) {
                    //vectStopWords.addElement(str);

                    while(vectChunks.contains(str)) {
                        //System.out.println("REMOVING  " + str);
                        vectChunks.remove(str);
                    }

                }
                System.out.println("Capacity after ALL additions and trimming II: " + vectChunks.size());
            } catch (IOException e)  {
            }

            LinkedHashSet<String> setTerms =new LinkedHashSet<String>(vectChunks);
            vectChunks.clear();
            vectChunks.addAll(setTerms);
            //Code to remove Duplicates ends here//


        } catch (FileNotFoundException e) {
        }

    }

    void writeVectorToFile(String tFile){
        try{
            BufferedWriter outV = new BufferedWriter(new FileWriter(tFile));
            
            int vectSize;
            vectSize=vectChunks.size();
            String[] StrArray = new String[vectSize];

            vectChunks.copyInto(StrArray);
            System.out.println(vectSize + "  SIZE U");

            for(int i=0; i < vectChunks.size(); i++) {
                outV.write(StrArray[i]+"\n");
                
            }
            outV.close();outV.flush();

        } catch (IOException e)  {
        }    

    }

    public static void main(String argv[]) {
        VectOps Vops = new VectOps();
        //Vops.removeStopAndDuplicateWords(STOP_FILE,INT_TERM_FILE);
        Vops.removeDuplicateWords();

    }

}

/* //commented block that writes into a serialised file.              
ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream( "G:\\KKK\\SVM\\Vanga2.txt" ) );
oos.writeObject(vectChunks);
oos.close ();
 */
