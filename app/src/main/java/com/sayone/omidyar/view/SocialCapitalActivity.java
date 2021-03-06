package com.sayone.omidyar.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sayone.omidyar.BaseActivity;
import com.sayone.omidyar.R;
import com.sayone.omidyar.model.LandKind;
import com.sayone.omidyar.model.MultipleAnswer;
import com.sayone.omidyar.model.SocialCapitalAnswer;
import com.sayone.omidyar.model.SocialCapitalAnswerOptions;
import com.sayone.omidyar.model.SocialCapitalQuestions;
import com.sayone.omidyar.model.SpredTable;
import com.sayone.omidyar.model.Survey;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by sayone on 21/9/16.
 */

public class SocialCapitalActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    public static final String TAG = SocialCapitalActivity.class.getName();
    private Realm realm;
    private SharedPreferences preferences;
    Context context;

    String currentSocialCapitalServey;
    String surveyId;

    private Button backButton;
    private Button nextButton;

    private LinearLayout checkboxgroup;
    private RadioGroup radiobuttongroup;

    private TextView question;

    private CheckBox optionA;
    private CheckBox optionB;
    private CheckBox optionC;
    private CheckBox optionD;
    private CheckBox optionE;

    private RadioButton optionARadio;
    private RadioButton optionBRadio;
    private RadioButton optionCRadio;
    private RadioButton optionDRadio;
    private RadioButton optionERadio;

    private DrawerLayout menuDrawerLayout;
    private ImageView imageViewMenuIcon;
    private ImageView drawerCloseBtn;
    private TextView surveyIdDrawer;
    private TextView pageNumber;
    private TextView landType;
    private int currentQuestionId = 0;
    //Side Nav
    private TextView textViewAbout;
    private TextView startSurvey;
    private TextView harvestingForestProducts;
    private TextView agriculture;
    private TextView grazing;
    private TextView mining;
    private TextView sharedCostsOutlays;
    private TextView certificate;
    private TextView logout;

    RealmResults<SocialCapitalQuestions> socialCapitalQuestionses;
    SocialCapitalQuestions socialCapitalQuestionsSelectedItem;
    RealmList<SocialCapitalAnswerOptions> socialCapitalAnswerOptionsesList;

    LandKind landKind;
    String language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_capital);

        context = this;
        realm = Realm.getDefaultInstance();
        preferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        surveyId = preferences.getString("surveyId", "");
        currentSocialCapitalServey = preferences.getString("currentSocialCapitalSurvey", "");
        language = Locale.getDefault().getDisplayLanguage();
        socialCapitalAnswerOptionsesList = new RealmList<>();
        checkboxgroup = (LinearLayout) findViewById(R.id.checkboxgroup);
        radiobuttongroup = (RadioGroup) findViewById(R.id.radiobuttongroup);
        radiobuttongroup.setOnCheckedChangeListener(this);
        question = (TextView) findViewById(R.id.question);
        optionA = (CheckBox) findViewById(R.id.option_a);
        optionB = (CheckBox) findViewById(R.id.option_b);
        optionC = (CheckBox) findViewById(R.id.option_c);
        optionD = (CheckBox) findViewById(R.id.option_d);
        optionE = (CheckBox) findViewById(R.id.option_e);
        optionARadio = (RadioButton) findViewById(R.id.option_a_radio);
        optionBRadio = (RadioButton) findViewById(R.id.option_b_radio);
        optionCRadio = (RadioButton) findViewById(R.id.option_c_radio);
        optionDRadio = (RadioButton) findViewById(R.id.option_d_radio);
        optionERadio = (RadioButton) findViewById(R.id.option_e_radio);
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.menu_drawer_layout);
        imageViewMenuIcon = (ImageView) findViewById(R.id.image_view_menu_icon);
        drawerCloseBtn = (ImageView) findViewById(R.id.drawer_close_btn);
        surveyIdDrawer = (TextView) findViewById(R.id.text_view_id);
        pageNumber = (TextView) findViewById(R.id.page_number);
        landType = (TextView) findViewById(R.id.land_type);
        landKind = realm.where(LandKind.class)
                .equalTo("surveyId", surveyId)
                .equalTo("status", "active")
                .equalTo("name", currentSocialCapitalServey)
                .findFirst();
        currentQuestionId = 0;
        loadQuestion(currentQuestionId);
        backButton = (Button) findViewById(R.id.back_button);
        nextButton = (Button) findViewById(R.id.next_button);
        surveyIdDrawer.setText(surveyId);
        imageViewMenuIcon.setOnClickListener(this);
        surveyIdDrawer.setText(surveyId);
        drawerCloseBtn.setOnClickListener(this);
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        //Side Nav
        textViewAbout = (TextView) findViewById(R.id.text_view_about);
        startSurvey = (TextView) findViewById(R.id.text_start_survey);
        harvestingForestProducts = (TextView) findViewById(R.id.text_harvesting_forest_products);
        agriculture = (TextView) findViewById(R.id.text_agriculture);
        grazing = (TextView) findViewById(R.id.text_grazing);
        mining = (TextView) findViewById(R.id.text_mining);
        sharedCostsOutlays = (TextView) findViewById(R.id.text_shared_costs_outlays);
        certificate = (TextView) findViewById(R.id.text_certificate);
        logout = (TextView) findViewById(R.id.logout);
        textViewAbout.setOnClickListener(this);
        startSurvey.setOnClickListener(this);
        harvestingForestProducts.setOnClickListener(this);
        agriculture.setOnClickListener(this);
        grazing.setOnClickListener(this);
        mining.setOnClickListener(this);
        sharedCostsOutlays.setOnClickListener(this);
        certificate.setOnClickListener(this);
        logout.setOnClickListener(this);
        setNav();
    }

    @Override
    protected void onResume() {
        setNav();
        super.onResume();
    }

    public void loadQuestion(int currentQId) {
        Survey survey = realm.where(Survey.class)
                .equalTo("surveyId", surveyId)
                .findFirst();
        LandKind landKindLoad = realm.where(LandKind.class)
                .equalTo("surveyId", surveyId)
                .equalTo("status", "active")
                .equalTo("name", currentSocialCapitalServey)
                .findFirst();
        language = survey.getLanguage();
        for (SocialCapitalAnswer socialCapitalAnswer : landKindLoad.getSocialCapitals().getSocialCapitalAnswers()) {
            Log.e("NN ", socialCapitalAnswer.toString());
        }
        SocialCapitalQuestions socialCapitalQuestionsLandKindLoad = landKindLoad.getSocialCapitals().getSocialCapitalAnswers().get(currentQId).getSocialCapitalQuestion();
        if (language.equals("हिन्दी") || language.equalsIgnoreCase("Hindi")) {
            setQuestionView(socialCapitalQuestionsLandKindLoad.getOptionType(),
                    socialCapitalQuestionsLandKindLoad.getQuestionHindi(),
                    socialCapitalQuestionsLandKindLoad.getSocialCapitalAnswerOptionses(),
                    landKindLoad.getSocialCapitals().getSocialCapitalAnswers().get(currentQId).getMultipleAnswers());
        } else {
            setQuestionView(socialCapitalQuestionsLandKindLoad.getOptionType(),
                    socialCapitalQuestionsLandKindLoad.getQuestion(),
                    socialCapitalQuestionsLandKindLoad.getSocialCapitalAnswerOptionses(),
                    landKindLoad.getSocialCapitals().getSocialCapitalAnswers().get(currentQId).getMultipleAnswers());
        }
        pageNumber.setText(currentQId + 1 + "/14");

        if (currentSocialCapitalServey.equals(getString(R.string.string_forestland)))
            landType.setText(getResources().getText(R.string.string_forestland));
        if (currentSocialCapitalServey.equals(getString(R.string.string_pastureland)))
            landType.setText(getResources().getText(R.string.string_pastureland));
        if (currentSocialCapitalServey.equals(getString(R.string.string_miningland)))
            landType.setText(getResources().getText(R.string.string_miningland));
        if (currentSocialCapitalServey.equals(getString(R.string.string_cropland)))
            landType.setText(getResources().getText(R.string.string_cropland));
    }

    public void setQuestionView(String type,
                                String questionval,
                                RealmList<SocialCapitalAnswerOptions> options,
                                RealmList<MultipleAnswer> multipleAnswers) {
        question.setText(questionval);
        optionA.setVisibility(View.GONE);
        optionB.setVisibility(View.GONE);
        optionC.setVisibility(View.GONE);
        optionD.setVisibility(View.GONE);
        optionE.setVisibility(View.GONE);
        optionARadio.setVisibility(View.GONE);
        optionBRadio.setVisibility(View.GONE);
        optionCRadio.setVisibility(View.GONE);
        optionDRadio.setVisibility(View.GONE);
        optionERadio.setVisibility(View.GONE);
        int i = 0;
        for (SocialCapitalAnswerOptions socialCapitalAnswerOptions : options) {
            switch (i) {
                case 0:
                    if (type.equals("multiple")) {
                        changeDisplay(type, options, i, optionA, null, multipleAnswers, "optionA");
                    } else {
                        changeDisplay(type, options, i, null, optionARadio, multipleAnswers, "");
                    }
                    break;
                case 1:
                    if (type.equals("multiple")) {
                        changeDisplay(type, options, i, optionB, null, multipleAnswers, "optionB");
                    } else {
                        changeDisplay(type, options, i, null, optionBRadio, multipleAnswers, "");
                    }
                    break;
                case 2:
                    if (type.equals("multiple")) {
                        changeDisplay(type, options, i, optionC, null, multipleAnswers, "optionC");
                    } else {
                        changeDisplay(type, options, i, null, optionCRadio, multipleAnswers, "");
                    }
                    break;
                case 3:
                    if (type.equals("multiple")) {
                        changeDisplay(type, options, i, optionD, null, multipleAnswers, "optionD");
                    } else {
                        changeDisplay(type, options, i, null, optionDRadio, multipleAnswers, "");
                    }
                    break;
                case 4:
                    if (type.equals("multiple")) {
                        changeDisplay(type, options, i, optionE, null, multipleAnswers, "optionE");
                    } else {
                        changeDisplay(type, options, i, null, optionERadio, multipleAnswers, "");
                    }
                    break;
            }
            i++;
        }
    }

    public void changeDisplay(String type, RealmList<SocialCapitalAnswerOptions> options,
                              int index, CheckBox checkBox, RadioButton radioButton,
                              RealmList<MultipleAnswer> multipleAnswers, String btnName) {

        if (type.equals("multiple")) {
            checkboxgroup.setVisibility(View.VISIBLE);
            radiobuttongroup.setVisibility(View.GONE);
            if (options.get(index) == null) {
                checkBox.setVisibility(View.GONE);
            } else {
                for (MultipleAnswer multipleAnswerItr : multipleAnswers) {
                    //Log.e("AFTER ", options.get(index).getId() + " " + multipleAnswerItr.getAnswer());
                    if (options.get(index).getId() == multipleAnswerItr.getAnswer()) {
                        //Log.e("AFTER ", "CLEAR CHANGE DISPLAY TEST");
                        checkBox.setChecked(true);
                        socialCapitalAnswerOptionsesList.add(options.get(index));
                        if (btnName.equals("optionA")) {
                            checkA();
                        } else if (btnName.equals("optionB")) {
                            checkB();
                        } else if (btnName.equals("optionC")) {
                            checkC();
                        } else if (btnName.equals("optionD")) {
                            checkD();
                        } else if (btnName.equals("optionE")) {
                            checkE();
                        }
                    }
                }
                checkBox.setVisibility(View.VISIBLE);
                if (language.equals("हिन्दी") || language.equalsIgnoreCase("Hindi")) {
                    checkBox.setText(options.get(index).getOptionsHindi());
                } else {
                    checkBox.setText(options.get(index).getOptions());
                }
                checkBox.setTag(R.string.checkbox_id, index);
            }
        } else {
            radiobuttongroup.setVisibility(View.VISIBLE);
            checkboxgroup.setVisibility(View.GONE);
            if (options.get(index) == null) {
                radioButton.setVisibility(View.GONE);
            } else {
                for (MultipleAnswer multipleAnswerItr : multipleAnswers) {
                    if (options.get(index).getId() == multipleAnswerItr.getAnswer()) {
                        radioButton.setChecked(true);
                        socialCapitalAnswerOptionsesList.add(options.get(index));
                    }
                }

                radioButton.setVisibility(View.VISIBLE);
                if (language.equals("हिन्दी") || language.equalsIgnoreCase("Hindi")) {
                    radioButton.setText(options.get(index).getOptionsHindi());
                } else {
                    radioButton.setText(options.get(index).getOptions());
                }
                radioButton.setTag(R.string.checkbox_id, index);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.e("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.e("CDA", "onBackPressed Called");
        backButtonAction();
    }

    public void backButtonAction() {
        if (currentQuestionId > 0) {
            int nextQuestionId = currentQuestionId - 1;
            //Log.e("Current id ", nextQuestionId + "");

            socialCapitalAnswerOptionsesList.clear();
            clearOptions();

            loadQuestion(nextQuestionId);
            currentQuestionId = nextQuestionId;
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                backButtonAction();
//                if (currentQuestionId > 0) {
//                    int nextQuestionId = currentQuestionId - 1;
//                    //Log.e("Current id ", nextQuestionId + "");
//
//                    socialCapitalAnswerOptionsesList.clear();
//                    clearOptions();
//
//                    loadQuestion(nextQuestionId);
//                    currentQuestionId = nextQuestionId;
//                } else {
//                    finish();
//                }
                break;
            case R.id.next_button:
                if (socialCapitalAnswerOptionsesList.size() > 0) {
                    if (currentQuestionId < 14) {
                        //Log.e("Current id ", socialCapitalAnswerOptionsesList.size() + "");
                        RealmList<MultipleAnswer> multipleAnswers;
                        multipleAnswers = new RealmList<>();
                        int qNo = landKind.getSocialCapitals().getSocialCapitalAnswers().get(currentQuestionId).getSocialCapitalQuestion().getQuestionno();
                        for (SocialCapitalAnswerOptions socialCapitalAnswerOptions : socialCapitalAnswerOptionsesList) {
                            multipleAnswers.add(addAnswer(socialCapitalAnswerOptions.getId(), qNo));
                        }
                        realm.beginTransaction();
                        landKind.getSocialCapitals().getSocialCapitalAnswers().get(currentQuestionId).setMultipleAnswers(multipleAnswers);
                        realm.commitTransaction();

                        calculateFactorScore(landKind.getSocialCapitals().getSocialCapitalAnswers().get(currentQuestionId));

                        if (currentQuestionId < 13) {
                            socialCapitalAnswerOptionsesList.clear();
                            clearOptions();

                            //Log.e("MULTIPLE ", landKind.getSocialCapitals().getSocialCapitalAnswers().get(currentQuestionId).getMultipleAnswers().toString());


                            int preQuestionId = currentQuestionId + 1;
                            loadQuestion(preQuestionId);

                            currentQuestionId = preQuestionId;
                        } else {
                            Intent intent = new Intent(SocialCapitalActivity.this, NaturalCapitalSurveyStartActivity.class);
                            startActivity(intent);
//                        Intent intent = new Intent(SocialCapitalActivity.this,CertificateActivity.class);
//                        startActivity(intent);
                        }

                    } else {
                        Intent intent = new Intent(SocialCapitalActivity.this, NaturalCapitalSurveyStartActivity.class);
                        startActivity(intent);
//                    Intent intent = new Intent(SocialCapitalActivity.this, CertificateActivity.class);
//                    startActivity(intent);
                    }
                } else {

                    Toast.makeText(context, getResources().getText(R.string.select_option), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.image_view_menu_icon:
                toggleMenuDrawer();
                break;
            case R.id.drawer_close_btn:
                toggleMenuDrawer();
                break;
            case R.id.text_view_about:
                Intent i = new Intent(SocialCapitalActivity.this, AboutActivity.class);
                startActivity(i);
                break;
            case R.id.logout:
                Locale myLocale = new Locale(Locale.getDefault().getDisplayLanguage());
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);

                Intent intent = new Intent(SocialCapitalActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.text_start_survey:
                Intent intents = new Intent(getApplicationContext(), MainActivity.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intents);
                break;
            case R.id.text_harvesting_forest_products:
                setCurrentSocialCapitalSurvey(getString(R.string.string_forestland));
                startLandTypeActivity();
                break;
            case R.id.text_agriculture:
                setCurrentSocialCapitalSurvey(getString(R.string.string_cropland));
                startLandTypeActivity();
                break;
            case R.id.text_grazing:
                setCurrentSocialCapitalSurvey(getString(R.string.string_pastureland));
                startLandTypeActivity();
                break;
            case R.id.text_mining:
                setCurrentSocialCapitalSurvey(getString(R.string.string_miningland));
                startLandTypeActivity();
                break;
            case R.id.text_shared_costs_outlays:
                Intent intent_outlay = new Intent(getApplicationContext(), SharedCostSurveyStartActivity.class);
                startActivity(intent_outlay);
                break;
            case R.id.text_certificate:
                Intent intent_certificate = new Intent(getApplicationContext(), NewCertificateActivity.class);
                startActivity(intent_certificate);
                break;

        }
    }

    private void setNav() {
        harvestingForestProducts.setVisibility(View.GONE);
        agriculture.setVisibility(View.GONE);
        grazing.setVisibility(View.GONE);
        mining.setVisibility(View.GONE);

        if (checkLandKind(getString(R.string.string_forestland))) {
            harvestingForestProducts.setVisibility(View.VISIBLE);
        }

        if (checkLandKind(getString(R.string.string_cropland))) {
            agriculture.setVisibility(View.VISIBLE);
        }

        if (checkLandKind(getString(R.string.string_pastureland))) {
            grazing.setVisibility(View.VISIBLE);
        }

        if (checkLandKind(getString(R.string.string_miningland))) {
            mining.setVisibility(View.VISIBLE);
        }
    }

    private void startLandTypeActivity() {
        Intent intent = new Intent(SocialCapitalActivity.this, StartLandTypeActivity.class);
        startActivity(intent);
    }

    private void setCurrentSocialCapitalSurvey(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentSocialCapitalSurvey", name);
        editor.apply();
    }

    private boolean checkLandKind(String name) {
        LandKind landKind = realm.where(LandKind.class)
                .equalTo("name", name)
                .equalTo("surveyId", surveyId)
                .findFirst();
        if (landKind.getStatus().equals("active")) {
            return true;
        } else {
            return false;
        }
    }

    public void calculateFactorScore(SocialCapitalAnswer socialCapitalAnswer) {
        double factoreScore = 0;
        SocialCapitalQuestions socialCapitalQuestionsCalculate = socialCapitalAnswer.getSocialCapitalQuestion();
        int questionWt = socialCapitalQuestionsCalculate.getWeight();

        for (MultipleAnswer multipleAnswer : socialCapitalAnswer.getMultipleAnswers()) {
            for (SocialCapitalAnswerOptions socialCapitalAnswerOptions : socialCapitalQuestionsCalculate.getSocialCapitalAnswerOptionses()) {
                if (multipleAnswer.getAnswer() == socialCapitalAnswerOptions.getId()) {
                    socialCapitalAnswerOptions.getVal();
                    Log.e("Q wt ", socialCapitalAnswerOptions.getVal() + " " + questionWt);
                    Log.e("Q wt ", socialCapitalAnswerOptions.getVal() + " " + (double) questionWt / 100);
                    Log.e("SCORE ", (socialCapitalAnswerOptions.getVal() * questionWt) / 100 + "");
                    factoreScore = factoreScore + (socialCapitalAnswerOptions.getVal() * (double) questionWt) / 100;
                }
            }
        }
        Log.e("SCORE ", factoreScore + "");

        realm.beginTransaction();
        socialCapitalAnswer.setFactorScore(factoreScore);
        realm.commitTransaction();

        saveSocialCapitalValues();
    }

    public void saveSocialCapitalValues() {
        LandKind landKindLoad = realm.where(LandKind.class)
                .equalTo("surveyId", surveyId)
                .equalTo("status", "active")
                .equalTo("name", currentSocialCapitalServey)
                .findFirst();
        Survey survey = realm.where(Survey.class)
                .equalTo("surveyId", surveyId)
                .findFirst();
        double totalFactorScore = 0.0;
        RealmList<SocialCapitalAnswer> socialCapitalAnswers = landKindLoad.getSocialCapitals().getSocialCapitalAnswers();
        for (SocialCapitalAnswer socialCapitalAnswer : socialCapitalAnswers) {
            totalFactorScore = totalFactorScore + socialCapitalAnswer.getFactorScore();
        }

        Log.e("Factores Score", totalFactorScore + "");
        SpredTable spredTable;
        if (totalFactorScore == 20.0) {
            spredTable = realm.where(SpredTable.class)
                    .equalTo("lessThan", totalFactorScore)
                    .findFirst();
        } else if (totalFactorScore == 0.0) {
            spredTable = realm.where(SpredTable.class)
                    .equalTo("moreThan", totalFactorScore)
                    .findFirst();
        } else {
            spredTable = realm.where(SpredTable.class)
                    .lessThanOrEqualTo("moreThan", totalFactorScore)
                    .greaterThan("lessThan", totalFactorScore)
                    .findFirst();
        }
        double sov = survey.getOverRideRiskRate() ==  0 ? survey.getRiskRate() : survey.getOverRideRiskRate();
        Log.e("Rating ", spredTable.getRating());
        Log.e("Sovereign ", sov + "");
        Log.e("Spred ", spredTable.getSpread() + "");
        Log.e("Discount rate ", spredTable.getSpread() + sov + "");

        realm.beginTransaction();
        landKindLoad.getSocialCapitals().setScore(totalFactorScore);
        landKindLoad.getSocialCapitals().setSovereign(sov);
        landKindLoad.getSocialCapitals().setSpread(spredTable.getSpread());
        landKindLoad.getSocialCapitals().setRating(spredTable.getRating());
        landKindLoad.getSocialCapitals().setDiscountRate(spredTable.getSpread() + sov);
        realm.commitTransaction();
        Log.e("SO ", landKindLoad.toString());
    }

    public void clearOptions() {
        optionA.setChecked(false);
        optionB.setChecked(false);
        optionC.setChecked(false);
        optionD.setChecked(false);
        optionE.setChecked(false);

        radiobuttongroup.clearCheck();

//        optionARadio.setChecked(false);
//        optionBRadio.setChecked(false);
//        optionCRadio.setChecked(false);
//        optionDRadio.setChecked(false);
//        optionERadio.setChecked(false);
    }

    public MultipleAnswer addAnswer(long answerId, int qNo) {
        SocialCapitalAnswerOptions socialCapitalAnswerOptions = realm.where(SocialCapitalAnswerOptions.class)
                .equalTo("id", answerId)
                .findFirst();


        realm.beginTransaction();
        MultipleAnswer multipleAnswer = realm.createObject(MultipleAnswer.class);
        multipleAnswer.setId(getNextKeyMultipleAnswer());
        multipleAnswer.setAnswerValue(socialCapitalAnswerOptions.getVal());
        multipleAnswer.setAnswer((int) answerId);
        multipleAnswer.setQuestionNo(qNo);
        realm.commitTransaction();
        return multipleAnswer;
    }

    public void slectedItems(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        socialCapitalQuestionsSelectedItem = realm.where(LandKind.class)
                .equalTo("surveyId", surveyId)
                .equalTo("status", "active")
                .equalTo("name", currentSocialCapitalServey)
                .findFirst()
                .getSocialCapitals()
                .getSocialCapitalAnswers()
                .get(currentQuestionId)
                .getSocialCapitalQuestion();
        switch (view.getId()) {
            case R.id.option_a:
                checkA();
                checkBoxChecked(checked, optionA);
//                Log.e("PP ", optionA.getTag(R.string.checkbox_id).toString());
//                for (SocialCapitalAnswerOptions socialCapitalAnswerOptions : socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses()) {
//                    Log.e("ZZ ", socialCapitalAnswerOptions.toString());
//                }
//                if (checked) {
//                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionA.getTag(R.string.checkbox_id).toString())));
//                } else {
//                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionA.getTag(R.string.checkbox_id).toString())));
//                }
                break;
            case R.id.option_b:
                checkB();
                checkBoxChecked(checked, optionB);
//                Log.e("PP ", optionB.getTag(R.string.checkbox_id).toString());
//                if (checked) {
//                    socialCapitalAnswerOptionsesList.add(
//                            socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(
//                                    Integer.parseInt(
//                                            optionB.getTag(R.string.checkbox_id).toString())));
//                } else {
//                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionB.getTag(R.string.checkbox_id).toString())));
//                }
                break;
            case R.id.option_c:
                checkC();
                checkBoxChecked(checked, optionC);
//                Log.e("PP ", optionC.getTag(R.string.checkbox_id).toString());
//                if (checked) {
//                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionC.getTag(R.string.checkbox_id).toString())));
//                } else {
//                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionC.getTag(R.string.checkbox_id).toString())));
//                }
                break;
            case R.id.option_d:
                checkD();
                checkBoxChecked(checked, optionD);
//                Log.e("PP ", optionD.getTag(R.string.checkbox_id).toString());
//                if (checked) {
//                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionD.getTag(R.string.checkbox_id).toString())));
//                } else {
//                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionD.getTag(R.string.checkbox_id).toString())));
//                }
                break;
            case R.id.option_e:
                checkE();
                checkBoxChecked(checked, optionE);
//                Log.e("PP ", optionE.getTag(R.string.checkbox_id).toString());
//                if (checked) {
//                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionE.getTag(R.string.checkbox_id).toString())));
//                } else {
//                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionE.getTag(R.string.checkbox_id).toString())));
//                }
                break;
        }
    }

    public void checkBoxChecked(boolean checked, CheckBox checkBox) {
        if (checked) {
            socialCapitalAnswerOptionsesList.clear();
            socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(checkBox.getTag(R.string.checkbox_id).toString())));
        } else {
            uncheckAll();
            //socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(checkBox.getTag(R.string.checkbox_id).toString())));
        }
    }

    public void uncheckAll() {
        optionA.setChecked(false);
        optionB.setChecked(false);
        optionC.setChecked(false);
        optionD.setChecked(false);
        optionE.setChecked(false);
        socialCapitalAnswerOptionsesList.clear();
    }

    public void checkA() {
        optionA.setChecked(true);
    }

    public void checkB() {
        optionA.setChecked(true);
        optionB.setChecked(true);
    }

    public void checkC() {
        optionA.setChecked(true);
        optionB.setChecked(true);
        optionC.setChecked(true);
    }

    public void checkD() {
        optionA.setChecked(true);
        optionB.setChecked(true);
        optionC.setChecked(true);
        optionD.setChecked(true);
    }

    public void checkE() {
        optionA.setChecked(true);
        optionB.setChecked(true);
        optionC.setChecked(true);
        optionD.setChecked(true);
        optionE.setChecked(true);
    }

    public void slectedItemsRadio(View view) {
        Log.e("SELECTED ITEMS ", "HERE");
        socialCapitalAnswerOptionsesList.clear();
        boolean checked = ((RadioButton) view).isChecked();
        socialCapitalQuestionsSelectedItem = realm.where(LandKind.class)
                .equalTo("surveyId", surveyId)
                .equalTo("status", "active")
                .equalTo("name", currentSocialCapitalServey)
                .findFirst()
                .getSocialCapitals()
                .getSocialCapitalAnswers()
                .get(currentQuestionId)
                .getSocialCapitalQuestion();

        Log.e("SELECTED ITEMS ", socialCapitalAnswerOptionsesList.size() + "");
        switch (view.getId()) {
            case R.id.option_a_radio:
                Log.e("PP ", optionARadio.getTag(R.string.checkbox_id).toString());
                for (SocialCapitalAnswerOptions socialCapitalAnswerOptions : socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses()) {
                    Log.e("ZZ ", socialCapitalAnswerOptions.toString());
                }
                if (checked) {
                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionARadio.getTag(R.string.checkbox_id).toString())));
                } else {
                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionARadio.getTag(R.string.checkbox_id).toString())));
                }
                break;
            case R.id.option_b_radio:
                Log.e("PP ", optionBRadio.getTag(R.string.checkbox_id).toString());
                if (checked) {
                    socialCapitalAnswerOptionsesList.add(
                            socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(
                                    Integer.parseInt(
                                            optionBRadio.getTag(R.string.checkbox_id).toString())));
                } else {
                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionBRadio.getTag(R.string.checkbox_id).toString())));
                }
                break;
            case R.id.option_c_radio:
                Log.e("PP ", optionCRadio.getTag(R.string.checkbox_id).toString());
                if (checked) {
                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionCRadio.getTag(R.string.checkbox_id).toString())));
                } else {
                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionCRadio.getTag(R.string.checkbox_id).toString())));
                }
                break;
            case R.id.option_d_radio:
                Log.e("PP ", optionDRadio.getTag(R.string.checkbox_id).toString());
                if (checked) {
                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionDRadio.getTag(R.string.checkbox_id).toString())));
                } else {
                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionDRadio.getTag(R.string.checkbox_id).toString())));
                }
                break;
            case R.id.option_e_radio:
                Log.e("PP ", optionERadio.getTag(R.string.checkbox_id).toString());
                if (checked) {
                    socialCapitalAnswerOptionsesList.add(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionERadio.getTag(R.string.checkbox_id).toString())));
                } else {
                    socialCapitalAnswerOptionsesList.remove(socialCapitalQuestionsSelectedItem.getSocialCapitalAnswerOptionses().get(Integer.parseInt(optionERadio.getTag(R.string.checkbox_id).toString())));
                }
                break;
        }
    }

    public int getNextKeySocialCapitalAnswer() {
        return realm.where(SocialCapitalAnswer.class).max("id").intValue() + 1;
    }

    public int getNextKeyMultipleAnswer() {
        return realm.where(MultipleAnswer.class).max("id").intValue() + 1;
    }


    public void getAllSurvey() {
        Survey survey = realm.where(Survey.class)
                .equalTo("surveyId", surveyId)
                .findFirst();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        socialCapitalQuestionsSelectedItem = realm.where(LandKind.class)
//                .equalTo("surveyId", surveyId)
//                .equalTo("status", "active")
//                .equalTo("name", currentSocialCapitalSurvey)
//                .findFirst()
//                .getSocialCapitals()
//                .getSocialCapitalAnswers()
//                .get(currentQuestionId)
//                .getSocialCapitalQuestion();
//        switch(checkedId){
//            case R.id.option_a_radio:
//                socialCapitalAnswerOptionsesList.clear();
//                socialCapitalAnswerOptionsesList.add(
//                        socialCapitalQuestionsSelectedItem
//                                .getSocialCapitalAnswerOptionses()
//                                .get(Integer.parseInt(
//                                        optionARadio.getTag(R.string.checkbox_id).toString())));
//                break;
//            case R.id.option_b_radio:
//                socialCapitalAnswerOptionsesList.clear();
//                socialCapitalAnswerOptionsesList.add(
//                        socialCapitalQuestionsSelectedItem
//                                .getSocialCapitalAnswerOptionses()
//                                .get(Integer.parseInt(
//                                        optionBRadio.getTag(R.string.checkbox_id).toString())));
//                break;
//            case R.id.option_c_radio:
//                socialCapitalAnswerOptionsesList.clear();
//                socialCapitalAnswerOptionsesList.add(
//                        socialCapitalQuestionsSelectedItem
//                                .getSocialCapitalAnswerOptionses()
//                                .get(Integer.parseInt(
//                                        optionCRadio.getTag(R.string.checkbox_id).toString())));
//                break;
//            case R.id.option_d_radio:
//                socialCapitalAnswerOptionsesList.clear();
//                socialCapitalAnswerOptionsesList.add(
//                        socialCapitalQuestionsSelectedItem
//                                .getSocialCapitalAnswerOptionses()
//                                .get(Integer.parseInt(
//                                        optionDRadio.getTag(R.string.checkbox_id).toString())));
//                break;
//            case R.id.option_e_radio:
//                socialCapitalAnswerOptionsesList.clear();
//                socialCapitalAnswerOptionsesList.add(
//                        socialCapitalQuestionsSelectedItem
//                                .getSocialCapitalAnswerOptionses()
//                                .get(Integer.parseInt(
//                                        optionCRadio.getTag(R.string.checkbox_id).toString())));
//                break;
//        }
    }

    public void toggleMenuDrawer() {
        if (menuDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            menuDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            menuDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}