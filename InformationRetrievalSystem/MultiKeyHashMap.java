import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultiKeyHashMap<Key1,Key2,Value> {


    private Map<Key1,Map<Key2,Value>> mkMap;

    public MultiKeyHashMap(){
        mkMap = new HashMap<Key1,Map<Key2,Value>>();
    }

   
    public Value put(Key1 first, Key2 second, Value val) {
        Map<Key2,Value> k2Map = null;
        if(mkMap.containsKey(first)) {
            k2Map = mkMap.get(first);
        }else {
            k2Map = new HashMap<Key2,Value>();
            mkMap.put(first, k2Map);
        }
        return k2Map.put(second, val);
    }

   
    public boolean containsKey(Key1 k1, Key2 k2) {
        if(mkMap.containsKey(k1)) {
            Map<Key2,Value> k2Map = mkMap.get(k1);
            return k2Map.containsKey(k2);
        }
        return false;
    }

    
    public boolean containsKey(Key1 k1) {
        return mkMap.containsKey(k1);
    }

    
    public Value get(Key1 k1, Key2 k2) {
        if(mkMap.containsKey(k1)) {
            Map<Key2,Value> k2Map = mkMap.get(k1);
            return k2Map.get(k2);
        }
        return null;
    }

   
    public Map<Key2,Value> get(Key1 k1) {
        return mkMap.get(k1);
    }

    
    public Value getBySubKey(Key2 k2) {
        for(Map<Key2,Value> m : mkMap.values()) {
            if(m.containsKey(k2))
                return m.get(k2);
        }
        return null;
    }

   
    public Value remove(Key1 k1, Key2 k2) {
        if(mkMap.containsKey(k1)) {
            Map<Key2,Value> k2Map = mkMap.get(k1);
            return k2Map.remove(k2);
        } 
        return null;
    }

    
    
    private Map<Key2,Value> remove(Key1 k1) {
        return mkMap.remove(k1);
    }

   
    public int size() {
        int size = 0;
        for(Map<Key2,Value> m : mkMap.values()) {
            size++;
            size += m.size();
        }
        return size;
    }

    
    public List<Value> getAllItems(){
        List<Value> items = new ArrayList<Value>();
        for(Map<Key2,Value> m : mkMap.values()) {
            for(Value v : m.values()) {
                items.add(v);
            }
        }
        return items;
    }

   
    public void clear() {
        for(Map<Key2,Value> m : mkMap.values())
            m.clear();

        mkMap.clear();
    }

    public static void main(String[] args){
        MultiKeyHashMap<String,String,Double> MultiKey = new  MultiKeyHashMap<String,String,Double>();
        Integer KD;
        int terms =10;
        int docs = 5;
        for (Integer i=0 ; i<terms;i++)
            for(Integer j=0; j<docs; j++){
                KD=i*j;
                MultiKey.put(i.toString(),j.toString(),(double)(i*j));
            }
         for (Integer i=0 ; i<terms;i++) {
             for(Integer j=0; j<docs; j++)
                System.out.print(MultiKey.get(i.toString(),j.toString())+" ");
             System.out.print("\n");
             System.out.println();
         }
        
        Integer k;
        k=123;
        System.out.println(k.toString());
        System.out.println(MultiKey.get("2","4")+"  ghlhlkhkkkkkkkkk");
        System.out.println(MultiKey.get("200","400")+"  ghlhlkhkkkkkkkkk");
        System.out.println(MultiKey.get("2000","4")+"  ghlhlkhkkkkkkkkk");

    }    
}
