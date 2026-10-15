/** Checked exception: the member already holds the maximum number of loans. */
public class MemberLimitExceededException extends Exception {
    private static final long serialVersionUID = 1L;

    public MemberLimitExceededException(Member member, int max) {
        super(member.getName() + " (" + member.getId() + ") already has the maximum of " + max + " loans");
    }
}
