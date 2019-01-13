package md.ti181m.snailmail.inbox;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.utils.Prefs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InboxPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private SnailMailApi api;
    @Mock private Prefs prefs;
    @Mock private InboxView view;

    @Test
    public void testCreation() {
        InboxPresenter presenter = new InboxPresenter(api, prefs);

        presenter.onCreate();
    }

    @Test
    public void testOnCreate_unseenCountIsZero() {
        InboxPresenter presenter = makePresenter();

        presenter.onCreate();

        verify(view).updateUnseenCount(0);
    }

    @Test
    public void testOnCreate_mailboxNumberSetFromPrefs() {
        InboxPresenter presenter = makePresenter();
        when(prefs.getMailboxId()).thenReturn("103");

        presenter.onCreate();

        verify(view).setMailboxNumber("103");
    }

    @Test
    public void testOnStart_mailDownloaded() {
        InboxPresenter presenter = makePresenter();
        when(prefs.getMailboxId()).thenReturn("53");

        presenter.onStart();

        verify(api).getMail(/*tag*/any(), eq("53"), any(), any());
    }

    @Test
    public void testOnStart_loadingViewsShown() {
        InboxPresenter presenter = makePresenter();

        presenter.onStart();

        verify(view).setProgressVisible(eq(true));
        verify(view).displayLoadingText();
    }

    @Test
    public void testOnRefresh_mailDownloaded() {
        InboxPresenter presenter = makePresenter();
        when(prefs.getMailboxId()).thenReturn("53");

        presenter.onRefresh();

        verify(api).getMail(/*tag*/any(), eq("53"), any(), any());
    }

    @Test
    public void testOnMarkAsSeen_askConfirmation() {
        InboxPresenter presenter = makePresenter();

        presenter.onMarkAllSeenClicked();

        verify(view).askMarkAsSeenConfirmation();
    }

    @Test
    public void testOnMarkAsSeenConfirmed_request() {
        InboxPresenter presenter = makePresenter();
        when(prefs.getMailboxId()).thenReturn("53");

        presenter.onMarkAllSeenConfirmed();

        verify(api).markAllAsSeen(/*tag*/any(), eq("53"), any(), any());
    }

    @Test
    public void testOnMarkAsSeenConfirmed_progress() {
        InboxPresenter presenter = makePresenter();

        presenter.onMarkAllSeenConfirmed();

        verify(view).setProgressVisible(eq(true));
    }

    @Test
    public void testOnMailDelete_askConfirmation() {
        InboxPresenter presenter = makePresenter();

        Mail someMail = someMail();
        presenter.onMailDeleteClicked(someMail);

        verify(view).askMailDeletionConfirmation(same(someMail));
    }

    private Mail someMail() {
        return someMailWithId(0);
    }

    @Test
    public void testOnMailDeleteConfirmed_request() {
        InboxPresenter presenter = makePresenter();

        Mail someMail = someMailWithId(34);
        presenter.onMailDeleteConfirmed(someMail);

        verify(api).deleteMail(/*tag*/any(), eq(34L), any(), any());
    }

    private Mail someMailWithId(int id) {
        return new Mail(id, 0, null, null);
    }

    @Test
    public void testOnMailDeleteConfirmed_progress() {
        InboxPresenter presenter = makePresenter();

        Mail someMail = someMail();
        presenter.onMailDeleteConfirmed(someMail);

        verify(view).setProgressVisible(eq(true));
    }

    private InboxPresenter makePresenter() {
        InboxPresenter presenter = new InboxPresenter(api, prefs);
        presenter.setView(view);
        return presenter;
    }
}