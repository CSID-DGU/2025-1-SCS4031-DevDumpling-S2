public enum UserType {
    A("신용-투자 적극형"),
    B("신용-투자 보통형"),
    C("신용-투자 소극형"),
    D("신용-투자 회피형");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 