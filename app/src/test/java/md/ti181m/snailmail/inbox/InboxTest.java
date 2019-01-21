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
    public void testOnCreate_unseenCountIsZero() {
        assertThat(inbox.getUnseenCount()).isEqualTo(0);
    }

    @Test
    public void testOnCreate_mailboxNumberSetFromPrefs() {
        when(prefs.getMailboxId()).thenReturn("103");

        assertThat(inbox.getMailBoxNumber()).isEqualTo("103");
    }

    @Test
    public void testOnStart_mailDownloaded() {
        when(prefs.getMailboxId()).thenReturn("53");

        inbox.downloadMailForDisplay();

        verify(api).getMail(/*tag*/any(), eq("53"), any(), any());
    }

    @Test
    public void testOnStart_loadingViewsShown() {
        inbox.downloadMailForDisplay();

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
        assertThat(inbox.getContent()).isEqualTo(Inbox.Content.LOADING);
    }

    @Test
    public void testOnMarkAsSeenConfirmed_request() {
        when(prefs.getMailboxId()).thenReturn("53");

        inbox.markAllAsSeen();

        verify(api).markAllAsSeen(/*tag*/any(), eq("53"), any(), any());
    }

    @Test
    public void testOnMarkAsSeenConfirmed_progress() {
        inbox.markAllAsSeen();

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
    }

    @Test
    public void testOnMailDeleteConfirmed_request() {
        Mail someMail = someMailWithId(34);
        inbox.deleteMail(someMail);

        verify(api).deleteMail(/*tag*/any(), eq(34L), any(), any());
    }

    private Mail someMailWithId(int id) {
        return new Mail(id, 0, null, null);
    }

    @Test
    public void testOnMailDeleteConfirmed_progress() {
        Mail someMail = someMail();
        inbox.deleteMail(someMail);

        assertThat(inbox.getProgress()).isEqualTo(Inbox.Progress.VISIBLE);
    }

    private Mail someMail() {
        return someMailWithId(0);
    }
}