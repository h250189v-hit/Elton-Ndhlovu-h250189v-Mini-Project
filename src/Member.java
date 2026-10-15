import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    public static final int MAX_LOANS = 3;

    private final String id;
    private final String name;
    private final List<Loan> loans = new ArrayList<>();

    public Member(String id, String name) {
        this.id = LibraryItem.requireText(id, "Member id");
        this.name = LibraryItem.requireText(name, "Member name");
    }

    public String getId()   { return id; }
    public String getName() { return name; }

    /** Read-only view: outsiders cannot modify the list behind our back (encapsulation). */
    public List<Loan> getLoans() { return Collections.unmodifiableList(loans); }

    void ensureCanBorrow() throws MemberLimitExceededException {
        if (loans.size() >= MAX_LOANS) throw new MemberLimitExceededException(this, MAX_LOANS);
    }

    void addLoan(Loan loan)       { loans.add(loan); }
    void removeLoan(Loan loan)    { loans.remove(loan); }

    @Override
    public String toString() {
        return name + " (" + id + "), loans: " + loans.size() + "/" + MAX_LOANS;
    }
}
