package ru.mirea.khrechko.foodscan.data.auth;

public class AuthUserDto {
    private final String uid;
    private final String email;
    private final String displayName;

    public AuthUserDto(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email == null ? "" : email;
        if (displayName == null || displayName.trim().isEmpty()) {
            int at = this.email.indexOf('@');
            this.displayName = at > 0 ? this.email.substring(0, at) : this.email;
        } else {
            this.displayName = displayName;
        }
    }
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
}
