package helium314.keyboard.latin.crypto;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class ContactSelectorOverlay {

    public interface OnContactSelectedListener {
        void onContactSelected(String contactName);
    }

    private final Context context;
    private final WindowManager windowManager;
    private View overlayView;
    private boolean isShowing = false;

    public ContactSelectorOverlay(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show(List<CryptoHelper.Contact> contacts, String currentContact, OnContactSelectedListener listener) {
        if (isShowing) {
            dismiss();
            return;
        }

        // Create overlay layout programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFFFFFFFF);
        layout.setPadding(20, 20, 20, 20);

        // Title
        TextView title = new TextView(context);
        title.setText("Select Contact");
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 20);
        layout.addView(title);

        // Contact list
        ListView listView = new ListView(context);
        String[] contactNames = new String[contacts.size()];
        int selectedIndex = -1;

        for (int i = 0; i < contacts.size(); i++) {
            CryptoHelper.Contact contact = contacts.get(i);
            String status = contact.hasSession ? "✓" : "✗";
            contactNames[i] = contact.name + " " + status;

            if (contact.name.equals(currentContact)) {
                selectedIndex = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
            android.R.layout.simple_list_item_single_choice, contactNames);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (selectedIndex >= 0) {
            listView.setItemChecked(selectedIndex, true);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = contacts.get(position).name;
            listener.onContactSelected(selectedContact);
            dismiss();
        });

        layout.addView(listView);

        overlayView = layout;

        // Window parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            800, // width
            1200, // height
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER;

        // Dismiss when touching outside
        overlayView.setOnTouchListener((v, event) -> {
            dismiss();
            return true;
        });

        try {
            windowManager.addView(overlayView, params);
            isShowing = true;
        } catch (Exception e) {
            android.util.Log.e("ContactSelectorOverlay", "Failed to show overlay", e);
        }
    }

    public void dismiss() {
        if (isShowing && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
                isShowing = false;
                overlayView = null;
            } catch (Exception e) {
                android.util.Log.e("ContactSelectorOverlay", "Failed to dismiss overlay", e);
            }
        }
    }

    public boolean isShowing() {
        return isShowing;
    }
}
