package com.hyperlocal.app.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.registration.countrypicker.AlphabetItem;
import com.hyperlocal.app.ui.registration.countrypicker.CountryDetails;
import com.hyperlocal.app.ui.registration.countrypicker.DataHelper;
import com.hyperlocal.app.ui.registration.countrypicker.RecyclerViewAdapter;
import com.hyperlocal.app.ui.registration.countrypicker.RecyclerViewFastScroller;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @Author ${Umesh} on 03-04-2018.
 */

public class CountryPickerActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    @BindView(R.id.my_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.fast_scroller) RecyclerViewFastScroller fastScroller;
    @BindView(R.id.edittext) EditText searchEditText;
    private ArrayList<Object> temp = new ArrayList<>();
    private List<AlphabetItem> mAlphabetItems;
    private ArrayList<Object> objectArrayList;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_picker_activity);
        ButterKnife.bind(this);
        initialiseData();
        initialiseUI();
        searchCountry();


    }

    private void searchCountry() {
        RxTextView.afterTextChangeEvents(searchEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeEvent -> {
                    CharSequence s = changeEvent.view().getText();
                    if (TextUtils.isEmpty(s)) {
                        adapter.updateData(objectArrayList);
                        adapter.notifyDataSetChanged();
                        temp.clear();
                    } else {
                        filter(s.toString());
                    }
                });
    }


    void filter(String text) {
        temp = new ArrayList();
        for (Object d : objectArrayList) {
            if (d instanceof CountryDetails && ((CountryDetails) d).getCountryName().contains(text)) {
                temp.add(d);
            }
        }
        adapter.updateData(temp);
        adapter.notifyDataSetChanged();
    }


    protected void initialiseData() {
        DataHelper dataHelper = new DataHelper(CountryPickerActivity.this);
        Map<String, ArrayList<CountryDetails>> hashMap = dataHelper.getData();
        objectArrayList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<CountryDetails>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            objectArrayList.add(key);
            ArrayList<CountryDetails> arrayList1 = entry.getValue();
            objectArrayList.addAll(arrayList1);
        }

        mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < objectArrayList.size(); i++) {
            Object name = objectArrayList.get(i);
            if (name instanceof CountryDetails) {
                String name1 = ((CountryDetails) name).getCountryName();
                if (name1.trim().isEmpty())
                    continue;
                String word = name1.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(new AlphabetItem(i, word, false));
                }
            }

        }
    }

    protected void initialiseUI() {
        DividerItemDecoration divider = new DividerItemDecoration(
                mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));
        mRecyclerView.addItemDecoration(divider);
        adapter = new RecyclerViewAdapter(CountryPickerActivity.this, objectArrayList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(mRecyclerView);
        fastScroller.setUpAlphabet(mAlphabetItems);
    }


    @Override
    public void geCountryItem(int position) {
        if (temp.size() > 0) {
            Object countryDetails = temp.get(position);
            if (countryDetails instanceof CountryDetails) {
                Intent intent = new Intent();
                intent.putExtra("countryCode", ((CountryDetails) countryDetails).getCountryCode());
                intent.putExtra("countryName", ((CountryDetails) countryDetails).getCountryName());
                intent.putExtra("dialCode", ((CountryDetails) countryDetails).getDialCode());
                setResult(200, intent);
                finish();//finishing activity
            }
        } else {
            Object countryDetails = objectArrayList.get(position);
            if (countryDetails instanceof CountryDetails) {
                Intent intent = new Intent();
                intent.putExtra("countryCode", ((CountryDetails) countryDetails).getCountryCode());
                intent.putExtra("countryName", ((CountryDetails) countryDetails).getCountryName());
                intent.putExtra("dialCode", ((CountryDetails) countryDetails).getDialCode());
                setResult(200, intent);
                finish();//finishing activity
            }
        }
    }
}
