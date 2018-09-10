package reuiot2015.smartwatch;

public class UserProfile  {

    private String uuid;
    private String name;
    private String dateBirth;
    private String sex;
    private int height_feet;
    private int height_inches;
    private int weight;
    private String email;
    private String phone;
    private String contact_name;
    private String contact_email;
    private String contact_phone;

    public UserProfile(String uuid, String name, String dateBirth, String sex, int height_feet, int height_inches, int weight, String email, String phone, String contact_name, String contact_email, String contact_phone) {
        this.uuid = uuid;
        this.name = name;
        this.dateBirth = dateBirth;
        this.sex = sex;
        this.height_feet = height_feet;
        this.height_inches = height_inches;
        this.weight = weight;
        this.email = email;
        this.phone = phone;
        this.contact_name = contact_name;
        this.contact_email = contact_email;
        this.contact_phone = contact_phone;
    }


    public void setName(String name) { this.name = name; }

    public String getName() { return this.name; }

    public void setDateBirth(String dateBirth) { this.dateBirth = dateBirth; }

    public String getDateBirth() { return this.dateBirth; }

    public void setSex(String sex) { this.sex = sex; }

    public String getSex() { return this.sex; }

    public void setHeightFeet(int height_feet) { this.height_feet = height_feet; }

    public int getHeightFeet() { return this.height_feet;}

    public void setHeightInches(int height_inches) { this.height_inches = height_inches; }

    public int getHeightInches() { return this.height_inches;}

    public void setWeight(int weight) { this.weight = weight;}

    public int getWeight() {return this.weight; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() {return this.email;}

    public void setPhone(String phone) { this.phone = phone; }

    public String getPhone() { return this.phone;}

    public void setUUID(String uuid) { this.uuid = uuid; }

    public String getUuid() { return this.uuid;}

    public void setContactName(String contact_name) { this.contact_name = contact_name; }

    public String getContactName() { return this.contact_name; }

    public void setContactEmail(String contact_email) { this.contact_email = contact_email; }

    public String getContactEmail() { return this.contact_email; }

    public void setContactPhone(String contact_phone) { this.contact_phone = contact_phone; }

    public String getContactPhone() { return this.contact_phone; }


}
