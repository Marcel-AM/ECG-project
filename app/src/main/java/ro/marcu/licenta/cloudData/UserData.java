package ro.marcu.licenta.cloudData;

public class UserData {

    private String name;
    private String email;
    private String gender;
    private String age;

    public UserData(){

    }

    public UserData(String name, String email, String gender, String age) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
