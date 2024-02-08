package android.application.duties_for_all;

public class Student {
    //variables
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String grade;
    private boolean local;
    private boolean cow;
    private boolean chicken;
    private boolean visitorCenter;
    private int numberDone;
    private Date latestDone;
    private boolean onCampus;

    //constructors
    public Student() {
        this.id = "";
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.grade = "predp";
        this.local = true;
        this.cow = true;
        this.chicken = true;
        this.visitorCenter = true;
        this.numberDone = 0;
        this.latestDone = new Date("Aug 1, 2022");
        this.onCampus = true;
    }
    public Student(String id) {this.id = id;}

    //getters
    public String getId() {return this.id;}
    public String getEmail() {return email;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getFullName() {return fullName;}
    public String getGrade() {return grade;}
    public boolean isLocal() {return local;}
    public boolean allowCow() {return cow;}
    public boolean allowChicken() {return chicken;}
    public boolean allowVisitorCenter() {return visitorCenter;}
    public int getNumberDone() {return numberDone;}
    public Date getLatestDone() {return latestDone;}
    public boolean isOnCampus() {return onCampus;}

    //setters
    public void setId(String id) {this.id = id;}
    public void setEmail(String email) {this.email = email;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fullName = firstName + " " + lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
    }
    public void setGrade(String grade) {this.grade = grade;}
    public void setLocal(int n) {
        if (n == 0) {this.local = false;}
        if (n == 1) {this.local = true;}
    }
    public void setCow(int n) {
        if (n == 0) {this.cow = false;}
        if (n == 1) {this.cow = true;}
    }
    public void setChicken(int n) {
        if (n == 0) {this.chicken = false;}
        if (n == 1) {this.chicken = true;}
    }
    public void setVisitorCenter(int n) {
        if (n == 0) {this.visitorCenter = false;}
        if (n == 1) {this.visitorCenter = true;}
    }
    public void setNumberDone(int numberDone) {this.numberDone = numberDone;}
    public void setLatestDone(Date date) {this.latestDone = date;}
    public void setOnCampus(int n) {
        if (n == 0) {this.onCampus = false;}
        if (n == 1) {this.onCampus = true;}
    }

    //methods
    public void add(int type, Date date) {
        //dining hall, chicken, other
        if (type == 0 || type == 4) {this.numberDone = this.numberDone + 1;}
        //cow or visitor center
        if (type == 1 || type == 3) {this.numberDone = this.numberDone + 3;}
        //chicken
        if (type == 2) {this.numberDone = this.numberDone + 2;}

        if (this.latestDone.before(date)) {this.latestDone = date;}
    }
}
