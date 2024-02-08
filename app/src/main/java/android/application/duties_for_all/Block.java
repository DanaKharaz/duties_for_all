package android.application.duties_for_all;

import java.util.ArrayList;

public class Block {
    //variables
    private String time;
    private int nPeople;
    private final boolean predp;
    private final boolean dp1;
    private final boolean dp2;
    private final boolean locals;
    private final boolean nonLocals;
    private ArrayList<Student> people;

    //constructors
    public Block(String time, int nPeople, boolean predp, boolean dp1, boolean dp2, boolean locals, boolean nonLocals) {
        this.time = time;
        this.nPeople = nPeople;
        this.predp = predp;
        this.dp1 = dp1;
        this.dp2 = dp2;
        this.locals = locals;
        this.nonLocals = nonLocals;
        people = new ArrayList<>();
    }
    public Block(String time, int nPeople) {
        this.time = time;
        this.nPeople = nPeople;
        this.predp = true;
        this.dp1 = true;
        this.dp2 = true;
        this.locals = true;
        this.nonLocals = true;
        people = new ArrayList<>();
    }
    public Block() {
        this.time = "Select";
        this.nPeople = 0;
        this.predp = true;
        this.dp1 = true;
        this.dp2 = true;
        this.locals = true;
        this.nonLocals = true;
        people = new ArrayList<>();
    }

    //getters
    public String getTime() {return time;}
    public int getnPeople() {return nPeople;}
    public boolean isPredp() {return predp;}
    public boolean isDp1() {return dp1;}
    public boolean isDp2() {return dp2;}
    public boolean isLocals() {return locals;}
    public boolean isNonLocals() {return nonLocals;}
    public ArrayList<Student> getPeople() {return people;}
    //setters
    public void setTime(String time) {this.time = time;}
    public void setnPeople(int nPeople) {this.nPeople = nPeople;}
    public void setPeople(ArrayList<Student> people) {this.people = people;}
}

