package com.sayone.omidyar.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sayone.omidyar.BaseActivity;
import com.sayone.omidyar.R;
import com.sayone.omidyar.model.CostElement;
import com.sayone.omidyar.model.CostElementYears;
import com.sayone.omidyar.model.Frequency;
import com.sayone.omidyar.model.LandKind;
import com.sayone.omidyar.model.Quantity;
import com.sayone.omidyar.model.RevenueProduct;
import com.sayone.omidyar.model.RevenueProductYears;
import com.sayone.omidyar.model.Survey;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class NaturalCapitalCostActivityC extends BaseActivity implements View.OnClickListener {

    Realm realm;
    SharedPreferences sharedPref;
    String serveyId;
    Context context;
    String currentSocialCapitalServey;

    Spinner spinnerYear,spinnerUnit,spinnerCurrency,spinnerTimePeriod;
    String year,unit,currency,numberTimes,timePeriod;
    Button buttonBack,buttonNext;
    TextView loadQuestions;
    Button saveBtn;

    RealmList<CostElement> costElements;
    int totalCostProductCount = 0;
    int currentCostProductIndex = 0;
    int totalYearsCount = 0;
    int currentYearIndex = 0;
    int currentCostProductIndexSave = 0;
    int currentYearIndexSave = 0;

    ArrayAdapter<String> timePeriod_adapter;
    ArrayList<String> timePeriodList;

    ArrayAdapter<String> year_adapter;
    ArrayList<String> yearList;

    ArrayAdapter<String> unit_adapter;
    ArrayList<String> unitList;

    TextView quantityQuestion;
    TextView productQuestion;
    EditText noOfTimesEdit;
    EditText quanityEdit;
    EditText priceEdit;

    double inflationRate = 0.05;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_natural_cost_survey_c);

        context = this;
        realm = Realm.getDefaultInstance();
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        serveyId = sharedPref.getString("surveyId","");
        currentSocialCapitalServey = sharedPref.getString("currentSocialCapitalServey","");

        spinnerYear = (Spinner)findViewById(R.id.spinner_year);
        spinnerUnit = (Spinner)findViewById(R.id.spinner_unit);
        spinnerCurrency = (Spinner)findViewById(R.id.spinner_currency);
        spinnerTimePeriod = (Spinner)findViewById(R.id.spinner_time_period);
        buttonBack = (Button)findViewById(R.id.button_back);
        buttonNext = (Button)findViewById(R.id.button_next);
        loadQuestions = (TextView) findViewById(R.id.load_questions);
        saveBtn = (Button) findViewById(R.id.save_btn);
        quantityQuestion = (TextView) findViewById(R.id.quantity_question);
        productQuestion = (TextView) findViewById(R.id.product_question);
        noOfTimesEdit = (EditText) findViewById(R.id.no_of_times_edit);
        quanityEdit = (EditText) findViewById(R.id.quanity_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);

        costElements = new RealmList<>();
        totalCostProductCount = 0;
        currentCostProductIndex = 0;
        totalYearsCount = 0;
        currentYearIndex = 0;
        currentCostProductIndexSave = 0;
        currentYearIndexSave = 0;
        inflationRate = 0.05;

        yearList = new ArrayList<>();
        timePeriodList = new ArrayList<>();
        unitList = new ArrayList<>();

        year_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        timePeriod_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timePeriodList);
        unit_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitList);


        Survey results = realm.where(Survey.class)
                .equalTo("surveyId",serveyId)
                .findFirst();

        RealmResults<Frequency> frequencyResult = realm.where(Frequency.class).findAll();
        for(Frequency frequency:frequencyResult){
            timePeriodList.add(frequency.getHarvestFrequency());
        }

        RealmResults<Quantity> quantityResult = realm.where(Quantity.class).findAll();
        for(Quantity quantity:quantityResult){
            unitList.add(quantity.getQuantityName());
        }








//        year_adapter = ArrayAdapter.createFromResource(this,
//                R.array.year_array, android.R.layout.simple_spinner_item);

        //year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        ArrayAdapter<CharSequence> unit_adapter = ArrayAdapter.createFromResource(this,
