package writeside.domain.model;

public class Customer {

    private String name;
    private String socialCreditNr;

    public Customer(String name, String socialCreditNr) {
        this.name = name;
        this.socialCreditNr = socialCreditNr;
    }

    public String getName() {
        return name;
    }

    public String getSocialCreditNr() {
        return socialCreditNr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSocialCreditNr(String socialCreditNr) {
        this.socialCreditNr = socialCreditNr;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", socialCreditNr='" + socialCreditNr + '\'' +
                '}';
    }
}
