package md.ti181m.snailmail.inbox;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.utils.Prefs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InboxTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock SnailMailApi api;
    @Mock Prefs prefs;

    @InjectMocks Inbox inbox;

    @Test
    public void testInbox_initially_mailIsEmpty() {
        assertThat(inbox.getVisibleMail()).isEmpty();
    }

    @Test
    public void testInbox_initially_unseenCountIsZero() {
        assertThat(inbox.getUnseenCount()).isEqualTo(0);
    }

    @Test
    public void testInbox_initially_deletedPercentageIsZero() {
        assertThat(inbox.getDeletedPercentage()).isCloseTo(0, offset(0.1));
    }

    @Test
    public void testDownloadMailForDisplay_usesMailBoxNumberFromPrefs() {
        when(prefs.getMailboxId()).thenReturn("53");

        inbox.downloadMailForDisplay();

        assertThatGetMailCalledWithId("53");
    }

    private void assertThatGetMailCalledWithId(String id) {
        verify(api).getMail(/*tag*/any(), eq(id), any(), any());
    }

    @Test
    public void testInbox_initially_stateIsEmpty() {
        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.GONE);
        assertThat(inbox.getContent()).isEqualTo(Inbox.Content.EMPTY);
    }

    @Test
    public void testDownloadMailForDisplay_stateIsLoading() {
        inbox.downloadMailForDisplay();

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
        assertThat(inbox.getContent()).isEqualTo(Inbox.Content.LOADING);
    }

    @Test
    public void testMarkAllAsSeen_usesMailboxNumberFromPrefs() {
        when(prefs.getMailboxId()).thenReturn("123");

        inbox.markAllAsSeen();

        assertThatMarkAsSeenCalledWithId("123");
    }

    private void assertThatMarkAsSeenCalledWithId(String id) {
        verify(api).markAllAsSeen(/*tag*/any(), eq(id), any(), any());
    }

    @Test
    public void testMarkAllAsSeen_progressShown() {
        inbox.markAllAsSeen();

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
    }

    @Test
    public void testDeleteMail_usesMailId() {
        Mail someMail = someMailWithId(34);

        inbox.deleteMail(someMail);

        assertThatDeleteMailCalledWithId(34);
    }

    private void assertThatDeleteMailCalledWithId(long id) {
        verify(api).deleteMail(/*tag*/any(), eq(id), any(), any());
    }

    private Mail someMailWithId(int id) {
        return new Mail(id, 0, null, null);
    }

    @Test
    public void testDeleteMail_progressShown() {
        Mail someMail = someMail();
        inbox.deleteMail(someMail);

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
    }

    private Mail someMail() {
        return someMailWithId(0);
    }
}