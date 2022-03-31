package ro.marcu.licenta.cloudData;

public class AdviceData {

    private String ageId;
    private String excelent;
    private String normal;
    private String poor;


    public AdviceData(){

    }

    public AdviceData(String excelent, String normal, String poor) {
        this.excelent = excelent;
        this.normal = normal;
        this.poor = poor;
    }

    public String getAgeId() {
        return ageId;
    }

    public void setAgeId(String ageId) {
        this.ageId = ageId;
    }

    public String getExcelent() {
        return excelent;
    }

    public void setExcelent(String excelent) {
        this.excelent = excelent;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getPoor() {
        return poor;
    }

    public void setPoor(String poor) {
        this.poor = poor;
    }
}
