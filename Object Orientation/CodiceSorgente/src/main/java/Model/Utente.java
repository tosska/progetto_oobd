package Model;

public class Utente {
    private String username;
    private String email;
    private String password;

    public Utente (String user, String mail, String pass) {
        setUsername(user);
        setEmail(mail);
        setPassword(pass);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


}
