package com.example.smartair;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterViewModel extends ViewModel {

    private final UserManager userManager = new UserManager();
    private final DataManager dataManager = new DataManager();
    private final EntryValidator validator = new EntryValidator();
    private final String TAG = "User Registration";

    private final MutableLiveData<String> _firstNameError
            = new MutableLiveData<>();
    public LiveData<String> firstNameError = _firstNameError;

    private final MutableLiveData<String> _middleNameError
            = new MutableLiveData<>();
    public LiveData<String> middleNameError = _middleNameError;

    private final MutableLiveData<String> _lastNameError
            = new MutableLiveData<>();
    public LiveData<String> lastNameError = _lastNameError;

    private final MutableLiveData<String> _emailError
            = new MutableLiveData<String>();
    public LiveData<String> emailError = _emailError;

    private final MutableLiveData<String> _passwordError
            = new MutableLiveData<String>();
    public LiveData<String> passwordError = _passwordError;

    private final MutableLiveData<String> _passwordConfirmationError
            = new MutableLiveData<String>();
    public LiveData<String> passwordConfirmationError = _passwordConfirmationError;

    private final MutableLiveData<String> _sendEmailVerificationResult
            = new MutableLiveData<String>();
    public LiveData<String> sendEmailVerificationResult = _sendEmailVerificationResult;

    private final MutableLiveData<String> _userEmail
            = new MutableLiveData<String>();
    public LiveData<String> userEmail = _userEmail;

    private final MutableLiveData<Boolean> _registrationSuccess
            = new MutableLiveData<>();
    public MutableLiveData<Boolean> registrationSuccess = _registrationSuccess;


    public void requestRegistration(String firstName, String middleName, String lastname,
                                    String email, String password, String passwordConfirmation,
                                    String accountType) {
        Log.d(TAG, "requestRegistrationEvent");

        validator.validateNameFormat(_firstNameError, firstName, true);
        validator.validateNameFormat(_middleNameError, middleName, false);
        validator.validateNameFormat(_lastNameError, lastname, true);
        validator.validateEmail(_emailError, email);
        validator.validatePassword(_passwordError, password);
        validator.validatePasswordConfirmation(_passwordConfirmationError,
                password, passwordConfirmation);

        if(isValidEntries()) {
            performRegistration(firstName, middleName, lastname, email, password, accountType);
        }
    }

    private boolean isValidEntries() {

        Log.d(TAG, "entryValidationEvent");

        return firstNameError.getValue() == null
                && middleNameError.getValue() == null
                && lastNameError.getValue() == null
                && emailError.getValue() == null
                && passwordError.getValue() == null
                && passwordConfirmationError.getValue() == null;
    }

    private void performRegistration(String firstName, String middleName, String lastName,
                                     String email, String password, String accountType) {
        Log.d(TAG, "createUser: " + email);

        userManager.register(email, password).addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               Log.d(TAG, "createUserWithEmailAndPassword: SUCCESS");

               FirebaseUser user = userManager.getCurrentUser();

               userManager.sendEmailVerification(user)
                       .addOnCompleteListener(sendEmail -> {

                   if(sendEmail.isSuccessful()) {
                       Log.d(TAG, "sendEmailVerification: SUCCESS");

                       _userEmail.setValue(email);
                       _sendEmailVerificationResult.setValue(AppConstants.SUCCESS);

                       DatabaseReference userReference
                               = dataManager
                                    .getReference(AppConstants.USERPATH).child(user.getUid());

                       dataManager.setupUser(userReference, email, accountType,
                                                firstName, middleName, lastName);

                       _registrationSuccess.setValue(true);
                   } else {
                        dataManager.deleteUserData(user.getUid());
                        userManager.delete();
                        _userEmail.setValue(null);
                        _sendEmailVerificationResult.setValue(AppConstants.FAIL);
                       _registrationSuccess.setValue(false);
                   }

                   userManager.logout();

               });

           } else {
                Log.d(TAG, "createUserWithEmailAndPassword: FAIL", task.getException());
                _registrationSuccess.setValue(false);
           }
        });
    }

}
