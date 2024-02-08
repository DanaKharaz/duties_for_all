package android.application.duties_for_all;

import java.util.ArrayList;


public class DutyList {
    //variables
    private Date date;
    private int nStudents;
    private ArrayList<Student> students;
    private int nBlocks;
    private ArrayList<Block> blocks;
    private int type; //0 = dining hall; 1 = cow; 2 = chicken; 3 = visitor center; 4 = other
    private String title;
    private ArrayList<Student> extraStudents;

    //constructors
    public DutyList(Date date, int nStudents, int nBlocks, int type, String title) {
        this.date = date;
        this.nStudents = nStudents;
        this.nBlocks = nBlocks;
        this.type = type;
        this.title = title;
    }
    public DutyList(String title, int nBlocks, ArrayList<Block> blocks) {
        this.nBlocks = nBlocks;
        this.blocks = new ArrayList<>();
        this.blocks.addAll(blocks);
        this.title = title;
    }
    public DutyList(Date date, int nStudents, int type) {
        this.date = date;
        this.nStudents = nStudents;
        this.type = type;
        this.students = new ArrayList<>();
        this.extraStudents = new ArrayList<>();
    }
    public DutyList() {
        this.nBlocks = 0;
        this.blocks = new ArrayList<>();
        this.title = "";
        this.extraStudents = new ArrayList<>();
    }

    //getters
    public Date getDate() {return date;}
    public int getnStudents() {return nStudents;}
    public ArrayList<Student> getStudents() {return students;}
    public int getnBlocks() {return nBlocks;}
    public ArrayList<Block> getBlocks() {return blocks;}
    public int getType() {return type;}
    public String getTitle() {return title;}
    public ArrayList<Student> getExtraStudents() {return extraStudents;}
    //setters
    public void setDate(Date date) {this.date = date;}
    public void setStudents(ArrayList<Student> students) {this.students = students;}
    public void setnBlocks(int nBlocks) {this.nBlocks = nBlocks;}
    public void setBlocks(ArrayList<Block> blocks) {this.blocks = blocks;}
    public void setType(int type) {this.type = type;}
    public void setTitle(String title) {this.title = title;}
    public void setExtraStudents(ArrayList<Student> extraStudents) {this.extraStudents = extraStudents;}
}