//                R.array.unit_array, android.R.layout.simple_spinner_item);
//        unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> currency_adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        final ArrayAdapter<CharSequence> timePeriod_adapter = ArrayAdapter.createFromResource(this,
//                R.array.time_period_array, android.R.layout.simple_spinner_item);
//        timePeriod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(year_adapter);
        spinnerUnit.setAdapter(unit_adapter);
        spinnerCurrency.setAdapter(currency_adapter);
        spinnerTimePeriod.setAdapter(timePeriod_adapter);


        for(LandKind landKind:results.getLandKinds()){
            if(landKind.getName().equals("Forestland") && currentSocialCapitalServey.equals("Forestland")){
                if(landKind.getForestLand().getCostElements().size() > 0){
                    //loadYears(landKind.getForestLand().getRevenueProducts().get(0).getRevenueProductYearses());
                    costElements = landKind.getForestLand().getCostElements();
                    totalCostProductCount = costElements.size();
                    currentCostProductIndex = 0;
                    currentYearIndex = 0;
                    if(currentCostProductIndex < totalCostProductCount){
                        loadRevenueProduct(landKind.getForestLand().getCostElements().get(currentCostProductIndex));
                    }
                }
            }else if(landKind.getName().equals("Cropland") && currentSocialCapitalServey.equals("Cropland")){
                if(landKind.getCropLand().getCostElements().size() > 0){
                    //loadYears(landKind.getForestLand().getRevenueProducts().get(0).getRevenueProductYearses());
                    costElements = landKind.getCropLand().getCostElements();
                    totalCostProductCount = costElements.size();
                    currentCostProductIndex = 0;
                    currentYearIndex = 0;
                    if(currentCostProductIndex < totalCostProductCount){
                        loadRevenueProduct(landKind.getCropLand().getCostElements().get(currentCostProductIndex));
                    }
                }
            }else if(landKind.getName().equals("Pastureland") && currentSocialCapitalServey.equals("Pastureland")){
                if(landKind.getPastureLand().getCostElements().size() > 0){
                    //loadYears(landKind.getForestLand().getRevenueProducts().get(0).getRevenueProductYearses());
                    costElements = landKind.getPastureLand().getCostElements();
                    totalCostProductCount = costElements.size();
                    currentCostProductIndex = 0;
                    currentYearIndex = 0;
                    if(currentCostProductIndex < totalCostProductCount){
                        loadRevenueProduct(landKind.getPastureLand().getCostElements().get(currentCostProductIndex));
                    }
                }
            }else if(landKind.getName().equals("Mining Land") && currentSocialCapitalServey.equals("Mining Land")){
                if(landKind.getMiningLand().getCostElements().size() > 0){
                    //loadYears(landKind.getForestLand().getRevenueProducts().get(0).getRevenueProductYearses());
                    costElements = landKind.getMiningLand().getCostElements();
                    totalCostProductCount = costElements.size();
                    currentCostProductIndex = 0;
                    currentYearIndex = 0;
                    if(currentCostProductIndex < totalCostProductCount){
                        loadRevenueProduct(landKind.getMiningLand().getCostElements().get(currentCostProductIndex));
                    }
                }
            }
        }





        ///year= spinnerYear.getSelectedItem().toString();
        unit= spinnerUnit.getSelectedItem().toString();
        currency= spinnerCurrency.getSelectedItem().toString();
//        timePeriod= spinnerTimePeriod.getSelectedItem().toString();

        buttonNext.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        saveBtn.setOnClickListener(this);



        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                year= parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                unit= parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                currency= parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                timePeriod= parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.button_next:
                nextLandKind();
