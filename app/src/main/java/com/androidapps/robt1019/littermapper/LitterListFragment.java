package com.androidapps.robt1019.littermapper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rob on 24/06/15.
 */
public class LitterListFragment extends ListFragment {

    private static final int REQUEST_NEW_LITTER = 0;

    private LitterMapperDBHelper.LitterCursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Query list of litter items
        mCursor = LitterManager.get(getActivity()).queryLitterItems();
        // Create an adapter to point at this cursor
        LitterCursorAdapter adapter = new LitterCursorAdapter(getActivity(), mCursor);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        mCursor.close();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.litter_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_litter:
                Intent i = new Intent(getActivity(), LitterMapperActivity.class);
                startActivityForResult(i, REQUEST_NEW_LITTER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_NEW_LITTER == requestCode) {
            mCursor.requery();
            ((LitterCursorAdapter)getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // the id argument will be the Litter ID; This is given by CursorAdapter for free
        Intent i = new Intent(getActivity(), LitterMapperActivity.class);
        i.putExtra(LitterMapperActivity.EXTRA_LITTER_ID, id);
        startActivity(i);
    }

    private static class LitterCursorAdapter extends CursorAdapter {

        private LitterMapperDBHelper.LitterCursor mLitterCursor;

        public LitterCursorAdapter (Context context, LitterMapperDBHelper.LitterCursor cursor) {
            super(context, cursor, 0);
            mLitterCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the litter item for the current row
            Litter litter = mLitterCursor.getLitter();

            // Set up the date text view
            TextView dateTextView = (TextView)view;
            String cellText =
                    context.getString(R.string.cell_text) + " " + litter.getDate()
                    + " " + litter.getBrand() + " " + litter.getType();
            dateTextView.setText(cellText);
        }
    }
}
