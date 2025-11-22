package com.example.smartair;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class AddChildViewModel extends ViewModel {
    private final UserManager userManager = new UserManager();
    private final FirebaseAuth secondaryAuth;
    private final DataManager dataManager = new DataManager();
    private final String TAG = "Add Child Account";

    /// livedata copypaste
//    private final MutableLiveData<> _name
//            = new MutableLiveData<>();
//    public LiveData<> name = _name;

    private final MutableLiveData<String> _usernameError
            = new MutableLiveData<>();
    public LiveData<String> usernameError = _usernameError;
    private final MutableLiveData<Boolean> _usernameValidity
            = new MutableLiveData<>();
    public LiveData<Boolean> usernameValidity = _usernameValidity;

    private final MutableLiveData<String> _passwordError
            = new MutableLiveData<String>();
    public LiveData<String> passwordError = _passwordError;

    private final MutableLiveData<String> _firstNameError
            = new MutableLiveData<String>();
    public LiveData<String> firstNameError = _firstNameError;

    private final MutableLiveData<String> _middleNameError
            = new MutableLiveData<String>();
    public LiveData<String> middleNameError = _middleNameError;

    private final MutableLiveData<String> _lastNameError
            = new MutableLiveData<String>();
    public LiveData<String> lastNameError = _lastNameError;

    private final MutableLiveData<String> _parentPasswordError
            = new MutableLiveData<String>();
    public LiveData<String> parentPasswordError = _parentPasswordError;
    private final MutableLiveData<Boolean> _parentPasswordValidity
            = new MutableLiveData<>();
    public LiveData<Boolean> parentPasswordValidity = _parentPasswordValidity;

    private final MutableLiveData<String> _createChildResult
            = new MutableLiveData<String>();
    public LiveData<String> createChildResult = _createChildResult;

    private final MediatorLiveData<Boolean> _formValidity
            = new MediatorLiveData<Boolean>(false);
    public LiveData<Boolean> formValidity = _formValidity;

    public AddChildViewModel(Context context) {
        AuthProvider provider = new AuthProvider(context);
        secondaryAuth = provider.getAuthInstance();

        _formValidity.addSource(usernameError, e -> checkValidity());
        _formValidity.addSource(usernameValidity, e -> checkValidity());
        _formValidity.addSource(passwordError, e -> checkValidity());
        _formValidity.addSource(firstNameError, e -> checkValidity());
        _formValidity.addSource(middleNameError, e -> checkValidity());
        _formValidity.addSource(lastNameError, e -> checkValidity());
        _formValidity.addSource(parentPasswordError, e -> checkValidity());
        _formValidity.addSource(parentPasswordValidity, e -> checkValidity());
        _formValidity.setValue(false);
    }

    public ArrayList<String> getYearRange() {

        ArrayList<String> yearList = new ArrayList<String>();

        for(int i = AppConstants.YEARSTART; i <= AppConstants.YEAREND; i++) {
            yearList.add(String.valueOf(i));
        }

        return yearList;
    }

    public ArrayList<String> getMonthRange() {

        ArrayList<String> monthList  = new ArrayList<String>();

        for(int i = 1; i <= 12; i ++) {
            if (i < 10) {
                monthList.add(0 + String.valueOf(i));
            } else {
                monthList.add(String.valueOf(i));
            }
        }

        return monthList;

    }

    public ArrayList<String> getDayRange(String year, String month) {

        ArrayList<String> dayList = new ArrayList<String>();

        switch (month) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                for(int i = 1; i <= 31; i++) {
                    if (i < 10) {
                        dayList.add(0 + String.valueOf(i));
                    } else {
                        dayList.add(String.valueOf(i));
                    }
                }
                break;

            case "04":
            case "06":
            case "09":
            case "11":
                for(int i = 1; i <= 30; i++) {
                    if (i < 10) {
                        dayList.add(0 + String.valueOf(i));
                    } else {
                        dayList.add(String.valueOf(i));
                    }
                }
                break;

            case "02":
                if(isLeapYear(year)) {
                    for(int i = 1; i <= 29; i++) {
                        if (i < 10) {
                            dayList.add(0 + String.valueOf(i));
                        } else {
                            dayList.add(String.valueOf(i));
                        }
                    }
                } else {
                    for(int i = 1; i <= 28; i++) {
                        if (i < 10) {
                            dayList.add(0 + String.valueOf(i));
                        } else {
                            dayList.add(String.valueOf(i));
                        }
                    }
                }
                break;
        }

        return dayList;

    }

    public int getMaxDayIndex(String year, String month) {
        int maxDayIndex = 0;

        switch (month) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                maxDayIndex = 30;
                break;

            case "04":
            case "06":
            case "09":
            case "11":
                maxDayIndex = 29;
                break;

            case "02":
                if(isLeapYear(year)) {
                    maxDayIndex = 28;
                } else {
                    maxDayIndex = 27;
                }
                break;
        }

        return maxDayIndex;
    }

    public boolean isLeapYear(String year) {
        int temp = Integer.parseInt(year);
        return (temp % 4 == 0 && temp % 100 != 0) || (temp % 400 == 0);
    }

    public void createChildRequest(String username, String password,
                            String firstName, String middleName, String lastName,
                            String parentPassword) {

        validateUsername(username);
        validatePassword(password);
        validateNameFormat(_firstNameError, firstName, true);
        validateNameFormat(_middleNameError, middleName, false);
        validateNameFormat(_lastNameError, lastName, true);
        validateParentPassword(parentPassword);

    }

    public void createChild(String username, String password,
                            String firstName, String middleName, String lastName,
                            String birthday, String sex,
                            String parentPassword) {

        String parentUid = userManager.getCurrentUser().getUid();
        String childSynthEmail = getSyntheticEmail(username);

        secondaryAuth.createUserWithEmailAndPassword(childSynthEmail, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createChildWithUsernameAndPassword: SUCCESS");

                                FirebaseUser childUser = secondaryAuth.getCurrentUser();
                                String childUid = childUser.getUid();

                                DatabaseReference childUserRef
                                        = dataManager.getReference(AppConstants.USERPATH)
                                        .child(childUid);

                                dataManager.setupChild(childUserRef, AppConstants.CHILD, parentUid,
                                        childUid, username, childSynthEmail,
                                        getTitleCase(firstName), getTitleCase(middleName),
                                        getTitleCase(lastName), birthday, sex.toLowerCase());

                                secondaryAuth.signOut();

                                _createChildResult.setValue(AppConstants.SUCCESS);

                            } else {

                                Log.i(TAG, "createChildWithUsernameAndPassword: FAIL");
                                _createChildResult.setValue(AppConstants.FAIL);

                            }
                        });

    }

    private void checkValidity() {
        boolean valid =
                _usernameError.getValue() == null &&
                Boolean.TRUE.equals(_usernameValidity.getValue()) &&
                _passwordError.getValue() == null &&
                _firstNameError.getValue() == null &&
                _middleNameError.getValue() == null &&
                _lastNameError.getValue() == null &&
                _parentPasswordError.getValue() == null &&
                Boolean.TRUE.equals(_parentPasswordValidity.getValue());

        _formValidity.setValue(valid);
    }

    private void validateUsername(String username) {

        if (username.contains("@")) {
            _usernameError.setValue("Username cannot contain '@'");
            _usernameValidity.setValue(false);
            return;
        }

        if (username.contains(" ")) {
            _usernameError.setValue("Username cannot contain spaces");
            _usernameValidity.setValue(false);
            return;
        }

        if (username.length() < 6) {
            _usernameError.setValue("Username must be at least 6 characters");
            _usernameValidity.setValue(false);
            return;
        }

        dataManager.exists(dataManager.getReference(AppConstants.USERNAMEPATH).child(username))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean doesExist = task.getResult();
                        if (doesExist) {
                            _usernameError.setValue("Username taken");
                            _usernameValidity.setValue(false);
                        } else {
                            _usernameError.setValue(null);
                            _usernameValidity.setValue(true);
                        }
                    } else {
                        Log.e(TAG, "Couldn't verify existence of username");
                        _usernameError.setValue("Couldn't verify existence of username");
                        _usernameValidity.setValue(false);
                    }
                });
    }

    private void validatePassword(String password) {

        if (password.length() < 6) {
            Log.i(TAG, "Invalid password entry");
            _passwordError.setValue("Password must be at least 6 characters");
        } else {
            _passwordError.setValue(null);
        }

    }

    private void validateNameFormat(MutableLiveData<String> nameData, String name, boolean required) {

        Pattern nameFilter = Pattern.compile(
                "^(?=.{1,50}$)[A-Za-zÀ-ÖØ-öø-ÿ]+([.' -][A-Za-zÀ-ÖØ-öø-ÿ]+)*[.'-]?$"
        );

        if (required) {
            if(nameFilter.matcher(name).matches()) {
                nameData.setValue(null);
            } else {
                nameData.setValue("Invalid name format");
                Log.i(TAG, "Invalid name format");
            }
        } else {
            if (!name.isEmpty()) {
                if (nameFilter.matcher(name).matches()) {
                    nameData.setValue(null);
                } else {
                    nameData.setValue("Invalid name format");
                    Log.i(TAG, "Invalid name format");
                }
            }
        }

    }

    private void validateParentPassword(String password) {
        userManager.reauthenticate(password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        _parentPasswordError.setValue("Incorrect password");
                        _parentPasswordValidity.setValue(false);
                    } else {
                        _parentPasswordError.setValue(null);
                        _parentPasswordValidity.setValue(true);
                    }
                });
    }

    private String getSyntheticEmail(String username) {
        return username + AppConstants.SYNTH_EMAIL_DOMAIN;
    }

    private String getTitleCase(String string) {

        StringBuilder result = new StringBuilder();

        if (!string.isEmpty()) {
            char[] arr = string.toCharArray();

            result.append(String.valueOf(arr[0]).toUpperCase());

            for(int i = 1; i < string.length(); i ++) {
                result.append(String.valueOf(arr[i]).toLowerCase());
            }
        }

        return result.toString();
    }

}
