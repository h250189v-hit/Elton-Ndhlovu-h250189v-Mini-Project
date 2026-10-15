/** Capability contract: anything that can be lent to a member (Lab 6: interface). */
public interface Borrowable {
    void borrow(Member member) throws ItemNotAvailableException, MemberLimitExceededException;
    void giveBack();
}
