package com.sayone.omidyar.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sayone.omidyar.BaseActivity;
import com.sayone.omidyar.R;
import com.sayone.omidyar.model.CostElement;
import com.sayone.omidyar.model.CostElementYears;
import com.sayone.omidyar.model.LandKind;
import com.sayone.omidyar.model.Survey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

public class NaturalCapitalCostActivityB_copy extends BaseActivity implements View.OnClickListener {

    Realm realm;
    SharedPreferences sharedPref;
    String serveyId;
    String currentSocialCapitalServey;

    Spinner spinnerYear;
    String year;
    Button buttonBack,buttonNext;
    LinearLayout allEditText;
    Context context;
    Button addYearsButton;
    private ImageView imageViewMenuIcon;
    private ImageView drawerCloseBtn;
    private TextView textViewAbout;
    private TextView logout;
    private TextView startSurvey;
    private TextView landType;
    private DrawerLayout menuDrawerLayout;
    ArrayList<Spinner> editTexts;
    private TextView surveyIdDrawer;
    RealmList<CostElementYears> costElementYearsArrayList;
    TextView enterYearHeading;

    int i = 0;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_natural_cost_survey_b);

        context = this;
        language = Locale.getDefault().getDisplayLanguage();
        realm = Realm.getDefaultInstance();
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        serveyId = sharedPref.getString("surveyId","");
        currentSocialCapitalServey = sharedPref.getString("currentSocialCapitalServey","");
        Log.e("SER ID ",serveyId);

        editTexts = new ArrayList<>();
        costElementYearsArrayList = new RealmList<>();



        spinnerYear=(Spinner)findViewById(R.id.spinner_year);
        buttonBack=(Button)findViewById(R.id.button_back);
        buttonNext=(Button)findViewById(R.id.button_next);
        allEditText = (LinearLayout) findViewById(R.id.all_edit_text);
        addYearsButton = (Button) findViewById(R.id.add_years_button);
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.menu_drawer_layout);
        imageViewMenuIcon = (ImageView) findViewById(R.id.image_view_menu_icon);
        drawerCloseBtn = (ImageView) findViewById(R.id.drawer_close_btn);
        textViewAbout = (TextView) findViewById(R.id.text_view_about);
        logout = (TextView) findViewById(R.id.logout);
        startSurvey=(TextView)findViewById(R.id.text_start_survey);
        surveyIdDrawer=(TextView)findViewById(R.id.text_view_id);
        enterYearHeading = (TextView) findViewById(R.id.enter_year_heading);
        landType=(TextView)findViewById(R.id.land_type);

        buttonNext.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        addYearsButton.setOnClickListener(this);
        imageViewMenuIcon.setOnClickListener(this);
        drawerCloseBtn.setOnClickListener(this);
        textViewAbout.setOnClickListener(this);
        logout.setOnClickListener(this);
        startSurvey.setOnClickListener(this);
        surveyIdDrawer.setText(serveyId);
        landType.setText(currentSocialCapitalServey);

        Survey results = realm.where(Survey.class)
                .equalTo("surveyId",serveyId)
                .findFirst();
        for(LandKind landKind:results.getLandKinds()){
            if(landKind.getName().equals("Forestland") && currentSocialCapitalServey.equals("Forestland")){
                if(landKind.getForestLand().getCostElements().size() > 0){
                    loadYears(landKind.getForestLand().getCostElements().get(0).getCostElementYearses());
                }
            }else if(landKind.getName().equals("Cropland") && currentSocialCapitalServey.equals("Cropland")){
                if(landKind.getCropLand().getCostElements().size() > 0){
                    loadYears(landKind.getCropLand().getCostElements().get(0).getCostElementYearses());
                }
            }else if(landKind.getName().equals("Pastureland") && currentSocialCapitalServey.equals("Pastureland")){
                if(landKind.getPastureLand().getCostElements().size() > 0){
                    loadYears(landKind.getPastureLand().getCostElements().get(0).getCostElementYearses());
                }
            }else if(landKind.getName().equals("Mining Land") && currentSocialCapitalServey.equals("Mining Land")){
                if(landKind.getMiningLand().getCostElements().size() > 0){
                    loadYears(landKind.getMiningLand().getCostElements().get(0).getCostElementYearses());
                }
            }
        }
        if(i == 0){
            i = 1;
        }








        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(year_adapter);

        //year= spinnerYear.getSelectedItem().toString();

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                year= parent.getItemAtPosition(pos).toString();
                //Log.e("Year ",year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void loadYears(RealmList<CostElementYears> costElementYearsRealmList){
        i = 1;
        LinearLayout.LayoutParams mRparams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(CostElementYears costElementYears : costElementYearsRealmList){
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            Log.e("YEAR PEEEEEEEEE",costElementYears.getYear()+" "+currentYear);
            if(costElementYears.getYear() < currentYear && costElementYears.getYear() != 0){
                ArrayList yearArray = new ArrayList();
                int year = currentYear - 1;
                while(year >= 1990){
                    yearArray.add(String.valueOf(year));
                    year--;
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArray);
                //ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,yearArray);

                Spinner spinner=new Spinner(this);
                spinner.setAdapter(arrayAdapter);
                spinner.setSelection(arrayAdapter.getPosition(costElementYears.getYear()+""));
                spinner.setLayoutParams(mRparams);

                spinner.setId(i);
                allEditText.addView(spinner);
                editTexts.add(spinner);

                arrayAdapter.notifyDataSetChanged();
                //setContentView();
                //layout.addView(spinner);
                i++;



//                EditText myEditText = new EditText(context);
//                myEditText.setLayoutParams(mRparams);
//                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                myEditText.setId(i);
//                myEditText.setHint(getResources().getString(R.string.enter_year_hint)+" " + i);
//                myEditText.setText(costElementYears.getYear()+"");
//                allEditText.addView(myEditText);
//                editTexts.add(myEditText);
//                i++;
            }
        }
    }

    public void generateYearFields(int j){
        LinearLayout.LayoutParams mRparams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        int k;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(k=1; k<=j; k++){
            ArrayList yearArray = new ArrayList();
            int year = currentYear - 1;
            while(year >= 1990){
                yearArray.add(year--);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArray);

            Spinner spinner=new Spinner(this);
            spinner.setLayoutParams(mRparams);
            spinner.setAdapter(arrayAdapter);
            spinner.setId(k);
            allEditText.addView(spinner);
            editTexts.add(spinner);
            //layout.addView(spinner);
            i++;



//            EditText myEditText = new EditText(context);
//            myEditText.setLayoutParams(mRparams);
//            myEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//            myEditText.setId(k);
//            myEditText.setHint(getResources().getString(R.string.enter_year_hint)+" "+ k);
//            allEditText.addView(myEditText);
//            editTexts.add(myEditText);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){

            case R.id.button_next:
                if(editTexts.size() > 0){
                    saveYears();

                    intent=new Intent(getApplicationContext(),NaturalCapitalCostActivityC.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context,"Select at least one year",Toast.LENGTH_SHORT).show();
                }
//
//
//                saveYears();



                break;

            case R.id.button_back:
                finish();
                break;

            case R.id.add_years_button:
                //Log.e("Year ",year);
                if(!year.equals("year")){
                    enterYearHeading.setVisibility(View.VISIBLE);
                    generateYearFields(Integer.parseInt(year));
                }else{
                        Toast.makeText(context,getResources().getString(R.string.select_no_of_years), Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.image_view_menu_icon:
                toggleMenuDrawer();
                break;
            case  R.id.drawer_close_btn:
                toggleMenuDrawer();
                break;
            case  R.id.text_view_about:
                Intent i = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(i);
                break;
            case R.id.text_start_survey:
                Intent intentt = new Intent(getApplicationContext(),MainActivity.class);
                intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentt);
                break;
            case R.id.logout:
                Intent intents = new Intent(getApplicationContext(),RegistrationActivity.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intents);
                break;
        }

    }

    public void saveYears(){
        Survey results = realm.where(Survey.class)
                .equalTo("surveyId",serveyId)
                .findFirst();
        for(LandKind landKind:results.getLandKinds()){
            if(landKind.getName().equals("Forestland") && currentSocialCapitalServey.equals("Forestland")){
                for (CostElement costElements1: landKind.getForestLand().getCostElements()){
                    //Log.e("LAND ", revenueProduct1.getName());
                    //Log.e("LAND AA ", revenueProduct1.getRevenueProductYearses().size()+"");


                    for(Spinner editText : editTexts){
                        //editText.setText("233");
                        //Log.e("SSS ",editText.getText().toString());
                        costElementYearsArrayList.add(saveProductYears(Integer.parseInt(editText.getSelectedItem().toString()), costElements1.getId(), "Forestland"));
                    }

                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    costElementYearsArrayList.add(saveTrend(costElements1.getId(), "Forestland"));
                    for(int k=0;k<=15;k++){
                        costElementYearsArrayList.add(saveProjectionYears(year, costElements1.getId(), "Forestland", k));
                        year++;
                    }



                    realm.beginTransaction();
                    costElements1.setCostElementYearses(costElementYearsArrayList);
                    realm.commitTransaction();
                    costElementYearsArrayList.clear();
                }
            }else if(landKind.getName().equals("Cropland") && currentSocialCapitalServey.equals("Cropland")){
                for (CostElement costElements1: landKind.getCropLand().getCostElements()){
                    //Log.e("LAND ", revenueProduct1.getName());
                    //Log.e("LAND AA ", revenueProduct1.getRevenueProductYearses().size()+"");


                    for(Spinner editText : editTexts){
                        //editText.setText("233");
                        //Log.e("SSS ",editText.getText().toString());
                        costElementYearsArrayList.add(saveProductYears(Integer.parseInt(editText.getSelectedItem().toString()), costElements1.getId(), "Cropland"));
                    }

                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    costElementYearsArrayList.add(saveTrend(costElements1.getId(), "Cropland"));
                    for(int k=0;k<=5;k++){
                        costElementYearsArrayList.add(saveProjectionYears(year, costElements1.getId(), "Cropland", k));
                        year++;
                    }


                    realm.beginTransaction();
                    costElements1.setCostElementYearses(costElementYearsArrayList);
                    realm.commitTransaction();
                    costElementYearsArrayList.clear();
                }
            }else if(landKind.getName().equals("Pastureland") && currentSocialCapitalServey.equals("Pastureland")){
                for (CostElement costElements1: landKind.getPastureLand().getCostElements()){
                    //Log.e("LAND ", revenueProduct1.getName());
                    //Log.e("LAND AA ", revenueProduct1.getRevenueProductYearses().size()+"");


                    for(Spinner editText : editTexts){
                        //editText.setText("233");
                        //Log.e("SSS ",editText.getText().toString());
                        costElementYearsArrayList.add(saveProductYears(Integer.parseInt(editText.getSelectedItem().toString()), costElements1.getId(), "Pastureland"));
                    }

                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    costElementYearsArrayList.add(saveTrend(costElements1.getId(), "Pastureland"));
                    for(int k=0;k<=8;k++){
                        costElementYearsArrayList.add(saveProjectionYears(year, costElements1.getId(), "Pastureland", k));
                        year++;
                    }


                    realm.beginTransaction();
                    costElements1.setCostElementYearses(costElementYearsArrayList);
                    realm.commitTransaction();
                    costElementYearsArrayList.clear();
                }
            }else if(landKind.getName().equals("Mining Land") && currentSocialCapitalServey.equals("Mining Land")){
                for (CostElement costElements1: landKind.getMiningLand().getCostElements()){
                    //Log.e("LAND ", revenueProduct1.getName());
                    //Log.e("LAND AA ", revenueProduct1.getRevenueProductYearses().size()+"");


                    for(Spinner editText : editTexts){
                        //editText.setText("233");
                        //Log.e("SSS ",editText.getText().toString());
                        costElementYearsArrayList.add(saveProductYears(Integer.parseInt(editText.getSelectedItem().toString()), costElements1.getId(), "Mining Land"));
                    }

                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    costElementYearsArrayList.add(saveTrend(costElements1.getId(), "Mining Land"));
                    for(int k=0;k<=5;k++){
                        costElementYearsArrayList.add(saveProjectionYears(year, costElements1.getId(), "Mining Land", k));
                        year++;
                    }


                    realm.beginTransaction();
                    costElements1.setCostElementYearses(costElementYearsArrayList);
                    realm.commitTransaction();
                    costElementYearsArrayList.clear();
                }
            }
        }

//        Survey results1 = realm.where(Survey.class).findFirst();
//        for(LandKind landKind:results1.getLandKinds()){
//            if(landKind.getName().equals("Forestland")){
//                for (RevenueProduct revenueProduct1: landKind.getForestLand().getRevenueProducts()){
//                    Log.e("YEAR 1 ",revenueProduct1.getName()+"");
//
//                    for(RevenueProductYears revenueProductYears:revenueProduct1.getRevenueProductYearses()){
//
//                        Log.e("YEAR ",revenueProductYears.getYear()+" "+revenueProductYears.getId());
//                    }
//                }
//            }
//        }
    }


    public CostElementYears saveProductYears(int yearVal, long costElementId, String landKindName){
        Log.e("CCC ",serveyId+" "+yearVal+" "+costElementId+" "+landKindName);
        CostElementYears costElementYearsCheck = realm.where(CostElementYears.class)
                .equalTo("surveyId",serveyId)
                .equalTo("landKind",landKindName)
                .equalTo("costElementId",costElementId)
                .equalTo("year",yearVal)
                .findFirst();
        for(CostElementYears costElementYears1: realm.where(CostElementYears.class).findAll()){
            Log.e("BBB ", costElementYears1.toString());
        }
        Log.e("AA ", String.valueOf(costElementYearsCheck == null));
        if(costElementYearsCheck == null){
            realm.beginTransaction();
            CostElementYears costElementYears = realm.createObject(CostElementYears.class);
            costElementYears.setId(getNextKeyCostElementYears());
            costElementYears.setYear(yearVal);
            costElementYears.setCostElementId(costElementId);
            costElementYears.setLandKind(landKindName);
            costElementYears.setSurveyId(serveyId);
            realm.commitTransaction();
            return costElementYears;
        }else {
            return costElementYearsCheck;
        }
    }

    public CostElementYears saveTrend(long costElementId, String landKindName){
        Log.e("CCC ",serveyId+" "+0+" "+costElementId+" "+landKindName);
        CostElementYears costElementYearsCheck = realm.where(CostElementYears.class)
                .equalTo("surveyId",serveyId)
                .equalTo("landKind",landKindName)
                .equalTo("costElementId",costElementId)
                .equalTo("year",0)
                .findFirst();
        for(CostElementYears costElementYears1: realm.where(CostElementYears.class).findAll()){
            Log.e("BBB ", costElementYears1.toString());
        }
        Log.e("AA ", String.valueOf(costElementYearsCheck == null));
        if(costElementYearsCheck == null){
            realm.beginTransaction();
            CostElementYears costElementYears = realm.createObject(CostElementYears.class);
            costElementYears.setId(getNextKeyCostElementYears());
            costElementYears.setYear(0);
            costElementYears.setCostElementId(costElementId);
            costElementYears.setLandKind(landKindName);
            costElementYears.setSurveyId(serveyId);
            realm.commitTransaction();
            return costElementYears;
        }else {
            return costElementYearsCheck;
        }
    }

    public CostElementYears saveProjectionYears(int yearVal, long costElementId, String landKindName, int projectionIndex){
        Log.e("CCC ",serveyId+" "+yearVal+" "+costElementId+" "+landKindName);
        CostElementYears costElementYearsCheck = realm.where(CostElementYears.class)
                .equalTo("surveyId",serveyId)
                .equalTo("landKind",landKindName)
                .equalTo("costElementId",costElementId)
                .equalTo("year",yearVal)
                .findFirst();
        for(CostElementYears revenueProductYears1: realm.where(CostElementYears.class).findAll()){
            Log.e("BBB ", revenueProductYears1.toString());
        }
        Log.e("AA ", String.valueOf(costElementYearsCheck == null));
        if(costElementYearsCheck == null){
            realm.beginTransaction();
            CostElementYears costElementYears = realm.createObject(CostElementYears.class);
            costElementYears.setId(getNextKeyCostElementYears());
            costElementYears.setYear(yearVal);
            costElementYears.setCostElementId(costElementId);
            costElementYears.setLandKind(landKindName);
            costElementYears.setSurveyId(serveyId);
            costElementYears.setProjectedIndex(projectionIndex);
            realm.commitTransaction();
            return costElementYears;
        }else {
            return costElementYearsCheck;
        }
    }

    public int getNextKeyCostElementYears() {
        return realm.where(CostElementYears.class).max("id").intValue() + 1;
    }
    public void toggleMenuDrawer(){
        if(menuDrawerLayout.isDrawerOpen(GravityCompat.START)){
            menuDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            menuDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}