package com.ntcomplete.eyeguess.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.ntcomplete.eyeguess.R;
import com.ntcomplete.eyeguess.adapters.CustomCardScrollAdapter;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private final String TAG = getClass().getSimpleName();

    private CardScrollView mCardScrollView;
    private CustomCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCardScrollView = new CardScrollView(this);
        mAdapter = new CustomCardScrollAdapter();
        createCards();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        mCardScrollView.setOnItemClickListener(this);
        setContentView(mCardScrollView);

    }

    private void createCards() {
        Card card = new Card(this);

        card.setText(getString(R.string.activity_main_choose));
        mAdapter.addCard(card);

        card = new Card(this);
        card.setText(getString(R.string.activity_main_movies_header));
        card.setImageLayout(Card.ImageLayout.FULL);
        card.addImage(R.drawable.movie_reel);
        mAdapter.addCard(card);


        card = new Card(this);
        card.setText(getString(R.string.activity_main_actors_header));
        card.setImageLayout(Card.ImageLayout.FULL);
        card.addImage(R.drawable.arrested_development);
        mAdapter.addCard(card);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_immersion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "Selected: " + i);
        if(i > 0) {
            startActivity(new Intent(MainActivity.this, CountActivity.class));
        }

    }
}
