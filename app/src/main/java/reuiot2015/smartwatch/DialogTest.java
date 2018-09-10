package reuiot2015.smartwatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by cha on 2017/11/23.
 */

public class DialogTest extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("setMessage part")
                .setPositiveButton("positive button", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("negative button", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        Dialog returnedDialog = builder.create();
        returnedDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        returnedDialog.show();
        return returnedDialog;
    }
}