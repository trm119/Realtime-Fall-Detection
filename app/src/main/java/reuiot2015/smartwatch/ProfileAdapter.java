package reuiot2015.smartwatch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public ProfileAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    public void add(SensorService.SubjectInformation object) {
        list.add(object);
        super.add(object);
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
