package pfe.stage.oumast.signupandlogintabs;

import java.util.Date;

/**
 * Created by poste05 on 23/03/2018.
 */

public class SmsObject {
    private String id;
    private String phone;
    private String message;
    private String status;
    private String details;
    private Date date;
    private int nbr;
    public SmsObject() {
        this.nbr=0;
    }

    public SmsObject(String id, String phone, String message) {
        this.id = id;
        this.phone = phone;
        this.message = message;
        this.nbr=0;
    }

    public int getNbr() {
        return nbr;
    }

    public void setNbr(int nbr) {
        this.nbr = nbr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SmsObject{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", details='" + details + '\'' +
                ", date=" + date +
                '}';
    }
}
