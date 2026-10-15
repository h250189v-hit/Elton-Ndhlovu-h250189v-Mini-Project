import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Immutable record of one item lent to one member on one date. */
public class Loan {
    private final LibraryItem item;
    private final Member member;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    public Loan(LibraryItem item, Member member, LocalDate borrowDate) {
        if (item == null || member == null || borrowDate == null)
            throw new IllegalArgumentException("Loan needs an item, a member and a date");
        this.item = item;
        this.member = member;
        this.borrowDate = borrowDate;
        // polymorphic call: a Book is due after 14 days, a Dvd after 7
        this.dueDate = borrowDate.plusDays(item.loanPeriodDays());
    }

    public LibraryItem getItem()     { return item; }
    public Member getMember()        { return member; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }

    public boolean isOverdue(LocalDate today) { return today.isAfter(dueDate); }

    public long daysOverdue(LocalDate today) {
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, today));
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (borrowed %s, due %s)",
                item.getTitle(), member.getName(), borrowDate, dueDate);
    }
}
