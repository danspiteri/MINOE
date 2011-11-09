

package minoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Stores search criteria information - like year, location and so forth.
 * @author Daniel Spiteri
 */
public class SearchCriteria implements Serializable {

    private Vector<String> years;
    private Vector<String> locations;
    private Vector<String> types;
    private Vector<String> labels;
    private List<String> documentList;
    private String returnType;
    private int slop;


    public SearchCriteria(){
        this.years     = new Vector<String>();
        this.locations = new Vector<String>();
        this.types     = new Vector<String>();
        this.labels    = new Vector<String>();
        this.documentList    = new ArrayList<String>();
        this.slop = 0;
    }

    public void addYear(String y){
        this.years.add(y);
    }
    public void addLocation(String l){
        this.locations.add(l);
    }
    public void addType(String t){
        this.types.add(t);
    }
    public void addLabel(String l){
        this.labels.add(l);
    }
    public void addDocument(String documentID){
        this.documentList.add(documentID);
    }

    // setters
    public void setSlop(int s){
        this.slop = s;
    }
    public void setSlop(String s){
        int slopint = Integer.valueOf(s);
        this.slop = slopint;
    }
    public void setReturnType(String type){
        this.returnType = type;
    }

    // getters
    public int getSize(){
        int size = 0;
        size += this.years.size() + this.locations.size() + this.types.size() + this.labels.size();
        return size;
    }
    public int getSlop(){
        return this.slop;
    }
    public List<String> getDocumentList(){
        return this.documentList;
    }
    public String getReturnType(){
        return this.returnType;
    }
    public Vector<String> getYears(){
        return this.years;
    }
    public Vector<String> getLabels(){
        return this.labels;
    }
    public Vector<String> getLocations(){
        return this.locations;
    }
    public Vector<String> getTypes(){
        return this.types;
    }

    @Override
    public String toString(){
        String rtn = "";
        Vector v = this.years;
        for (int i = 0; i < v.size(); i++) {
            rtn += v.get(i);
            rtn += " - ";
        }
        v = this.locations;
        for (int i = 0; i < v.size(); i++) {
            rtn += v.get(i);
            rtn += " - ";
        }
        v = this.types;
        for (int i = 0; i < v.size(); i++) {
            rtn += v.get(i);
            rtn += " - ";
        }
        v = this.labels;
        for (int i = 0; i < v.size(); i++) {
            rtn += v.get(i);
            rtn += " - ";
        }
        rtn += this.slop;
        return rtn;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

}
