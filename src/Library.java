import java.time.LocalDate;
import java.util.*;

/** Core service: owns the collections and enforces the business rules. */
public class Library {
    private final Map<String, LibraryItem> items = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final Map<String, Loan> activeLoans = new HashMap<>();   // keyed by item id

    public void addItem(LibraryItem item) {
        if (items.containsKey(item.getId()))
            throw new IllegalArgumentException("An item with id " + item.getId() + " already exists");
        items.put(item.getId(), item);
    }

    public void registerMember(Member member) {
        if (members.containsKey(member.getId()))
            throw new IllegalArgumentException("A member with id " + member.getId() + " already exists");
        members.put(member.getId(), member);
    }

    public Loan borrow(String memberId, String itemId)
            throws ItemNotAvailableException, MemberLimitExceededException {
        return borrow(memberId, itemId, LocalDate.now());
    }

    public Loan borrow(String memberId, String itemId, LocalDate date)
            throws ItemNotAvailableException, MemberLimitExceededException {
        // 1. look up member and item
        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("No member with id " + memberId);
        LibraryItem item = items.get(itemId);
        if (item == null) throw new IllegalArgumentException("No item with id " + itemId);

        // 2. availability and limit are checked inside item.borrow(); it throws before changing anything
        item.borrow(member);

        // 3. create the loan and record it everywhere
        Loan loan = new Loan(item, member, date);
        member.addLoan(loan);
        activeLoans.put(itemId, loan);
        return loan;
    }

    public Loan returnItem(String itemId) {
        if (!items.containsKey(itemId)) throw new IllegalArgumentException("No item with id " + itemId);
        Loan loan = activeLoans.remove(itemId);
        if (loan == null) throw new IllegalStateException("Item " + itemId + " is not on loan");
        loan.getItem().giveBack();
        loan.getMember().removeLoan(loan);
        return loan;
    }

    public List<Loan> listOverdue(LocalDate today) {
        return activeLoans.values().stream()
                .filter(l -> l.isOverdue(today))
                .sorted(Comparator.comparing(Loan::getDueDate))
                .toList();
    }

    public List<LibraryItem> searchByTitle(String text) {
        String needle = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getTitle().toLowerCase().contains(needle))
                .sorted(Comparator.comparing(LibraryItem::getTitle))
                .toList();
    }

    public Collection<LibraryItem> getItems()   { return Collections.unmodifiableCollection(items.values()); }
    public Collection<Member> getMembers()      { return Collections.unmodifiableCollection(members.values()); }
    public Collection<Loan> getActiveLoans()    { return Collections.unmodifiableCollection(activeLoans.values()); }
}
