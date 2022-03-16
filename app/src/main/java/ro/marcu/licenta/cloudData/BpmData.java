package ro.marcu.licenta.cloudData;

public class BpmData {

    private String email;
    private String bpm;
    private String dateTime;

    public BpmData(String email, String bpm, String dateTime) {
        this.email = email;
        this.bpm = bpm;
        this.dateTime = dateTime;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}



