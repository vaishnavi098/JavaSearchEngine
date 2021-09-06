import java.io.*;
import java.io.File;
import java.util.HashMap;
public class configData{

    static String EQ_regex = "=";
    HashMap<String,String> KD = new HashMap<String,String>();
    configData(){
    }

    void keyData(String FilNam, String regex) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(FilNam));
        String str;
        String[] tokens;

        while ((str = in.readLine()) != null) {
            tokens = str.split(regex);
            if (tokens.length>2) {System.out.println("Error in Config File");}
            else 
                KD.put(tokens[0].toString(),tokens[1].toString());

            //for(int j=0;j<tokens.length;j++){  
            //  System.out.print(tokens[j] + " ");
            // System.out.println(tokens[j] + "  " + tokens[j].length());
            //}
           // System.out.println();
        }

    }

    String getData(String str) {
   
         return KD.get(str);
    }
    public static void main(String[] argv)throws Exception {
        configData CFD = new configData();

        try{
            CFD.keyData("G:\\KKK\\SVM\\engConfig.cfg",EQ_regex);
            
            System.out.println(CFD.getData("MASTER_DIRECTORY"));

        } catch (IOException e) { System.out.println("in trap");}
    } //end of main
} //end of Class

