package ro.marcu.licenta.cloudData;

public class BpmData {

    private String documentID;
    private String bpm;
    private String time;

    public BpmData(){

    }

    public BpmData(String bpm, String time) {

        this.bpm = bpm;
        this.time = time;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}



