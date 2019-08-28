# SMS Forwarder

Auto-forwards text messages aka SMS to destination phone numbers. It allows you to create target numbers to which redirects SMS, blacklist numbers from which SMS will not be forwarded and breaks when SMS will not be forwarded.

The application is very useful for people who have to carry more than one phone devices (e.g. private and business ones) and they don't have dual SIM on those phones. 

## Permissions

* **android.permission.READ_SMS** - mandatory to read an original SMS
* **android.permission.RECEIVE_SMS** - mandatory to receive SMS
* **android.permission.SEND_SMS** - mandatory to auto-forward SMS
* **android.permission.READ_CONTACTS** - optional to read contact's phone number instead typing phone number manually

## Technology Stack

* [Kotlin](https://kotlinlang.org/) - Programming Language
* [Gradle](https://gradle.org/) - Dependency Management
* [Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of Android libraries
* [Coroutines](https://developer.android.com/kotlin/coroutines) - Kotlin Coroutines
* [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) - Navigation components
* [Room](https://developer.android.com/topic/libraries/architecture/room) - Room Persistence Library
* [Material Design](https://material.io/develop/android/) - Android Material Design
* [Text drawable](https://github.com/amulyakhare/TextDrawable) - Android Text Drawable

## User Guide

### Display target phone numbers to which SMS will be auto-forwarded

![Target Phone Numbers](/img/target_phone_numbers.png)

### Add or edit target phone number

![Add/Edit Target Number](/img/enter_target_phone_number.png)

### Define blacklist numbers from those phone numbers will not be auto-forwarded to target numbers

![Blacklist numbers](/img/blacklist_numbers.png)

### Manage time-offs (day of week and start-end time) when SMS will not be auto-forwarded

![Time-offs](/img/time_offs.png)

![Edit Time-Off](/img/edit_time_off.png)

### Application settings

![Settings](/img/settings.png)

### Dark mode

![Dark mode](/img/dark_mode.png)

## License

This project is licensed under the MIT License
