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

public class AddChildViewModel extends ViewModel {
    private final UserManager userManager = new UserManager();
    private final FirebaseAuth secondaryAuth;
    private final DataManager dataManager = new DataManager();
    private final String TAG = "Add Child Account";
    private final EntryValidator entryValidator = new EntryValidator();
    private final StringHelper stringHelper = new StringHelper();

    /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// ///

    private final MutableLiveData<String> _usernameError
            = new MutableLiveData<>();
    public LiveData<String> usernameError = _usernameError;
    private final MutableLiveData<Boolean> _usernameValidity
            = new MutableLiveData<>();
    public LiveData<Boolean> usernameValidity = _usernameValidity;

    private final MutableLiveData<String> _passwordError
            = new MutableLiveData<>();
    public LiveData<String> passwordError = _passwordError;

    private final MutableLiveData<String> _firstNameError
            = new MutableLiveData<>();
    public LiveData<String> firstNameError = _firstNameError;

    private final MutableLiveData<String> _middleNameError
            = new MutableLiveData<>();
    public LiveData<String> middleNameError = _middleNameError;

    private final MutableLiveData<String> _lastNameError
            = new MutableLiveData<>();
    public LiveData<String> lastNameError = _lastNameError;

    private final MutableLiveData<String> _createChildResult
            = new MutableLiveData<>();
    public LiveData<String> createChildResult = _createChildResult;

    private final MediatorLiveData<Boolean> _formValidity
            = new MediatorLiveData<>(false);
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
        _formValidity.setValue(false);
    }

    public void createChildRequest(String username, String password,
                            String firstName, String middleName, String lastName) {

        entryValidator.validateUsername(_usernameError, _usernameValidity, username);
        entryValidator.validatePassword(_passwordError, password);
        entryValidator.validateNameFormat(_firstNameError, firstName, true);
        entryValidator.validateNameFormat(_middleNameError, middleName, false);
        entryValidator.validateNameFormat(_lastNameError, lastName, true);

    }

    public void createChild(String username, String password,
                            String firstName, String middleName, String lastName,
                            String birthday, String sex) {

        String parentUid = userManager.getCurrentUser().getUid();
        String childSynthEmail = stringHelper.getSyntheticEmail(username);

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
                                        stringHelper.toTitleCase(firstName),
                                        stringHelper.toTitleCase(middleName),
                                        stringHelper.toTitleCase(lastName),
                                        birthday, sex.toLowerCase());

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
                _lastNameError.getValue() == null;

        _formValidity.setValue(valid);
    }

}
