public class Dvd extends LibraryItem {
    private final int durationMinutes;

    public Dvd(String id, String title, int durationMinutes) {
        super(id, title);
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be positive");
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() { return durationMinutes; }

    @Override public int loanPeriodDays() { return 7; }
    @Override public String typeName()    { return "DVD"; }
    @Override protected String details()  { return durationMinutes + " min"; }
    @Override public String csvExtra()    { return String.valueOf(durationMinutes); }
}
