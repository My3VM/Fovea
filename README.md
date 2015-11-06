# Fovea
# Fovea Context Engine
The Fovea Context engine adds intelligence to any mobile app! It enables the host mobile app to mine the contextual data from his/her mobile phone, helping to gain meaningful insights, patterns and behaviour about the app user. 

App owners can leverage this engine to derive awesome use cases for his business at various stages. In its current version, Fovea engine aims at delivering the following services: 
- App Virality (referral) as service, which aids app recommendation driven by user to his close circle. When triggered, fovea provides user’s most influential people, for whom app-links are sent via SMS upon his approval.
- Informative dashboards revealing insights about App’s user base - User demography by location, App usage patterns
- Product discovery, driven by app personalisation and recommendation for each user.

All of the above features heavily relies upon the data /insights mined from the Fovea mobile context engine, which involves various context mining operations. Developers have flexibility to subscribe to only select services, for which they are comfortable seeking relevant permissions from users during the app installation.

The Fovea context engine relies on following mining operation for the available services. We recommend developers to enable all these services to benefit from Fovea to the maximum extent. However, engine is configurable to the selected services as specified during the SDK initialisation method.

- App Virality ( Call logs and optionally, Facebook)
- App Usage Dashboards (App usage)
- User demography dashboards (Location intelligence)
- Product discovery ( Configurable with only few services - Call logs, Browser history, Apps usage, Facebook context ) 


### Version
0.2.0
### Prerequisites
To use this SDK, you will need to make sure you've registered your app with [Fovea portal][dill] and have collected your developer keys - App key and Client keys.

## Integration steps
### 1. Install the SDK
The SDK integration is facilitated with simple gradle link to the Maven Jcenter. Please copy the following dependency in your app's gradle file.
```
compile 'com.cogknit.fovea:fovea-context-engine:0.2.0’
```
### 2. Provide permissions

Based on the services selected, special permissions may need to be added to allow user to authorize access to the target data. These tags need to be added as children to the manifest node in the manifest file. Listed below are the permissions required categorized by the services they support. To ensure proper functioning of library, necessary permissions associated to services requested must be added to the Manifest.


```xml
<!-- App Virality -->
<uses-feature android:name="android.hardware.telephony" android:required="false" />
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />

<!-- Apps usage dashboard—>
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
<!--Also add ' xmlns:tools="http://schemas.android.com/tools" ' to the root manifest tag-->

<!-- User demography dashboard-->
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Product discovery—>
<uses-permission android:name="com.android.browser.permission.READ_HISTORY_BOOKMARKS"/>
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
<!--Also add ' xmlns:tools="http://schemas.android.com/tools" ' to the root manifest tag-->
```
### 3. Fovea interface integration

**Initialize the Fovea Context Engine SDK** with the developer keys and selected Context mining services, *set Customer profile details* and *Facebook access token* for the logged in user.

*It is recommended to initialize fovea in your Application subclass or Main activity.
Also it is important to call these methods when there is network connectivity.*

```java
public static void initialize(Context context, String appKey, String clientKey, FoveaConstants.Services[] services, FoveaCallback foveaCallback)
```
```
public static void setCustomerProfileDetails(FoveaUserProfile customerProfile, FoveaCallback callback)
```
```
public static void setFacebookAccessToken(Context context, String token) throws UninitializedException
```
**Invoke App Referral APIs** to *retrieve the target invitees* and *proceed with invites* with selected and denied invites along with the invitation message.
> Developers must make sure to obtain user's consensus and let user choose the invitees as per his/her preferences, before proceeding with the invitation process.

```
public static List<FoveaAppInvitee> getAppInvitees(Context context) throws UninitializedException
```
```
public static void proceedWithAppInvites(Context context, 
List<FoveaAppInvitee> approvedInvitees,
List<FoveaAppInvitee> deniedInvitees,
String inviteMessage) throws     UninitializedException, Fovea.AppInviteException
```

### *Sample usage*
> Fovea engine Initialization

```
import com.cogknit.fovea.Fovea;
import com.cogknit.fovea.FoveaCallback;
import com.cogknit.fovea.FoveaConstants;
import com.cogknit.fovea.FoveaUserProfile;
import com.cogknit.fovea.providers.FoveaAppInvitee;

public class FoveaSDK extends Application {
@Override
public void onCreate() {
super.onCreate();
//Create Array of FoveaConstants.Services to specify the services to be activated
FoveaConstants.Services miningContexts[] = {
FoveaConstants.Services.APP_VIRALITY,
FoveaConstants.Services.APP_USAGE_DASHBOARD,
FoveaConstants.Services.APP_USERLOCATION_DASHBOARD,
FoveaConstants.Services.DISCOVERY
FoveaConstants.Services.FB
};

//1. Call Fovea.initialize() with context, Appkey, Client keys, an array of FoveaConstants.Services and a handler callback
Fovea.initialize(getApplicationContext(),"**App key goes here**", "**Client key goes here** ", miningContexts, new FoveaCallback() {
@Override
public void onCompletion() {
//Authorization successful. Set user profile and provide Facebook token if needed.
Log.v("Sample", "Successfully authorized");

//Build user profile
FoveaUserProfile profile1 = new FoveaUserProfile("4355690");
profile1.setUserName("John Smith");
profile1.setEmailID("john@gmail.com");
profile1.setDateOfBirth("13/05/2000");
profile1.setPhoneNumber("324-443-4444");
profile1.setGender(FoveaConstants.Gender.Male);

//2. Optional though important - Set customer profile details 
Fovea.setCustomerProfileDetails() with the User object and a handler callback
Fovea.setCustomerProfileDetails(profile1, new FoveaCallback() {
@Override
public void onCompletion() {
//Customer profile set successfully
}
@Override
public void onFailure(Exception exception) {
//Error setting user profile
Log.v("Sample", exception.getLocalizedMessage());
}
});
}
@Override
public void onFailure(Exception exception) {
//Error auth
Log.v("Sample", exception.getLocalizedMessage());
}
});

//3. SetFacebook Access token, re-initialize upon every FB login refresh
try
{
Fovea.setFacebookAccessToken(this, "**Facebook access token of the logged in user**");
}
catch (Fovea.UninitializedException exception)
{
Log.v("Sample", exception.getLocalizedMessage());
}
}
}
```

> Fovea App virality API integration


```
//1. Get the App invitees
List<FoveaAppInvitee> invites = null;
try{
invites = Fovea.getAppInvitees(this);
Log.v("Sample", "Recommended invitees" + invites);
}
catch (Fovea.UninitializedException exception) {
Log.v("Sample", exception.getLocalizedMessage());
}

//2. After getting app user's consensus, proceed with sending message with selected invitees; also provide with the denied contacts.
try{
if ((invites == null)||(invites.isEmpty())) {
return;
}
//Mock user response
List<FoveaAppInvitee> approvedList = invites.subList(0, 2);
List<FoveaAppInvitee> declinedList = invites.subList(2, 4);

Fovea.proceedWithAppInvites(this, approvedList, declinedList, "You'll love this cool app, powered by Fovea! Checkout https://example.com/getapp");
}
catch (Fovea.UninitializedException exception) {
Log.v("Sample", exception.getLocalizedMessage());
}
catch (Fovea.AppInviteException exception) {
Log.v("Sample", exception.getLocalizedMessage());
}
```

[dill]: <http://www.cogknit.com/fovea>