//                intent=new Intent(getApplicationContext(),NaturalCapitalCostActivityA.class);
//                startActivity(intent);
                break;

            case R.id.button_back:
                intent=new Intent(getApplicationContext(),NaturalCapitalSurveyActivityD.class);
                startActivity(intent);
                break;

            case R.id.save_btn:
                saveYearlyDatas(costElements.get(currentCostProductIndexSave));

                if(currentCostProductIndex < totalCostProductCount){
                    loadRevenueProduct(costElements.get(currentCostProductIndex));
                    //currentCostProductIndex++;
                }else{
                    Toast.makeText(context,"Completed ",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public void nextLandKind(){
        RealmResults<LandKind> landKindRealmResults = realm.where(LandKind.class)
                .equalTo("surveyId",serveyId)
                .equalTo("status","active")
                .findAll();
        int j = 0;
        int i = 0;
        for (LandKind landKind : landKindRealmResults) {
            Log.e("TAG ", landKind.toString());
            //Log.e(TAG, String.valueOf(survey1.getParticipants().size()));
            if(landKind.getName().equals(currentSocialCapitalServey)){
                j = i + 1;
            }
            i++;
        }

        if(j < i){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentSocialCapitalServey", landKindRealmResults.get(j).getName());
            editor.apply();

            Intent intent = new Intent(getApplicationContext(),OmidyarMap.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(),CertificateActivity.class);
            startActivity(intent);
        }
    }

    public void loadRevenueProduct(CostElement costElementLoad){
        if(costElementLoad.getType().equals("Timber")){
            loadQuestions.setText("How often do you harvest "+costElementLoad.getName()+"?");
            quantityQuestion.setText("What was the quantity of "+costElementLoad.getName()+" harvested each time?");
            productQuestion.setText("What was the price of the "+costElementLoad.getName()+" per unit?");
        }else if(costElementLoad.getType().equals("Non Timber")){
            loadQuestions.setText("How often do you harvest "+costElementLoad.getName()+"?");
            quantityQuestion.setText("What was the quantity of "+costElementLoad.getName()+" harvested each time?");
            productQuestion.setText("What was the price of the "+costElementLoad.getName()+" per unit?");
        }else {
            loadQuestions.setText("How often do you harvest "+costElementLoad.getName()+"?");
            quantityQuestion.setText("What was the quantity of "+costElementLoad.getName()+" harvested each time?");
            productQuestion.setText("What was the price of the "+costElementLoad.getName()+" per unit?");
        }






        //yearList = revenueProductLoad.getRevenueProductYearses();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearList.clear();
        totalYearsCount = 0;
        for(CostElementYears costElementYears:costElementLoad.getCostElementYearses()){
            if(costElementYears.getYear() < currentYear && costElementYears.getYear() != 0){
                yearList.add(costElementYears.getYear()+"");
                year_adapter.notifyDataSetChanged();

                Log.e("REV PROD ID ",costElementYears.getCostElementId()+"");
                totalYearsCount++;
            }
        }

        //totalYearsCount = revenueProductLoad.getRevenueProductYearses().size();
        Log.e("Current year ",currentYearIndex+"");
        Log.e("Total year ",totalYearsCount+"");
        Log.e("Current cost ",currentCostProductIndex+"");
        Log.e("Total cost ",totalCostProductCount+"");

        currentCostProductIndexSave = currentCostProductIndex;
        currentYearIndexSave = currentYearIndex;

        if(currentYearIndex < totalYearsCount) {
            loadRevenueYears(costElementLoad.getCostElementYearses().get(currentYearIndex), costElementLoad);
            if (currentYearIndex == totalYearsCount) {
                currentCostProductIndex++;
                currentYearIndex = 0;
            }
        }
        Log.e("Current year ",currentYearIndex+"");
        Log.e("Total year ",totalYearsCount+"");
        Log.e("Current cost ",currentCostProductIndex+"");
        Log.e("Total cost ",totalCostProductCount+"");
    }

    public void loadRevenueYears(CostElementYears costElementYearsLoad, CostElement costElementLoad1){
        if(costElementYearsLoad.getCostFrequencyValue() != 0){
            noOfTimesEdit.setText(costElementYearsLoad.getCostFrequencyValue()+"");
        }
        if(costElementYearsLoad.getCostPerPeriodValue() != 0){
            quanityEdit.setText(costElementYearsLoad.getCostPerPeriodValue()+"");
        }
        if(costElementYearsLoad.getCostPerUnitValue() != 0){
            priceEdit.setText(costElementYearsLoad.getCostPerUnitValue()+"");
        }



        spinnerYear.setSelection(currentYearIndex);

        Frequency frequency = realm.where(Frequency.class)
                .equalTo("frequencyValue",(int) costElementYearsLoad.getCostFrequencyValue())
                .findFirst();

        if(timePeriodList.size() != 0 && frequency != null){
            Log.e("TEST FRE ", timePeriod_adapter.getPosition(frequency.getHarvestFrequency())+"");

            spinnerTimePeriod.setSelection(timePeriod_adapter.getPosition(frequency.getHarvestFrequency()));
            //spinnerTimePeriod.setSelection(timePeriodList.indexOf(frequency.getHarvestFrequency()));
        }


        Quantity quantity = realm.where(Quantity.class)
                .equalTo("quantityName",costElementYearsLoad.getCostPerUnitUnit())
                .findFirst();

        if(unitList.size() != 0 && quantity != null){
            Log.e("QUANTITY ", unit_adapter.getPosition(quantity.getQuantityName())+"");
            spinnerUnit.setSelection(unit_adapter.getPosition(quantity.getQuantityName()));
        }

        currentYearIndex++;
    }

    public void saveYearlyDatas(CostElement costElement2){
        Log.e("REVE PRO ",costElement2.toString()+" YEAR "+spinnerYear.getSelectedItem());
        for(CostElementYears costElementYears1:costElement2.getCostElementYearses()){
            if(String.valueOf(costElementYears1.getYear()).equals(spinnerYear.getSelectedItem()) ){
                Log.e("REVE YEAR ",costElementYears1.toString());
                String spinnerTimePeriodStr = spinnerTimePeriod.getSelectedItem().toString();
                Frequency frequency = realm.where(Frequency.class)
                        .equalTo("harvestFrequency",spinnerTimePeriodStr)
                        .findFirst();



                int yearCurent = Calendar.getInstance().get(Calendar.YEAR);
                int yearIndex = costElementYears1.getYear() - yearCurent;


                if(noOfTimesEdit.getText().toString().equals("")){
                    noOfTimesEdit.setText("0");
                }
                if(quanityEdit.getText().toString().equals("")){
                    quanityEdit.setText("0");
                }
                if(priceEdit.getText().toString().equals("")){
                    priceEdit.setText("0");
                }


                double total = frequency.getFrequencyValue()
                        * Integer.parseInt(noOfTimesEdit.getText().toString())
                        * Double.parseDouble(priceEdit.getText().toString())
                        * Double.parseDouble(quanityEdit.getText().toString());



                realm.beginTransaction();
                costElementYears1.setCostFrequencyValue(Integer.parseInt(noOfTimesEdit.getText().toString()));
                costElementYears1.setCostFrequencyUnit(frequency.getFrequencyValue());
                costElementYears1.setCostPerPeriodValue(Double.parseDouble(quanityEdit.getText().toString()));
                costElementYears1.setCostPerPeriodUnit(spinnerUnit.getSelectedItem().toString());
                costElementYears1.setCostPerUnitValue(Double.parseDouble(priceEdit.getText().toString()));
                costElementYears1.setCostPerUnitUnit(spinnerCurrency.getSelectedItem().toString());
                costElementYears1.setProjectedIndex(yearIndex);
                costElementYears1.setSubtotal(total);
                realm.commitTransaction();

                Log.e("RE CHECK ",costElementYears1.toString());
            }
        }
        calculateTrend(costElement2.getId());
    }

    public void calculateTrend(long costElementIdLong){
        CostElement costElement4 = realm.where(CostElement.class)
                .equalTo("id",costElementIdLong)
                .findFirst();
        double harvestFre = 0;
        double harvestTimes = 0;
        double harvestPrice = 0;

        double freqUnit = 0;
        String quaUnit = "";
        String priceCurrency = "";
        if(costElement4.getCostElementYearses().size() > 0){
            if(costElement4.getCostElementYearses().get(0).getCostFrequencyUnit() == 0){
                for(CostElementYears costElementYears:costElement4.getCostElementYearses()){
                    if(costElementYears.getProjectedIndex() < 0){
                        harvestFre = 0;
                        if(harvestTimes < costElementYears.getCostPerPeriodValue()){
                            harvestTimes = costElementYears.getCostPerPeriodValue();
                        }
                        if(harvestPrice < costElementYears.getCostPerUnitValue()){
                            harvestPrice = costElementYears.getCostPerUnitValue();
                        }
                        freqUnit = costElementYears.getCostFrequencyUnit();
                        quaUnit = costElementYears.getCostPerPeriodUnit();
                        priceCurrency = costElementYears.getCostPerUnitUnit();
                    }
                }
            }else{
                int eleCount = 0;
                for(CostElementYears costElementYears:costElement4.getCostElementYearses()){
                    if(costElementYears.getProjectedIndex() < 0){
                        harvestFre = costElementYears.getCostFrequencyValue();
                        harvestTimes = harvestTimes + costElementYears.getCostPerPeriodValue();
                        harvestPrice = harvestPrice + costElementYears.getCostPerUnitValue();

                        freqUnit = costElementYears.getCostFrequencyUnit();
                        quaUnit = costElementYears.getCostPerPeriodUnit();
                        priceCurrency = costElementYears.getCostPerUnitUnit();

                        eleCount++;
                    }
                }

                harvestTimes = harvestTimes/eleCount;
                harvestPrice = harvestPrice/eleCount;
            }
        }

        for(CostElementYears costElementYears:costElement4.getCostElementYearses()){
            if(costElementYears.getProjectedIndex() == 0){
                realm.beginTransaction();
                costElementYears.setCostFrequencyValue((int) harvestFre);
                costElementYears.setCostFrequencyUnit(freqUnit);
                costElementYears.setCostPerPeriodValue(harvestTimes);
                costElementYears.setCostPerPeriodUnit(quaUnit);
                costElementYears.setCostPerUnitValue(harvestPrice);
                costElementYears.setCostPerUnitUnit(priceCurrency);
                costElementYears.setProjectedIndex(0);
                costElementYears.setSubtotal(0);
                realm.commitTransaction();
            }
            if(costElementYears.getProjectedIndex() > 0){
                double marketPriceVal = harvestPrice * Math.pow((1 + inflationRate), costElementYears.getProjectedIndex());


                double totalVal = freqUnit
                        * harvestFre
                        * harvestTimes
                        * marketPriceVal;

                realm.beginTransaction();
                costElementYears.setCostFrequencyValue((int) harvestFre);
                costElementYears.setCostFrequencyUnit(freqUnit);
                costElementYears.setCostPerPeriodValue(harvestTimes);
                costElementYears.setCostPerPeriodUnit(quaUnit);
                costElementYears.setCostPerUnitValue(marketPriceVal);
                costElementYears.setCostPerUnitUnit(priceCurrency);
                costElementYears.setSubtotal(totalVal);
                realm.commitTransaction();
            }
        }


//        realm.beginTransaction();
//        revenueProductYears1.setHarvestFrequencyValue(Integer.parseInt(noOfTimesEdit.getText().toString()));
//        revenueProductYears1.setHarvestFrequencyUnit(frequency.getFrequencyValue());
//        revenueProductYears1.setQuantityValue(Double.parseDouble(quanityEdit.getText().toString()));
//        revenueProductYears1.setQuantityUnit(spinnerUnit.getSelectedItem().toString());
//        revenueProductYears1.setMarketPriceValue(Double.parseDouble(priceEdit.getText().toString()));
//        revenueProductYears1.setMarketPriceCurrency(spinnerCurrency.getSelectedItem().toString());
//        revenueProductYears1.setProjectedIndex(yearIndex);
//        revenueProductYears1.setSubtotal(total);
//        realm.commitTransaction();
    }
}
