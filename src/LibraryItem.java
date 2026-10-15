/**
 * Abstract parent of everything the library lends.
 * Holds shared state and shared behaviour; subclasses supply the differences.
 */
public abstract class LibraryItem implements Borrowable {
    private final String id;
    private final String title;
    private boolean available = true;

    protected LibraryItem(String id, String title) {
        this.id = requireText(id, "Item id");
        this.title = requireText(title, "Title");
    }

    /** Each concrete type decides its own loan period (polymorphism). */
    public abstract int loanPeriodDays();

    /** "BOOK", "DVD", ... used for display and for the CSV file. */
    public abstract String typeName();

    /** Type-specific description, e.g. the author of a book. */
    protected abstract String details();

    /** The single extra CSV column (author or duration). */
    public abstract String csvExtra();

    @Override
    public void borrow(Member member) throws ItemNotAvailableException, MemberLimitExceededException {
        if (!available) throw new ItemNotAvailableException(this);
        member.ensureCanBorrow();          // all checks first, so a failure changes nothing
        available = false;
    }

    @Override
    public void giveBack() { available = true; }

    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public boolean isAvailable() { return available; }

    static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(field + " must not be empty");
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %d-day loan | %s",
                typeName(), id, title, details(), loanPeriodDays(),
                available ? "available" : "ON LOAN");
    }
}
