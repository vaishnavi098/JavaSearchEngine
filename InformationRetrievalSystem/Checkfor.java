import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checkfor {

    static String URL_regex = "((http|https|ftp|file)://)(www.)?"
        + "[a-zA-Z0-9@:%._\\+~#?&//=]"
        + "{2,256}\\.[a-z]"
        + "{2,6}\\b([-a-zA-Z0-9@:%"
        + "._\\+~#?&//=]*)";

    static String Email_regex = "\\b[a-zA-Z0-9._%+-]*@[a-zA-Z0-9.-]*\\.[a-zA-Z]{2,4}\\b";

    static String IPDigits= "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
    static String IP_regex = "\\b"+IPDigits +"\\."+ IPDigits+ "\\."+ IPDigits +"\\."+IPDigits+"\\b";

    static String ACRY_regex = "([A-Z](\\.)?)+\\b" ;

    static String QUOTES_regex = "'([^'])*'";

    static String SPLIT_regex="[\\: \\; \\s \\. \\, \" \\' \\( \\) \\? \\! \\- \\/]";
    static String EQ_regex = "=";
    public static BufferedWriter out; 
    File OutFile;

    Checkfor( String Ofile){

        try{
            OutFile= new File(Ofile);
            if(OutFile.exists()) {
                OutFile.delete();
            }
            out = new BufferedWriter(new FileWriter(OutFile,true));
        }catch(IOException e){
            System.out.println("Caught in Exception");

        }
    }

    public String getRegexStr(String regName){
        String regexPat;
        if(regName=="ACRY"){
            regexPat=ACRY_regex;
        }else if(regName=="QUOTES"){
            regexPat=QUOTES_regex;
        }else if(regName=="URL"){
            regexPat=URL_regex;
        }else if(regName=="Email"){
            regexPat=Email_regex;
        }else if(regName=="IP"){
            regexPat=IP_regex;
        }else if(regName=="EQ"){
            regexPat=EQ_regex;
        }else {
            regexPat=SPLIT_regex;
        }
        //System.out.println(" EJKKADA " + regexPat);

        return regexPat;
    }

    public boolean CheckResultEmail(String Asalu){
        Scanner sc = new Scanner(Asalu);
        String TokenPeice = sc.next();
        //String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        boolean result = TokenPeice.matches(Email_regex);
        return result;
    }

    public boolean CheckResultURL(String Asalu) {
        try {
            Pattern patt = Pattern.compile(URL_regex);
            Matcher matcher = patt.matcher(Asalu);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean CheckResultIP(String Asalu) {
        try {
            Pattern patt = Pattern.compile(IP_regex);
            Matcher matcher = patt.matcher(Asalu);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean CheckResultACRY(String Asalu) {
        try {
            Pattern patt = Pattern.compile(ACRY_regex);
            Matcher matcher = patt.matcher(Asalu);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean CheckResultQUOTE(String Asalu) {
        try {
            Pattern patt = Pattern.compile(QUOTES_regex);
            Matcher matcher = patt.matcher(Asalu);
            System.out.println(Asalu + "   "  + QUOTES_regex);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    void appendToFile(String mainFile, String appFile) throws IOException {
        BufferedReader inBuff = new BufferedReader(new FileReader(appFile));
        int counter=0;
        BufferedWriter outBuff = new BufferedWriter(new FileWriter(mainFile,true));
        String str;
        while ((str = inBuff.readLine()) != null) {
            outBuff.write(str+"\n");
        }
        outBuff.close();
        inBuff.close();
    }

    void copyStrToFile(String srcStr, String destFile){

        try{
            BufferedWriter outBuff = new BufferedWriter(new FileWriter(destFile));
            outBuff.write(srcStr); outBuff.flush();
            outBuff.close();                
        }catch(IOException e){System.out.println("IOException  Verify Stack");
    }
}

void copyFile(String srcFile, String destFile){
FileInputStream iStream = null;
FileOutputStream oStream = null;
try{
File iFile=new File(srcFile);
File oFile=new File(destFile);

iStream = new FileInputStream(iFile);
oStream = new FileOutputStream(oFile);
byte[] buff = new byte[1024];
int len;
while((len=iStream.read(buff)) >0) {
oStream.write(buff,0,len);
}
oStream.close();
iStream.close();
}catch(IOException e){System.out.println("IOException  Verify Stack");
}
}

void cutFile(String FilNam, String regex,String typ) throws IOException {
BufferedReader in = new BufferedReader(new FileReader(FilNam));
int counter=0;
//System.out.println("checking tye " + typ);
if(typ=="true"){
out = new BufferedWriter(new FileWriter(OutFile,true));
} else {
out = new BufferedWriter(new FileWriter(OutFile));
}
String str;
String[] tokens;

while ((str = in.readLine()) != null) {
if (str.endsWith("-")) {
//System.out.println(str.charAt(str.length()-1)+ "  gaba pentine");
str=str.substring (0,str.length()-1);
str.trim();
str = str + in.readLine();
}
//System.out.println(str + " bahu baagu");
//tokens = str.split("[\\: \\; \\s \\. \\, \" \\' \\( \\) \\? \\! \\- \\/]");
tokens = str.split(regex);
//System.out.println(tokens.length + "braco");

for(int j=0;j<tokens.length;j++){  
if(tokens[j].length()>2){ 
out.write(tokens[j]+"\n"); out.flush();
//System.out.println(tokens[j] + "  " + tokens[j].length());
}

}
}out.close();

}

void cutTokens(String FilNam, String regex, String typ)  { //throws IOException
//Pattern p1 = Pattern.compile("[A-Za-z][a-z]+");
try{
Pattern p1 = Pattern.compile(regex);
BufferedReader r = new BufferedReader(new FileReader(FilNam));
out = new BufferedWriter(new FileWriter(OutFile,true));
// BufferedReader r = new BufferedReader(new FileReader("1.txt"));
String line;
int counter=0;

//System.out.println("checking type " + typ);
while ((line = r.readLine()) != null) {
Matcher m = p1.matcher(line);
//if(typ=="QUOTES") {System.out.println(line);}
while (m.find()) {
counter++;
//System.out.println(m.group(0) + "   "+ typ +"  " + counter+"  " + m.group(0).length());
int s1 = m.start(0);
int e1 = m.end(0);
out.write((String)(m.group(0))+"\n");out.flush();
//out.append(m.group(0)+"\n");out.flush();
//System.out.println(line.substring(s1, e1));
//System.out.println(m.group(0)+" group mn");
}
}
out.close();
} catch(IOException e) { System.out.println("Inexception\n");
}
}

int  getTermCount(String inFileNam, String regex, String tempFileOut, String typ) throws IOException {
//Pattern p1 = Pattern.compile("[A-Za-z][a-z]+");
File oFile =new File(tempFileOut);
if(oFile.exists()) {
oFile.delete();
}

Stemmer S3 = new Stemmer();// BufferedReader r = new BufferedReader(new FileReader("1.txt"));
S3.stemToFile(inFileNam,tempFileOut);
//System.out.println(tempFileOut+"  temp file out");
Pattern p1 = Pattern.compile(regex);
BufferedReader r = new BufferedReader(new FileReader(tempFileOut));
String line;
int counter=0;

while ((line = r.readLine()) != null) {
Matcher m = p1.matcher(line);
//System.out.println("checking type lines  " + line);
//if(typ=="QUOTES") {System.out.println(line);}
while (m.find()) {
counter++;
//System.out.println(m.group(0) + "   "+ typ +"  " + counter+"  " + m.group(0).length());
int s1 = m.start(0);
int e1 = m.end(0);
//out.append(m.group(0)+"\n");
//System.out.println(line.substring(s1, e1));
//System.out.println("checking type lines  " + line + "Found  "+m.group(0));
}
}
r.close();
return(counter);
//out.close();
}

public static void main(String[] args) {
Checkfor t1 = new Checkfor(args[1]);
// System.out.println(t1.getRegexStr("SPLIT"));
// System.out.println( t1.getRegexStr("URL"));
// System.out.println(t1.getRegexStr("Email"));
// System.out.println( t1.getRegexStr("IP"));
// System.out.println(t1.getRegexStr("ACRY"));
// System.out.println(t1.getRegexStr("QUOTES"));

try{
t1.cutFile(args[0],SPLIT_regex,"SPLIT") ;
t1.cutTokens(args[0],URL_regex,"URL") ;
t1.cutTokens(args[0],Email_regex,"Email");
t1.cutTokens(args[0],URL_regex,"URL") ;
t1.cutTokens(args[0],IP_regex,"IP") ;
t1.cutTokens(args[0],ACRY_regex, " ACRY") ;
t1.cutTokens(args[0],QUOTES_regex, "QUOTES") ;
System.out.println(t1.getTermCount(args[0],"BIHAR",args[1], "WORDS")) ;
//out.close();

} catch (IOException e) { System.out.println(args[0]+" : FILE Does Not Exits");}
}
}

