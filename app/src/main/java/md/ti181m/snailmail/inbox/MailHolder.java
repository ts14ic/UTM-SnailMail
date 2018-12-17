package md.ti181m.snailmail.inbox;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import md.ti181m.snailmail.R;

class MailHolder extends BaseHolder {

    private static final int LAYOUT = R.layout.item_mail;

    @BindView(R.id.icon_image_view) ImageView iconImageView;
    @BindView(R.id.when_received_text_view) TextView whenReceivedTextView;
    @BindView(R.id.when_dismissed_text_view) TextView whenDismissedTextView;

    MailHolder(ViewGroup parent) {
        super(parent, LAYOUT);
    }

    @Override
    void bind(BaseItem baseItem) {
        MailItem item = (MailItem) baseItem;

        iconImageView.setColorFilter(getItemColor(item));

        whenReceivedTextView.setText(formatDate(item.getWhenReceived()));
        whenReceivedTextView.setTextColor(getItemColor(item));

        if (item.hasBeenSeen()) {
            String formattedDate = formatDate(item.getWhenSeen());
            String dismissedText = itemView.getContext()
                    .getString(R.string.inbox__dismissed_at_s, formattedDate);
            whenDismissedTextView.setText(dismissedText);

            whenDismissedTextView.setVisibility(View.VISIBLE);
        } else {
            whenDismissedTextView.setVisibility(View.GONE);
        }
    }

    private String formatDate(long date) {
        return DateTimeFormat.forPattern("HH:mm YYYY-MM-dd")
                .print(date);
    }

    @ColorInt
    private int getItemColor(MailItem item) {
        int colorRes;
        if (item.hasBeenSeen()) {
            colorRes = R.color.black;
        } else {
            colorRes = R.color.colorAccent;
        }
        return ContextCompat.getColor(itemView.getContext(), colorRes);
    }
}
